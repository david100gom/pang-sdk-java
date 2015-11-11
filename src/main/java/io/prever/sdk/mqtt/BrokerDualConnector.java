package io.prever.sdk.mqtt;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerDualConnector extends BrokerFailoverConnector {
  private final static Logger logger = LoggerFactory.getLogger(BrokerDualConnector.class);

  public BrokerDualConnector(String threadName, String clientId, MqttCallback callback) {
    super(threadName, clientId);
    setMqttCallback(callback);
  }

  public void run() {
    while (isRunning()) {
      for (MqttClient client : clients) {
        if (!client.isConnected()) {
          try {
            logger.info("Trying to connect ({}@{})",
                client.getClientId(), client.getServerURI());
            client.connect();
            logger.info("Connected ({}@{})",
                client.getClientId(), client.getServerURI());
            subscribe(client);
            onConnectionSuccess();
          } catch (Throwable e) {
            logger.error("Connection error({}@{})",
                client.getClientId(), client.getServerURI());
            onFailure(e);
          }
        }
      }
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ie) {
      }
    }
  }

  private void subscribe(MqttClient client) {
    for (SubscriberListener listener : subscribeListeners) {
      listener.subscribeTo(client);
    }
  }

  public void subscribe(String topic, int qos) throws MqttException {
    for (MqttClient client : clients) {
      if (client.isConnected()) {
        client.subscribe(topic, qos);
        logger.info("Subscribed (topic: {}, {}@{}, qos: {})",
                topic, client.getClientId(), client.getServerURI(), qos);
      }
    }
  }

  public void publish(String topic, MqttMessage message) throws MqttPersistenceException,
      MqttException {
    for (MqttClient client : clients) {
      if (client.isConnected()) {
        client.publish(topic, message);
      }
    }
  }

  @Override
  public boolean isAvailable() {
    for (MqttClient client : clients) {
      if (client.isConnected()) {
        return true;
      }
    }
    return false;
  }

  public void unsubscribe(String topic) throws MqttException {
    for (MqttClient client : clients) {
      if (client.isConnected()) {
        client.unsubscribe(topic);
      }
    }
  }
}
