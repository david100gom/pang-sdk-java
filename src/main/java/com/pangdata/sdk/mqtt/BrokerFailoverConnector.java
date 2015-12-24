package com.pangdata.sdk.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerFailoverConnector extends Thread implements BrokerConnector {
  private final static Logger logger = LoggerFactory.getLogger(BrokerFailoverConnector.class);

  private Object waitor = new Object();

  private int active = 0;

  private String[] brokerAddresses;

  private String clientId;

  private MqttCallback mqttCallback;

  protected MqttClient[] clients;

  protected boolean alive;

  protected List<ConnectionCallback> connectionCallbacks = new ArrayList<ConnectionCallback>();

  protected List<SubscriberListener> subscribeListeners = new ArrayList<SubscriberListener>();

  private String username;

  private String passwd;

  public BrokerFailoverConnector(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public BrokerFailoverConnector(String threadName, String username, String passwd, String clientId) {
    super(threadName + "-fo-conn");
    setDaemon(true);
    this.clientId = clientId;
    this.username = username;
    this.passwd = passwd;
  }

  private void init(String addresses) {
    if (mqttCallback == null) {
      throw new IllegalArgumentException("MqttCallback must not be null");
    }
    brokerAddresses = addresses.split(",");
    clients = new MqttClient[brokerAddresses.length];
    for (int i = 0; i < brokerAddresses.length; i++) {
      try {
        String id = clientId + "-" + i;
        logger.info("Creating client({}@{})", id,
            brokerAddresses[i]);
        clients[i] = new MqttClient(brokerAddresses[i], id, new MemoryPersistence());
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
      try {
        logger.info("Trying to connect({}@{})",
            clients[active].getClientId(), clients[active].getServerURI());
        if(username != null && passwd != null) {
          MqttConnectOptions opt = new MqttConnectOptions();
          opt.setUserName(username);
          opt.setPassword(passwd.toCharArray());
          clients[active].connect(opt);
        } else {
          clients[active].connect();
        }
        logger.info("Connected to broker({}@{})",
            clients[active].getClientId(), clients[active].getServerURI());
        subscribe();
        onConnectionSuccess();

        synchronized (waitor) {
          if (!clients[active].isConnected()) {
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
        logger.error("Failed to connect({}@{})",
            clients[active].getClientId(), clients[active].getServerURI());
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

  protected void onFailure(Throwable e) {
    for (ConnectionCallback callback : connectionCallbacks) {
      callback.onFailure(e);
    }
  }

  protected void onConnectionSuccess() {
    for (ConnectionCallback callback : connectionCallbacks) {
      callback.onSuccess();
    }
  }

  private void subscribe() {
    for (SubscriberListener listener : subscribeListeners) {
      listener.subscribeTo(getActive());
    }
  }

  public void addConnectionCallback(ConnectionCallback connectionCallback) {
    this.connectionCallbacks.add(connectionCallback);
  }

  public void addSubscribListener(SubscriberListener subscriberListener) {
    subscribeListeners.add(subscriberListener);
  }

  private MqttClient getActive() {
    return clients[active];
  }

  public boolean isAvailable() {
    return clients != null && clients[active].isConnected();
  }

  public void connect(String address) {
    init(address);
    alive = true;
    super.start();
  }

  protected boolean isRunning() {
    return alive;
  }

  public void subscribe(String topic, int qos) throws MqttException {
    MqttClient active = getActive();
    try {
      active.subscribe(topic, qos);
      logger.error("Subscribed to broker(topic: {}, {}@{}, qos: {})",
          topic, active.getClientId(), active.getServerURI(), qos);
    } catch (MqttException e) {
      throw e;
    }
  }

  public void unsubscribe(String topic) throws MqttException {
    MqttClient client = getActive();
    if (client.isConnected()) {
      client.unsubscribe(topic);
    }
  }

  public void publish(String topic, MqttMessage message) throws MqttPersistenceException,
      MqttException {
    MqttClient client = getActive();
    client.publish(topic, message);
  }

  public void close() {
    logger.info("Closing {}", getName());
    alive = false;
    interrupt();

    if (clients != null) {
      for (MqttClient client : clients) {
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

  public String getClientId() {
    return clientId;
  }

  public void setMqttCallback(MqttCallback callback) {
    this.mqttCallback = callback;
  }
}
