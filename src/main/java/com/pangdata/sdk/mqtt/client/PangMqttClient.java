package com.pangdata.sdk.mqtt.client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.AbstractPang;
import com.pangdata.sdk.PangException;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.ControlResponseCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.MqttTopics;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.util.DevicenameUtils;
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.TopicUtils;

public abstract class PangMqttClient extends AbstractPang{

  private static final Logger logger = LoggerFactory.getLogger(PangMqttClient.class);

  private static final int DEFAULT_CONTROL_QOS = 2;

  private static final int DEFAULT_DATA_QOS = 0;
  
  protected static final int DEFAULT_DATA_QOS1 = 1;

  protected int qos = DEFAULT_DATA_QOS;
  
  private String controlRequestTopic;

  private String dataPublishRootTopic;

  protected String serverURI;

  protected BrokerConnector failoverConnector;

  private ConnectionCallback connectionCallback;

  private Map<String, DataSharingCallback> dataSharingCallbacks =
      new ConcurrentHashMap<String, DataSharingCallback>();

  private Map<String, ControlCallback> controlCallbackMap =
      new ConcurrentHashMap<String, ControlCallback>();

  protected Map<String, Integer> subscribers = new HashMap<String, Integer>();

  private String username;
  
  private PangMqttClientCallback mqttCallback;

  private boolean sendable = true;

  private Map<String, String> registered = new HashMap<String, String> ();

  public PangMqttClient(String username, BrokerConnector failoverConnector) throws PangException {
    super();
    this.failoverConnector = failoverConnector;
    this.username = username;
    
    dataPublishRootTopic = MqttTopics.DataPublisher.getTopic() + username;
    
    controlRequestTopic =
        MqttTopics.ControlRequestPublisher.getTopic() + username
        + MqttTopic.MULTI_LEVEL_WILDCARD_PATTERN;
    
    mqttCallback = new PangMqttClientCallback(username, controlCallbackMap, dataSharingCallbacks);
    failoverConnector.setMqttCallback(mqttCallback);
  }
  
  public void setConnectionCallback(ConnectionCallback connectionCallback) {
    this.connectionCallback = connectionCallback;
    mqttCallback.setConnectionCallback(connectionCallback);
  }

  public String getServerURI() {
    return serverURI;
  }

  public void setServerURI(String serverURI) {
    this.serverURI = serverURI;
  }
  
  protected void createConnector(String address) {
    this.serverURI = address;
    mqttCallback.setControlResponseCallback(new ControlResponseCallback() {

      public void resonse(String topic, String responsedata) {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        // TODO definable charset
        message.setPayload(responsedata.getBytes(Charset.forName("utf-8")));
        try {
          failoverConnector.publish(topic, message);
        } catch (Exception e) {
          throw new PangException(e);
        }
      }
    });

    failoverConnector.addConnectionCallback(new com.pangdata.sdk.mqtt.connector.ConnectionCallback() {
      public void onSuccess() {
        if (connectionCallback != null) {
          connectionCallback.onConnectionSuccess();
        }
      }

      public void onFailure(Throwable e) {
        if (connectionCallback != null) {
          connectionCallback.onConnectionFailure(e);
        }
      }
    });
	}

  public boolean isConnected() {
    return failoverConnector.isAvailable();
  }

  private boolean sendData(String devicename, String data) {
    if(!isValidLicense()) {
      return false;
    }

    if(DevicenameUtils.isInvalid(devicename)) {
      throw new IllegalArgumentException("Devicename({}) is invalid"); 
    }
    registerDevices(devicename);
    
    MqttMessage message = new MqttMessage();
    message.setPayload(data.getBytes(charset));
    message.setQos(qos);

    String sendDataTopic = dataPublishRootTopic + MqttTopic.TOPIC_LEVEL_SEPARATOR + devicename;

    try {
      if (failoverConnector != null && failoverConnector.isAvailable()) {
        failoverConnector.publish(sendDataTopic, message);
      } else {
        logger.warn("MDS is not connected, data sending failed.");
        return false;
      }
    } catch (Exception e) {
      logger.error("Error occured to send data", e);
    }
    return true;
  }
  
  public boolean sendData(String devicename, Object data) {
    return sendData(devicename, String.valueOf(data));
  }
  
  public boolean sendData(Map<String, Object> obj) {
    if(!isValidLicense()) {
      return false;
    }

    for(String devicename:obj.keySet()) {
      if(DevicenameUtils.isInvalid(devicename)) {
        throw new IllegalArgumentException("Devicename("+devicename+") is invalid"); 
      }
    }
    String strValues = JsonUtils.convertObjToJsonStr(obj);
    
    MqttMessage message = new MqttMessage();
    message.setPayload(strValues.getBytes(charset));
    message.setQos(qos);
    
    String sendDataTopic = dataPublishRootTopic;
    
    try {
      if (failoverConnector != null && failoverConnector.isAvailable()) {
        failoverConnector.publish(sendDataTopic, message);
      } else {
        logger.warn("BrokerConnnector is not connected, data sending failed.");
        return false;
      }
    } catch (Exception e) {
      logger.error("Error occured to send data", e);
    }
    return true;
  }
   
  public void disconnect() {
    super.close();

    dataSharingCallbacks.clear();
    controlCallbackMap.clear();

    if (failoverConnector != null) {
      failoverConnector.close();
    }
    logger.info("Ioter client is disconnected.");
  }

  private void subscribe(String topic) {
    try {
      subscribers.put(topic, 0);
      if (failoverConnector.isAvailable()) {
        failoverConnector.subscribe(topic, 0);
      }
    } catch (MqttException e) {
      logger.error("subscribeSharingData MqttException", e);
      throw new PangException(e);
    }
  }

  private void unsubscribe(String topic) {
    try {
      subscribers.remove(topic);
      failoverConnector.unsubscribe(topic);
      logger.info("Unsubscribed a topic(topic: {})", topic);
    } catch (MqttException e) {
      logger.error("subscribeSharingData MqttException", e);
      throw new PangException(e);
    }
  }

  public void subscribeDataSharing(String giverUserId, String thingId,
      DataSharingCallback sharingDataCallback) {
    String mapKey = TopicUtils.getSharingKey(giverUserId, thingId);
    String topic = TopicUtils.getSubscribeDataSharingTopic(giverUserId, thingId);
    subscribe(topic);
    dataSharingCallbacks.put(mapKey, sharingDataCallback);
    logger.info("Subscribed to get shared data(userId: {}, thingId: {})", giverUserId,
        thingId);
  }

  public void unsubscribeDataSharing(String giverUserId, String thingId) {
    String mapKey = TopicUtils.getSharingKey(giverUserId, thingId);
    String topic = TopicUtils.getSubscribeDataSharingTopic(giverUserId, thingId);
    unsubscribe(topic);
    dataSharingCallbacks.remove(mapKey);
  }

  public void subscribeControl(String thingId, ControlCallback controlCallback) {
    String topic = TopicUtils.getSubscribeControlTopic(username, thingId);
    subscribe(topic);
    controlCallbackMap.put(thingId, controlCallback);
  }

  public void unsubscribeControl(String thingId) {
    String topic = TopicUtils.getSubscribeControlTopic(username, thingId);
    unsubscribe(topic);
    controlCallbackMap.remove(thingId);
  }

  public boolean isValidLicense() {
    return sendable;
  }

  public void setValidLicense(boolean sendable) {
    this.sendable = sendable; 
  }
  
  private void registerDevices(String devicename) {
    if(registered.containsKey(devicename)) {
      return;
    }
  }
}
