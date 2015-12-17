/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Preversoft
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.util.JsonUtils;

public class MqttFailoverHttpClient extends MqttDelegatedAbstractHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(MqttFailoverHttpClient.class);

  public MqttFailoverHttpClient(String userId, String key, String uri) {
    this(userId, key, uri, null);
  }

  public MqttFailoverHttpClient(String username, String userkey, String uri, DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri, dataSharingCallback);
    setBrokerConnector(new BrokerFailoverConnector("fo-connector", username + "-" + userkey));
  }

  @Override
  public void connect(String uri) throws Exception {
    super.connect(uri);
    
    HttpPost httpPost = null;

    HttpResponse response = null;
    try {

      if(httpClient == null) {
        httpClient = new DefaultHttpClient();
      }
      httpPost = new HttpPost(uri + "/pa/user/profile/" +userkey + "/"+ username);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("content-type", "application/json"));

      logger.info("Starting to get user profile.......");
      logger.info("URI: {}", httpPost.getURI().toString());
      response = httpClient.execute(httpPost);

      if (response.getStatusLine().getStatusCode() != 200) {
        logger.error("HTTP error: {}", EntityUtils.toString(response.getEntity(), "UTF-8"));
        throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatusLine().getStatusCode());
      }

      String profile = EntityUtils.toString(response.getEntity(), "UTF-8");
      logger.info("{} 's response profile: {}", username, profile);

      Map<String, Object> responseMap = (Map<String, Object>) JsonUtils.toObject(profile, Map.class);
      if (!(Boolean) responseMap.get("Success")) {
        throw new RuntimeException(String.format("Success: %s, Error message: %s",
            responseMap.get("Success"), responseMap.get("Message")));
      }
      connect((Map)responseMap.get("Data"));
    } catch (Exception e) {
      logger.error("User profile request error", e);
      throw e;
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
  
  protected void connect(Map profile) throws Exception {
    if (dataSharingCallback != null) {
      String sharedThings = (String) profile.get("SHARED_THINGS");
      String[] split = sharedThings.split(",");
      if (sharedThings.trim().length() != 0 && split.length > 0) {
        for (String thing : split) {
          String[] split2 = thing.split("\\/");
//          subscribeDataSharing(split2[0], split2[1], dataSharingCallback);
          brokerClient.subscribeDataSharing(split2[0], split2[1], dataSharingCallback);
        }
      }
    }
    String brokers = (String) profile.get("BROKERS");
    brokerClient.connect(brokers);
  }
}
