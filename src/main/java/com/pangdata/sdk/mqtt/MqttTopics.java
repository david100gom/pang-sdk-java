package com.pangdata.sdk.mqtt;


public enum MqttTopics {
  DataPublisher("data/" /* 'data/{username}/{thingid}' */),
  DataSubscriber("data/#"),
  SubscribeSharingDataRequest("request/share/#"),
  SubscribeActionRequest("request/action/#"),
  
  DataShare("share/" /* 'share/{username}/{thingid}' */),
  
  RouteDataPublisher("route-data/"),
  RouteDataSubscriber("route-data/#"),
  RouteDataSubscriberShort("route-data/"),
  
  RouteNotiPublisher("route-noti/"),
  RouteNotiSubscriber("route-noti/#"),
  RouteNotiSubscriberShort("route-noti/"),
  
  SharedDataRelaySubscriber("relay/#"),
  
  ControlRequestPublisher("control-request/" /* 'control/request/{username}/{thingid}' */), 
  
  ControlResponsePublisher("control-response/" /* 'control/response/{username}/{thingid}' */),
  ControlResponseSubscriber("control-response/#");

  
  private String topic;

  MqttTopics(String topic) {
    this.topic = topic;
  }
  
  public String getTopic() {
    return topic;
  }
  
}
