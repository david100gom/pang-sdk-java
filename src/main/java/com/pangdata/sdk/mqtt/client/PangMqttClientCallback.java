package com.pangdata.sdk.mqtt.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.PangException;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.ControlResponseCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.MqttTopics;
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.TopicUtils;

public class PangMqttClientCallback implements MqttCallback {
  private static final Logger logger = LoggerFactory.getLogger(PangMqttClientCallback.class);

  private String userId;
  
  private ConnectionCallback connectionCallback;
  
  private ControlResponseCallback controlResponseCallback;
  
  private String controlResponseRootTopic;

  private Map<String, ControlCallback> controlCallbackMap;

  private Map<String, DataSharingCallback> dataSharingCallbacks;
  
  public PangMqttClientCallback(String userId, Map<String, ControlCallback> controlCallbackMap, Map<String, DataSharingCallback> dataSharingCallbacks) {
    this.userId = userId;
    controlResponseRootTopic = MqttTopics.ControlResponsePublisher.getTopic() + userId;
    this.controlCallbackMap = controlCallbackMap;
    this.dataSharingCallbacks = dataSharingCallbacks;
  }

  public void setConnectionCallback(ConnectionCallback connectionCallback) {
    this.connectionCallback = connectionCallback;
  }
  
  public void setControlResponseCallback(ControlResponseCallback controlResponseCallback) {
    this.controlResponseCallback = controlResponseCallback;
  }

  public void messageArrived(String topic, MqttMessage message) throws Exception {
    String data = getData(message);

    if (TopicUtils.isDataShareTopic(topic)) {
      handleSharedData(topic, data);
    } else if (TopicUtils.isControlRequsetTopic(topic)) {
      handleControlResuest(topic, data);
    } else {
      handleUnknownTopic(topic);
    }
  }

  public void deliveryComplete(IMqttDeliveryToken token) {
    String[] topics = token.getTopics();
    //System topic should be null.
    if(topics == null) {
    	return;
    }
    String topic = null;
    String key = null;

    if (topics[0] != null) {
      topic = topics[0];
    }
    if(topic == null) {
      throw new IllegalArgumentException("topic is null");
    }
    if (TopicUtils.isControlRequsetTopic(topic)) {
      key = TopicUtils.getControlKey(topic, userId);
      ControlCallback controlCallback = controlCallbackMap.get(key);

      if (controlCallback != null) {
        String data = null;

        try {
          MqttMessage message = token.getMessage();
          if (message != null) {
            data = getData(message);
          }
        } catch (MqttException e) {
        }

        controlCallback.onDeliveryComplete(topic, data);
      }
    }
  }

  public void connectionLost(Throwable cause) {
    logger.debug("client connection lost", cause);
    if (connectionCallback != null) {
      connectionCallback.onConnectionLost(cause);
    }
  }

  private String getData(MqttMessage message) {
    byte[] payload = message.getPayload();
    String data = null;
    if (payload != null && payload.length > 0) {
      data = new String(payload);
    }
    return data;
  }

  private void handleControlResuest(String topic, String arrivedDataStr) throws MqttException,
      MqttPersistenceException {
    String key = TopicUtils.getControlKey(topic, userId);
    String controlResponseTopic =
        controlResponseRootTopic + MqttTopic.TOPIC_LEVEL_SEPARATOR + key;

    String controlKey = JsonUtils.getValueInJsonStr("controlKey", arrivedDataStr);

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("controlKey", controlKey);

    ControlCallback controlCallback = controlCallbackMap.get(key);
    if (controlCallback != null) {
      String controlData = JsonUtils.getValueInJsonStr("data", arrivedDataStr);
      String controlCallbackResult = controlCallback.execute(controlKey, controlData);
      map.put("data", controlCallbackResult);
    } else {
      map.put("data", "ControlCallback is not set.");
    }

    String responsedata = JsonUtils.convertObjToJsonStr(map);

    logger.debug("control response - topic : {}, data : {} ", controlResponseTopic, responsedata);
    if(controlResponseCallback != null) {
      controlResponseCallback.resonse(topic, responsedata);
    }
  }

  private void handleSharedData(String topic, String sharedData) {
    String key = TopicUtils.getSharingKey(topic);
    DataSharingCallback callback = dataSharingCallbacks.get(key);
    if (callback != null) {
      callback.onSharedDataArrived(sharedData);
    }
  }

  private void handleUnknownTopic(String topic) {
    logger.warn("data type : unknown, topic : {}", topic);
    throw new PangException(new IllegalStateException("Illgal topic message arrived."));
  }
}