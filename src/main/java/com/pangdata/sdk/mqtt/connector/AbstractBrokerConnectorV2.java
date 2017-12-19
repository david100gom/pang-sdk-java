package com.pangdata.sdk.mqtt.connector;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.mqtt.SubscriberListener;
import com.pangdata.sdk.util.PangProperties;

public abstract class AbstractBrokerConnectorV2 extends AbstractBrokerConnector {
  private final static Logger logger = LoggerFactory.getLogger(AbstractBrokerConnectorV2.class);

  protected boolean buffer;
  
  public AbstractBrokerConnectorV2(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public AbstractBrokerConnectorV2(String threadName, String username, String passwd, String clientId) {
    super(threadName, username, passwd, clientId);
    
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
