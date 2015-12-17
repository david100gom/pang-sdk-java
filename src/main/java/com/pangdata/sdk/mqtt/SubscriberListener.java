package com.pangdata.sdk.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;

public interface SubscriberListener {

  void subscribeTo(MqttClient activeClient);

}
