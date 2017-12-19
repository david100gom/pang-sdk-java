package com.pangdata.sdk.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.mqtt.connector.failover.BrokerFailoverConnector1;
import com.pangdata.sdk.util.JsonUtils;

public class MqttFailoverHttpClient extends MqttDelegatedAbstractHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(MqttFailoverHttpClient.class);

  public MqttFailoverHttpClient(String userId, String key, String uri) {
    this(userId, key, uri, null);
  }

  public MqttFailoverHttpClient(String username, String userkey, String uri, DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri, dataSharingCallback);
    createConnector(new BrokerFailoverConnector1("fo-connector", username + "-" + userkey));
  }

  @Override
  public void connect(String uri) throws Exception {
    super.connect(uri);
    
    try {
      Map<String, Object> profile = request("pa/user/profile"+"/" + userkey + "/" + username);
      connect((Map)profile.get("Data"));
    } catch (Exception e) {
      logger.error("User profile request error", e);
      throw e;
    }
  }
  
  protected void connect(Map profile) throws Exception {
    if (dataSharingCallback != null) {
      String sharedThings = (String) profile.get("SHARED_THINGS");
      String[] split = sharedThings.split(",");
      if (sharedThings.trim().length() != 0 && split.length > 0) {
        for (String thing : split) {
          String[] split2 = thing.split("\\/");
//          subscribeDataSharing(split2[0], split2[1], dataSharingCallback);
          pang.subscribeDataSharing(split2[0], split2[1], dataSharingCallback);
        }
      }
    }
    String brokers = (String) profile.get("BROKERS");
    pang.connect(brokers);
  }

  public void connect(String addresses, boolean anonymous) throws Exception {
    throw new UnsupportedOperationException();
  }

  public boolean isValidLicense() {
    return false;
  }

  public void setValidLicense(boolean sendable) {
  }

  @Override
  protected Pang createClient(String username, BrokerConnector connector) {
	return null;
  }

}
