package com.pangdata.sdk.mqtt.client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
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
import com.pangdata.sdk.mqtt.SubscriberListener;
import com.pangdata.sdk.mqtt.TopicUtils;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.mqtt.connector.BrokerFailoverConnector;
import com.pangdata.sdk.util.DevicenameUtils;
import com.pangdata.sdk.util.JsonUtils;

public class PangMqttClient extends AbstractPang{

  private static final Logger logger = LoggerFactory.getLogger(PangMqttClient.class);

  private static final int DEFAULT_CONTROL_QOS = 2;

  private static final int DEFAULT_DATA_QOS = 0;

  private String controlRequestTopic;

  private String dataPublishRootTopic;

  private String serverURI;

  private BrokerConnector failoverConnector;

  private ConnectionCallback connectionCallback;

  private String charset = "utf8";

  private Map<String, DataSharingCallback> dataSharingCallbacks =
      new ConcurrentHashMap<String, DataSharingCallback>();

  private Map<String, ControlCallback> controlCallbackMap =
      new ConcurrentHashMap<String, ControlCallback>();

  private Map<String, Integer> subscribers = new HashMap<String, Integer>();

  private String username;
  
  private PangMqttClientCallback mqttCallback;

  private boolean sendable = true;

  private Map<String, String> registered = new HashMap<String, String> ();

  public PangMqttClient(String username) throws PangException {
    this(username, "default", Long.toString(System.currentTimeMillis()));
  }

  public PangMqttClient(String username, String userkey) throws PangException {
    this(username, userkey, "default", Long.toString(System.currentTimeMillis()));
  }

    
  public PangMqttClient(String username, String threadName, String clientId) throws PangException {
    this(username, new BrokerFailoverConnector(threadName, clientId));
  }
  
  public PangMqttClient(String username, String passwd, String threadName, String clientId) throws PangException {
    this(username, new BrokerFailoverConnector(threadName, username, passwd, clientId));
  }
  
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

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void setServerURI(String serverURI) {
    this.serverURI = serverURI;
  }
  
  private void createConnector(String address) {
    this.serverURI = address;
    mqttCallback.setControlResponseCallback(new ControlResponseCallback() {

      public void resonse(String topic, String responsedata) {
        MqttMessage message = new MqttMessage();
        message.setQos(DEFAULT_CONTROL_QOS);
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

  public void connect(String address) throws PangException {
    createConnector(address);
    logger.info(String.format("Client(%s) is connecting to Broker(%s)", failoverConnector.getClientId(), serverURI));

    failoverConnector.addSubscribListener(new SubscriberListener() {

      public void subscribeTo(MqttClient activeClient) {

        // Control(action) spec may changed. not QoS level 2. Just 0. Action will be done in async. Not Sync
        /*try {
          activeClient.subscribe(controlRequestTopic, DEFAULT_CONTROL_QOS);
          CUtils.ok(logger, "'{}' has been subscribed to broker(id:{}, url:{}) with QoS level({}) ",
              controlRequestTopic, activeClient.getClientId(), activeClient.getServerURI(),
              DEFAULT_CONTROL_QOS);
        } catch (MqttException e) {
          CUtils.failed(logger, "Error occured to subscribe", e);
        }*/

        Set<Entry<String, Integer>> entrySet = subscribers.entrySet();
        Iterator<Entry<String, Integer>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
          Entry<String, Integer> topic = iterator.next();
          if (!failoverConnector.isAvailable()) {
            logger.error("Broker is not connected.");
            throw new IllegalStateException("Broker is not connected");
          }
          try {
            failoverConnector.subscribe(topic.getKey(), topic.getValue());
          } catch (MqttException e) {
            logger.error("Subscribe has an error", e);
          }
        }
      }
    });

    
    failoverConnector.connect(address);

    /*
     * MqttConnectOptions options = null; try { if (userId != null && clientKey != null) { options =
     * new MqttConnectOptions(); options.setUserName(userId);
     * options.setPassword(clientKey.toCharArray()); 
     * String mqttBrokerURI = null; // mqttBrokerURI = BrokerUrlLookup.getBrokerURI(userId, //
     * clientKey); // options.setServerURIs(new String[] {mqttBrokerURI});
     * mqttClient.connect(options); } else { mqttClient.connect(); }
     * 
     * if (connectionCallback != null) { connectionCallback.onConnectionSuccess(); }
     * 
     * 
     * } catch (MqttException e) { IoterException ioterException = new IoterException(e);
     * logger.error("Connection failed.", ioterException); if (connectionCallback != null) {
     * connectionCallback.onConnectionFailure(ioterException); } throw ioterException; }
     */
  }

  public boolean isConnected() {
    return failoverConnector.isAvailable();
  }

  private boolean sendData(String devicename, String data) {
    if(!isSendable()) {
      return false;
    }

    if(DevicenameUtils.isValid(devicename)) {
      throw new IllegalArgumentException("Devicename({}) is invalid"); 
    }
    registerDevices(devicename);
    
    MqttMessage message = new MqttMessage();
    message.setPayload(data.getBytes(Charset.forName(charset)));
    message.setQos(DEFAULT_DATA_QOS);

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
    if(!isSendable()) {
      return false;
    }

    for(String devicename:obj.keySet()) {
      if(DevicenameUtils.isValid(devicename)) {
        throw new IllegalArgumentException("Devicename({}) is invalid"); 
      }
    }
    String strValues = JsonUtils.convertObjToJsonStr(obj);
    
    MqttMessage message = new MqttMessage();
    message.setPayload(strValues.getBytes(Charset.forName(charset)));
    message.setQos(DEFAULT_DATA_QOS);
    
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

  public boolean isSendable() {
    return sendable;
  }

  public void setSendable(boolean sendable) {
    this.sendable = sendable; 
  }
  
  private void registerDevices(String devicename) {
    if(registered.containsKey(devicename)) {
      return;
    }
  }
}
