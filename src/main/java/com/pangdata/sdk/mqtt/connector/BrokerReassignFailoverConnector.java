package com.pangdata.sdk.mqtt.connector;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.PangOption;
import com.pangdata.sdk.mqtt.ReassignableBrokerProvider;
import com.pangdata.sdk.mqtt.SubscriberListener;

public class BrokerReassignFailoverConnector extends BrokerParentConnector{
  private final static Logger logger = LoggerFactory.getLogger(BrokerReassignFailoverConnector.class);

  protected MqttClient client;

  protected boolean alive;

  private Object waitor = new Object();
  
  private ReassignableBrokerProvider provier;

  public BrokerReassignFailoverConnector(String threadName, String username, String passwd, String clientId, ReassignableBrokerProvider provier) {
    super(threadName, username, passwd, clientId);
    this.provier = provier;
  }

  private void subscribe() {
    for (SubscriberListener listener : subscribeListeners) {
      listener.subscribeTo(client);
    }
  }

  public boolean isAvailable() {
    return client != null && client.isConnected();
  }

  @Override
  public void run() {
    boolean first = true;
    while (alive) {
      try {        
        if(!first) {
          PangOption option = provier.getAddress();
          logger.info("Reassigned new address: {}", option.getAddresss());
          init(option.getAddresss());
        }
        
        first = false;
        logConnecting(client);
        
        client.connect(getOption());
        
        logConnected(client);
        subscribe();
        onConnectionSuccess();

        synchronized (waitor) {
          if (!client.isConnected()) {
            logger.error("Failed to connect({}@{})",
                client.getClientId(), client.getServerURI());
            continue;
          }
          waitor.wait();
        }        
      } catch (InterruptedException e) {
        logger.info("Closing failover connector");
        return;
      } catch (Throwable e) {
        logConnectionFailed(client, e);
        onFailure(e);
        try {
          TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
        }
      }
    }
  }
  
  public void connect(String address) {
    if (mqttCallback == null) {
      throw new IllegalArgumentException("MqttCallback must not be null");
    }
    
    init(address);
    alive = true;
    super.start();
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  private void init(String address) {    
    try {
      if(client != null && client.isConnected()) {
        client.disconnect();
        client.close();
      }
      
      client = new MqttClient(address, clientId, new MemoryPersistence());
      
      client.setCallback(new MqttCallback() {

        public void messageArrived(String topic, MqttMessage message) throws Exception {
          mqttCallback.messageArrived(topic, message);
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
          mqttCallback.deliveryComplete(token);
        }

        public void connectionLost(Throwable cause) {
          logger.error("Connection lost from broker(id: {}, address: {})",
              client.getClientId(), client.getServerURI(), cause);
          synchronized (waitor) {
            waitor.notifyAll();
          }
          mqttCallback.connectionLost(cause);
        }
      });      
      
    } catch (MqttException e) {
      logger.error("Could not create mqtt client(id: {}, uri: {})", client.getClientId(), client.getServerURI(), e);
      onFailure(e);
    }    
  }

  protected boolean isRunning() {
    return alive;
  }

  public void subscribe(String topic, int qos) throws MqttException {
    try {
      client.subscribe(topic, qos);
      logger.info("Subscribed to broker(topic: {}, {}@{}, qos: {})",
          topic, client.getClientId(), client.getServerURI(), qos);
    } catch (MqttException e) {
      throw e;
    }
  }

  public void unsubscribe(String topic) throws MqttException {
    if (client.isConnected()) {
      client.unsubscribe(topic);
    }
  }

  public void publish(String topic, MqttMessage message) throws MqttPersistenceException,
      MqttException {
    client.publish(topic, message);
  }

  public void close() {
    alive = false;

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

  public String getClientId() {
    return clientId;
  }

}
