package com.pangdata.sdk.mqtt.connector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.SubscriberListenerV2;
import com.pangdata.sdk.util.PangProperties;

public abstract class BrokerParentConnectorV2 extends Thread implements BrokerConnectorV2 {
  private final static Logger logger = LoggerFactory.getLogger(BrokerParentConnectorV2.class);

  protected String clientId;

  protected boolean alive;

  protected List<ConnectionCallback> connectionCallbacks = new ArrayList<ConnectionCallback>();

  protected List<SubscriberListenerV2> subscribeListeners = new ArrayList<SubscriberListenerV2>();

  protected String username;

  protected String passwd;

  protected MqttCallback mqttCallback;
  
  protected boolean buffer;
  
  public BrokerParentConnectorV2(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public BrokerParentConnectorV2(String threadName, String username, String passwd, String clientId) {
    super(threadName + "-fo-conn");
    setDaemon(true);
    this.username = username;
    this.passwd = passwd;

    this.clientId = (String) PangProperties.getProperty("pang.clientId");
    if(this.clientId == null || this.clientId.trim().length() == 0) {
    	this.clientId = clientId;
    }
    
    String buffer = (String) PangProperties.getProperty("pang.buffer");
    if(buffer != null && buffer.trim().equalsIgnoreCase("true")) {
    	this.buffer = true;
    }
  }

  protected MqttConnectOptions getOption() {
	 MqttConnectOptions opt = new MqttConnectOptions();
	 opt.setCleanSession(false);
	 //Only high speed
	 opt.setMaxInflight(1024);
	 // This can not set be true. Because our client has fail over option. So can not use the same address of mqtt server.
	 opt.setAutomaticReconnect(false);
    if(isAuth()) {
      opt.setUserName(username);
      opt.setPassword(passwd.toCharArray());
    } 
    return opt;
  }

  public boolean isAuth() {
    return username != null && passwd != null;
  }
  
  protected void onFailure(Throwable e) {
    for (ConnectionCallback callback : connectionCallbacks) {
      callback.onFailure(e);
    }
  }

  protected void onConnectionSuccess() {
    for (ConnectionCallback callback : connectionCallbacks) {
      callback.onSuccess();
    }
  }
  
  public void setMqttCallback(MqttCallback callback) {
    this.mqttCallback = callback;
  }

  public void addConnectionCallback(ConnectionCallback connectionCallback) {
    this.connectionCallbacks.add(connectionCallback);
  }

  public void addSubscribListener(SubscriberListenerV2 subscriberListener) {
    subscribeListeners.add(subscriberListener);
  }

  protected boolean isRunning() {
    return alive;
  }

  
  public String getClientId() {
    return clientId;
  }
  
  protected void logConnecting(IMqttAsyncClient client) {
    logger.info("Connecting MDS[auth: {}, id: {}]",
        isAuth(), client.getClientId());
  }
  
  protected void logConnected(IMqttAsyncClient client) {
    logger.info("Connected MDS[auth: {}, id: {}]",
        isAuth(), client.getClientId());
  }
  
  protected void logConnectionFailed(IMqttAsyncClient client, Throwable e) {
    logger.error("Connection failed[auth: {}, id: {})",
        isAuth(), client.getClientId(), e);    
  }
}
