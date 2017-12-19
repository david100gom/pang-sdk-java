package com.pangdata.sdk.mqtt.connector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.SubscriberListener;
import com.pangdata.sdk.util.PangProperties;

public abstract class AbstractBrokerConnector extends Thread implements BrokerConnector {
  private final static Logger logger = LoggerFactory.getLogger(AbstractBrokerConnector.class);

  protected String clientId;

  protected boolean alive;

  protected List<ConnectionCallback> connectionCallbacks = new ArrayList<ConnectionCallback>();

  protected List<SubscriberListener> subscribeListeners = new ArrayList<SubscriberListener>();

  protected String username;

  protected String passwd;

  protected MqttCallback mqttCallback;

  public AbstractBrokerConnector(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public AbstractBrokerConnector(String threadName, String username, String passwd, String clientId) {
    super(threadName);
    setDaemon(true);
    this.clientId = (String) PangProperties.getProperty("pang.clientId");
    if(this.clientId == null || this.clientId.trim().length() == 0) {
        this.clientId = clientId;
    }
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
}
