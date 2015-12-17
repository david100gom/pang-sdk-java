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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.util.JsonUtils;

public class MqttReassignableHttpClient extends MqttDelegatedAbstractHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(MqttReassignableHttpClient.class);

  class DefaultReassignableBrokerProvider implements ReassignableBrokerProvider {

    public String getAddress() throws Exception {
      return getNewAddress();
    }
  };


  public MqttReassignableHttpClient(String username, String userkey, String uri) {
    this(username, userkey, uri, null);
  }

  public MqttReassignableHttpClient(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri, dataSharingCallback);
    setBrokerConnector(new BrokerReassignFailoverConnector("fo-connector",
        username + "-" + userkey, new DefaultReassignableBrokerProvider()));
  }

  private String getNewAddress() throws Exception {
    HttpPost httpPost = null;

    HttpResponse response = null;
    try {
      if (httpClient == null) {
        if(url.toLowerCase().startsWith("https")) {
          TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] certificate, String authType) {
              return true;
            }
          };
          SSLSocketFactory sf =
              new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
          SchemeRegistry registry = new SchemeRegistry();
          registry.register(new Scheme("https", 443, sf));
          ClientConnectionManager ccm = new SingleClientConnManager(registry);
          httpClient = new DefaultHttpClient(ccm);
        } else {
          httpClient = new DefaultHttpClient();
        }
      }

      httpPost = new HttpPost(url + "/pa/user/profile/" + userkey + "/" + username);
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

      Map<String, Object> responseMap =
          (Map<String, Object>) JsonUtils.toObject(profile, Map.class);
      if (!(Boolean) responseMap.get("Success")) {
        throw new RuntimeException(String.format("Success: %s, Error message: %s",
            responseMap.get("Success"), responseMap.get("Message")));
      }
      Map data = (Map) responseMap.get("Data");
      String brokers = (String) data.get("BROKERS");
      return brokers;
    } catch (Exception e) {
      logger.error("User profile request error", e);
      throw e;
    }
  }

  @Override
  public void connect(String uri) throws Exception {
    super.connect(uri);
    String address = getNewAddress();
    brokerClient.connect(address);
  }

}
