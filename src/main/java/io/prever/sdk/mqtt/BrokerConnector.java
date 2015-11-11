package io.prever.sdk.mqtt;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public interface BrokerConnector {
  String getClientId();
  
  void setMqttCallback(MqttCallback callback);
  
  void publish(String topic, MqttMessage message) throws MqttPersistenceException, MqttException;

  void unsubscribe(String topic) throws MqttException;

  void subscribe(String topic, int qos) throws MqttException;

  void connect(String address);

  boolean isAvailable();

  void close();
  
  void addConnectionCallback(ConnectionCallback connectionCallback);
  
  void addSubscribListener(SubscriberListener subscriberListener);
}
