package com.pangdata.sdk.mqtt.connector.failover;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.SubscriberListener;
import com.pangdata.sdk.mqtt.connector.AbstractBrokerConnectorV2;

public class BrokerFailoverConnectorV2 extends AbstractBrokerConnectorV2 {
  private final static Logger logger = LoggerFactory.getLogger(BrokerFailoverConnectorV2.class);
  
  private Object waitor = new Object();

  private int active = 0;

  private String[] brokerAddresses;

  protected IMqttAsyncClient[] clients;

  public BrokerFailoverConnectorV2(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public BrokerFailoverConnectorV2(String threadName, String username, String passwd, String clientId) {
    super(threadName + "-fo-conn", username, passwd, clientId);
  }

  private void init(String addresses) {
    if (mqttCallback == null) {
      throw new IllegalArgumentException("MqttCallback must not be null");
    }
    brokerAddresses = addresses.split(",");
    clients = new MqttAsyncClient[brokerAddresses.length];
    for (int i = 0; i < brokerAddresses.length; i++) {
      try {
        String id = clientId + "-" + i;
        logger.info("Creating client({}@{})", id,
            brokerAddresses[i]);
        clients[i] = new MqttAsyncClient(brokerAddresses[i], id, new MemoryPersistence());
        clients[i].setCallback(new MqttCallback() {

          public void messageArrived(String topic, MqttMessage message) throws Exception {
            mqttCallback.messageArrived(topic, message);
          }

          public void deliveryComplete(IMqttDeliveryToken token) {
            mqttCallback.deliveryComplete(token);
          }

          public void connectionLost(Throwable cause) {
            logger.error("Connection lost from broker(id: {}, address: {})",
                clients[active].getClientId(), clients[active].getServerURI(), cause);
            synchronized (waitor) {
              waitor.notifyAll();
            }
            mqttCallback.connectionLost(cause);
          }
        });
      } catch (MqttException e) {
        logger.error("Could not create mqtt client(id: {}, uri: {})", clients[i].getClientId(), clients[i].getServerURI(), e);
      }
    }
  }

  public void run() {
    while (alive) {
      IMqttAsyncClient mc = clients[active];
      try {
        logConnecting(mc);
        mc.connect(getOption());
        logConnected(mc);
        subscribe();
        onConnectionSuccess();

        synchronized (waitor) {
          if (!mc.isConnected()) {
            logger.error("Connection closed. try again({}@{})",
                clients[active].getClientId(), clients[active].getServerURI());
            continue;
          }
          waitor.wait();
          try {
            TimeUnit.SECONDS.sleep(3);
          } catch (InterruptedException ie) {
          }
          switchTargetBroker();
        }
      } catch (InterruptedException e) {
        logger.info("Closing failover connector");
        return;
      } catch (Throwable e) {
        logConnectionFailed(mc, e);
        onFailure(e);
        switchTargetBroker();
        try {
          TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
        }
      }
    }
  }

  private void switchTargetBroker() {
    if (brokerAddresses.length < 2) {
      return;
    }
    active = active == 0 ? 1 : 0;
  }

  private void subscribe() {
    for (SubscriberListener listener : subscribeListeners) {
      listener.subscribeTo(getActive());
    }
  }

  private IMqttAsyncClient getActive() {
    return clients[active];
  }

  public boolean isAvailable() {
    return clients != null && clients[active].isConnected();
  }

  public void connect(String address) {
    init(address);
    alive = true;
    start();
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected boolean isRunning() {
    return alive;
  }

  public void subscribe(String topic, int qos) throws MqttException {
    IMqttAsyncClient active = getActive();
    try {
      active.subscribe(topic, qos);
      logger.error("Subscribed to broker(topic: {}, {}@{}, qos: {})",
          topic, active.getClientId(), active.getServerURI(), qos);
    } catch (MqttException e) {
      throw e;
    }
  }

  public void unsubscribe(String topic) throws MqttException {
	  IMqttAsyncClient client = getActive();
    if (client.isConnected()) {
      client.unsubscribe(topic);
    }
  }

  public void publish(String topic, MqttMessage message) throws MqttPersistenceException,
      MqttException {
	  IMqttAsyncClient client = getActive();
    client.publish(topic, message);
  }

  public void close() {
    logger.info("Closing {}", getName());
    alive = false;
    interrupt();

    if (clients != null) {
      for (IMqttAsyncClient client : clients) {
        if (client.isConnected()) {
          try {
            client.disconnect();
            client.close();
            logger.info("Broker connection closed(id:{}, uri:{})", client.getClientId(),
                client.getServerURI());
          } catch (MqttException e) {
            logger.warn("FailoverConnector failed to close broker(id:{}, uri:{}) ",
                client.getClientId(), client.getServerURI());
          }
        }
      }
    }
  }
}
