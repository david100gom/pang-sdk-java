package com.pangdata.sdk.mqtt.connector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.SubscriberListener;

public abstract class BrokerParentConnector extends Thread implements BrokerConnector {
  private final static Logger logger = LoggerFactory.getLogger(BrokerParentConnector.class);

  protected String clientId;

  protected boolean alive;

  protected List<ConnectionCallback> connectionCallbacks = new ArrayList<ConnectionCallback>();

  protected List<SubscriberListener> subscribeListeners = new ArrayList<SubscriberListener>();

  protected String username;

  protected String passwd;

  protected MqttCallback mqttCallback;

  public BrokerParentConnector(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public BrokerParentConnector(String threadName, String username, String passwd, String clientId) {
    super(threadName + "-fo-conn");
    setDaemon(true);
    this.clientId = clientId;
    this.username = username;
    this.passwd = passwd;
  }

  protected MqttConnectOptions getOption() {
    MqttConnectOptions opt = new MqttConnectOptions();
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

  public void addSubscribListener(SubscriberListener subscriberListener) {
    subscribeListeners.add(subscriberListener);
  }

  protected boolean isRunning() {
    return alive;
  }

  
  public String getClientId() {
    return clientId;
  }
  
  protected void logConnecting(MqttClient client) {
    logger.info("Connecting MDS[auth: {}, id: {}]",
        isAuth(), client.getClientId());
  }
  
  protected void logConnected(MqttClient client) {
    logger.info("Connected MDS[auth: {}, id: {}]",
        isAuth(), client.getClientId());
  }
  
  protected void logConnectionFailed(MqttClient client, Throwable e) {
    logger.error("Connection failed[auth: {}, id: {})",
        isAuth(), client.getClientId(), e);    
  }
}
