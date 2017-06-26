package com.pangdata.sdk.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.http.AbstractHttp;
import com.pangdata.sdk.mqtt.client.PangMqttClient;
import com.pangdata.sdk.mqtt.connector.BrokerConnector;
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.SdkUtils;

abstract class MqttDelegatedAbstractHttpClient extends AbstractHttp {
  private static final Logger logger = LoggerFactory
      .getLogger(MqttDelegatedAbstractHttpClient.class);

  protected HttpClient httpClient;
  protected Pang pang;

  protected DataSharingCallback dataSharingCallback;

  private ConnectionCallback connectionCallback;

  public MqttDelegatedAbstractHttpClient(boolean mustinvoke) {
    super(mustinvoke);
  }

  public MqttDelegatedAbstractHttpClient(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri);

    this.dataSharingCallback = dataSharingCallback;
  }

  protected void createConnector(BrokerConnector connector) {
    pang = new PangMqttClient(username, connector);
    if (connectionCallback != null) {
      pang.setConnectionCallback(connectionCallback);
    }
  }

  public boolean isConnected() {
    return pang.isConnected();
  }

  public boolean sendData(String devicename, String data) {
    return pang.sendData(devicename, data);
  }

  public boolean sendData(String devicename, Object data) {
    return pang.sendData(devicename, data);
  }

  public boolean sendData(Object data) {
    return pang.sendData(data);
  }


  public void startTimerTask(String devicename, DataCallback dataCallback, long period,
      TimeUnit timeUnit) {
    pang.startTimerTask(devicename, dataCallback, period, timeUnit);
  }

  public void waitTimerTask() {
    pang.waitTimerTask();
  }

  public void waitTimerTask(long timeout, TimeUnit unit) {
    pang.waitTimerTask(timeout, unit);
  }

  public void stopTimerTask() {
    pang.stopTimerTask();
  }

  public void subscribeDataSharing(String giverUserId, String devicename,
      DataSharingCallback sharingDataCallback) {
    throw new UnsupportedOperationException();
  }

  public void unsubscribeDataSharing(String giverUserId, String devicename) {
    pang.unsubscribeDataSharing(giverUserId, devicename);
  }

  public void subscribeControl(String devicename, ControlCallback controlCallback) {
    pang.subscribeControl(devicename, controlCallback);
  }

  public void unsubscribeControl(String devicename) {
    pang.unsubscribeControl(devicename);
  }

  public void disconnect() {
    pang.disconnect();
  }

  public void setConnectionCallback(ConnectionCallback connectionCallback) {
    if (pang == null) {
      this.connectionCallback = connectionCallback;
    } else {
      pang.setConnectionCallback(connectionCallback);
    }
  }

  public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
      TimeUnit timeUnit) {
    pang.startTimerTask(multipleDataCallback, period, timeUnit);
  }

  protected void setProxyClient() {
    pang = new Pang() {

      public void waitTimerTask(long timeout, TimeUnit unit) {}

      public void waitTimerTask() {}

      public void unsubscribeDataSharing(String giverUserId, String devicename) {}

      public void unsubscribeControl(String devicename) {}

      public void subscribeDataSharing(String giverUserId, String devicename,
          DataSharingCallback sharingDataCallback) {}

      public void subscribeControl(String devicename, ControlCallback controlCallback) {}

      public void stopTimerTask() {}

      public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
          TimeUnit timeUnit) {}

      public void startTimerTask(String devicename, DataCallback dataCallback, long period,
          TimeUnit timeUnit) {}

      public void setConnectionCallback(ConnectionCallback connectionCallback) {}

      public boolean sendData(String devicename, Object value) {
        return true;
      }

      public boolean sendData(Object data) {
        return true;
      }

      public boolean sendData(String devicename, String data) {
        return true;
      }

      public boolean isConnected() {
        return true;
      }

      public void disconnect() {}

      public void connect(String addresses) throws Exception {}

      public boolean isSendable() {
        return false;
      }

      public void setSendable(boolean sendable) {}
    };
  }
  
  protected Map<String, Object> request(String target) throws Exception {
    HttpPost httpPost = null;
    HttpResponse response = null;
    try {
      httpClient = SdkUtils.createHttpClient(this.url);
    
      // FIXIT? http://mini.prever.io:3000/issues/2342
      // TODO upgrade version to handle timeout.
      HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 100 * 1000);
      HttpConnectionParams.setSoTimeout(httpClient.getParams(), 100 * 1000);
    
      httpPost = new HttpPost(this.url + "/"+ target+ "/" + userkey + "/" + username);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("content-type", "application/json"));
    
      logger.info("Requesting.......");
      logger.info("URI: {}", httpPost.getURI().toString());
      response = httpClient.execute(httpPost);
    
      if (response.getStatusLine().getStatusCode() != 200) {
        logger.error("HTTP error: {}", EntityUtils.toString(response.getEntity(), "UTF-8"));
        throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatusLine().getStatusCode());
      }
    
      String profile = EntityUtils.toString(response.getEntity(), "UTF-8");
      logger.info("{} 's response profile: {}", username, profile);
    
      Map<String, Object> responseMap =
          (Map<String, Object>) JsonUtils.toObject(profile, Map.class);
      if (!(Boolean) responseMap.get("Success")) {
        throw new RuntimeException(String.format("Success: %s, Error message: %s",
            responseMap.get("Success"), responseMap.get("Message")));
      }
      return responseMap;
    } finally {
      try {
        if (httpClient != null) {
          httpClient.getConnectionManager().shutdown();
        }
      } catch (Exception e) {
        logger.error("Error", e);
      }

    }
  }
}
