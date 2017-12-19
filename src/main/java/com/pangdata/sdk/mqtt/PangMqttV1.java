package com.pangdata.sdk.mqtt;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClient;
import com.pangdata.sdk.mqtt.client.PangMqttClientV1;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.mqtt.connector.scale.ScaleBrokerFailoverConnectorV1;

public class PangMqttV1 extends PangMqtt {

  public PangMqttV1() throws Exception {
    super(true);
  }

  public PangMqttV1(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) throws Exception {
    super(username, userkey, uri, dataSharingCallback);
  }
  
  public PangMqttV1(String username, String userkey) throws Exception {
    this(username, userkey, null);
  }

  public PangMqttV1(String username, String userkey, String uri) throws Exception {
    this(username, userkey, uri, null);
  }

  @Override
  protected Pang createClient(String username, BrokerConnector connector) {
	return new PangMqttClientV1(username, connector);
  }

  @Override
  protected BrokerConnector createReassignableConnector(String addresss,
	String username, String passwd, String id,
	DefaultReassignableBrokerProvider defaultReassignableBrokerProvider) {
	return new ScaleBrokerFailoverConnectorV1(addresss, username, passwd,
		        id, new DefaultReassignableBrokerProvider());
  }
}
