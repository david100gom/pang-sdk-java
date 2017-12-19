package com.pangdata.sdk.mqtt.connector;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBrokerConnectorV1 extends AbstractBrokerConnector {
  private final static Logger logger = LoggerFactory.getLogger(AbstractBrokerConnectorV1.class);

  public AbstractBrokerConnectorV1(String threadName, String clientId) {
    this(threadName, null, null, clientId);
  }
  
  public AbstractBrokerConnectorV1(String threadName, String username, String passwd, String clientId) {
    super(threadName, username, passwd, clientId);
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
