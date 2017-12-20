package com.pangdata.sdk.mqtt.client;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.PangException;
import com.pangdata.sdk.mqtt.SubscriberListener;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.mqtt.connector.failover.BrokerFailoverConnectorV1;

public class PangMqttClientV1 extends PangMqttClient{

  private static final Logger logger = LoggerFactory.getLogger(PangMqttClientV1.class);

  public PangMqttClientV1(String username) throws PangException {
    this(username, "default", Long.toString(System.currentTimeMillis()));
  }

  public PangMqttClientV1(String username, String userkey) throws PangException {
    this(username, userkey, "default", Long.toString(System.currentTimeMillis()));
  }

    
  public PangMqttClientV1(String username, String threadName, String clientId) throws PangException {
    this(username, new BrokerFailoverConnectorV1(threadName, clientId));
  }
  
  public PangMqttClientV1(String username, String passwd, String threadName, String clientId) throws PangException {
    this(username, new BrokerFailoverConnectorV1(threadName, username, passwd, clientId));
  }
  
  public PangMqttClientV1(String username, BrokerConnector failoverConnector) throws PangException {
    super(username, failoverConnector);
  }

  public void connect(String address) throws PangException {
	    createConnector(address);
	    logger.info(String.format("Client(%s) is connecting to Broker(%s)", failoverConnector.getClientId(), serverURI));

	    failoverConnector.addSubscribListener(new SubscriberListener<MqttClient>() {

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
}
