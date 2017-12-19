package com.pangdata.sdk.mqtt;


public interface SubscriberListener<T> {

  void subscribeTo(T activeClient);

}
