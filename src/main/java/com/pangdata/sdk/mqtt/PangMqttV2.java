package com.pangdata.sdk.mqtt;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClientV2;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.mqtt.connector.scale.ScaleBrokerFailoverConnectorV2;

public class PangMqttV2 extends PangMqtt {
  private static final Logger logger = LoggerFactory.getLogger(PangMqttV2.class);
  
  private CountDownLatch cd;

  public PangMqttV2() throws Exception {
    super(true);
  }

  public PangMqttV2(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) throws Exception {
    super(username, userkey, uri, dataSharingCallback);
  }
  
  public PangMqttV2(String username, String userkey) throws Exception {
    this(username, userkey, null);
  }

  public PangMqttV2(String username, String userkey, String uri) throws Exception {
    this(username, userkey, uri, null);
  }

  @Override
  protected Pang createClient(String username, BrokerConnector connector) {
	return new PangMqttClientV2(username, connector);
  }
  
  @Override
  protected BrokerConnector createReassignableConnector(String addresss,
	String username, String passwd, String id,
	DefaultReassignableBrokerProvider defaultReassignableBrokerProvider) {
	return new ScaleBrokerFailoverConnectorV2(addresss, username, passwd,
		        id, new DefaultReassignableBrokerProvider());
  }
}
