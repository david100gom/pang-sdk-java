/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Preversoft
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.prever.sdk.mqtt;

import org.eclipse.paho.client.mqttv3.MqttTopic;

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
