package com.pangdata.sdk.util;

import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.pangdata.sdk.mqtt.MqttTopics;

public class TopicUtils {

  public static String getSubscribeDataSharingTopic(String giverUserId, String thingId) {
    return MqttTopics.DataShare.getTopic() + giverUserId + MqttTopic.TOPIC_LEVEL_SEPARATOR + thingId;
  }

  public static String getSubscribeControlTopic(String userId, String thingId) {
    return MqttTopics.ControlRequestPublisher.getTopic() + userId + MqttTopic.TOPIC_LEVEL_SEPARATOR + thingId;
  }
  
  public static String getSharingKey(String giverUserId, String thingId) {
    return giverUserId + MqttTopic.TOPIC_LEVEL_SEPARATOR + thingId;
  }

  public static String getSharingKey(String topic) {
    return topic.replace(MqttTopics.DataShare.getTopic(), "");
  }

  public static String getControlKey(String topic, String userId) {
    return topic.replace(MqttTopics.ControlRequestPublisher.getTopic() + userId + MqttTopic.TOPIC_LEVEL_SEPARATOR, "");
  }

  public static boolean isDataShareTopic(String topic) {
    return topic.startsWith(MqttTopics.DataShare.getTopic());
  }

  public static boolean isControlRequsetTopic(String topic) {
    return topic.startsWith(MqttTopics.ControlRequestPublisher.getTopic());
  }

}
