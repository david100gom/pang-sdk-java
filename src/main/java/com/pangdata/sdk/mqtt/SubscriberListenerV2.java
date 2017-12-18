package com.pangdata.sdk.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

public interface SubscriberListenerV2 {

  void subscribeTo(IMqttAsyncClient activeClient);

}
