package com.pangdata.sdk.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.AbstractPang;
import com.pangdata.sdk.PangException;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.PangProperties;
import com.pangdata.sdk.util.SdkUtils;

public abstract class AbstractHttp extends AbstractPang {
  private static final Logger logger = LoggerFactory.getLogger(AbstractHttp.class);

  protected Map<Long, HttpClient> httpClients = new HashMap<Long, HttpClient>();
  protected String url = "https://pangdata.com";
  protected String username;
  protected String userkey;

  protected String fullurl;
  
  public AbstractHttp(boolean mustinvoke) {
    try {
      String username = (String) PangProperties.getProperty("pang.username");
      if(username != null && username.trim().length() > 0) {
        this.username = username.trim();
      } else {
        throw new PangException(new IllegalStateException("pang.username not found in pang.properties"));
      }
      String userkey = (String) PangProperties.getProperty("pang.userkey");
      if(userkey != null && userkey.trim().length() > 0) {
        this.userkey = userkey.trim();
      } else {
        throw new PangException(new IllegalStateException("pang.userkey not found in pang.properties"));
      }
      String url = (String) PangProperties.getProperty("pang.url");
      if(url != null && url.trim().length() > 0) {
        this.url = url;
      } 
    } catch (PangException e) {
      logger.error("Property error", e);
      throw e;
    }
  }
  
  public AbstractHttp(String username, String userkey, String uri) {
    if (uri != null) {
      this.url = uri;
    }
    this.username = username;
    this.userkey = userkey;
  }

  public void subscribeDataSharing(String giverUsername, String devicename,
      DataSharingCallback sharingDataCallback) {
    throw new UnsupportedOperationException();
  }

  public void connect(String uri) throws Exception {
    if (uri == null) {
      throw new IllegalArgumentException("uri must not be null");
    }
    this.url = uri;
  }
  
  public void connect(String uri, boolean anonymous) throws Exception {
   throw new UnsupportedOperationException();
  }

  protected boolean sendData(HttpRequestBase request) {
    logger.debug("Send data to server {}", fullurl);
    HttpResponse response = null;
    HttpClient httpClient = httpClients.get(Thread.currentThread().getId());
    try {
      if (httpClient == null) {
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(myParams, 100000);
        HttpConnectionParams.setConnectionTimeout(myParams, 100000); // Timeout
        
        httpClient = SdkUtils.createHttpClient(url, myParams);
        
        httpClients.put(Thread.currentThread().getId(), httpClient);
        httpClient.getParams().setParameter(ClientPNames.HANDLE_AUTHENTICATION, false);
      }
      
      if(!url.toLowerCase().startsWith("http")) {
        url = "http://" + url;
      }
      
      response = httpClient.execute(request);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != 200) {
        logger.error("HTTP error: {}", result);
        if(response.getStatusLine().getStatusCode() == 401) {
          logger.error("UNAUTHORIZED ERROR. Process will be shutdown. Verify your username and userkey");
          System.exit(1);
        }
        throw new IllegalStateException(String.format("HTTP error code : %s, Error message: %s", response
            .getStatusLine().getStatusCode(), result));
      }

      logger.debug("Response: {}", result);

      Map<String, Object> responseMap = (Map<String, Object>) JsonUtils.toObject(result, Map.class);
      if (!(Boolean) responseMap.get("Success")) {
        throw new IllegalStateException(String.format("Error message: %s", responseMap.get("Message")));
      }
      return true;
    } catch (Exception e) {
      throw new PangException(e);
    }
  }

  public void disconnect() {
    logger.info("Closing connections");
    try {
      if (httpClients.size() > 0) {
        Iterator<Entry<Long, HttpClient>> iterator = httpClients.entrySet().iterator();
        while(iterator.hasNext()) {
          iterator.next().getValue().getConnectionManager().shutdown();
        }
      }
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  protected String getUrl() {
    if(fullurl == null) {
      fullurl = String.format("%s/api/data/put/%s/%s", url, userkey, username);;
    }
    return fullurl;
  }
}
