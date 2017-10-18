package com.pangdata.sdk.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdkUtils {
  private static final Logger logger = LoggerFactory
      .getLogger(SdkUtils.class);
  
  public static String getDevicename(String devicename) {
    return getDevicename(devicename, null);
  }

  public static String getDevicename(String devicename, String...args) {
    if(args != null) {
      devicename = replaceTwoToOne(replaceSpecialCharacter(replace(devicename, args)));
    }
    String prefix = PangProperties.getPrefix();
    if (prefix != null && !prefix.isEmpty()) {
      devicename = prefix+PangProperties.getConcatenator()+devicename;
    }
    return devicename;
  }
  
  public static String replace(String value, String...args) {
    for(String arg:args) {
      value = value.replace("{}", arg);
    }
    return value;
  }
  
  
  public static String replaceSpaceCharacter(String devicename) {
    return devicename.replaceAll("[\\s+]", "");
  }

  public static String replaceTwoToOne(String devicename) {
    return devicename.replaceAll(PangProperties.getConcatenator()+"{2,}", PangProperties.getConcatenator());
  }
  public static String replaceSpecialCharacter(String devicename) {
    return devicename.replaceAll("[^a-zA-Z0-9]", PangProperties.getConcatenator());
  }
  
  public static InetAddress getLocalAddress() throws SocketException
  {
    Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while( ifaces.hasMoreElements() )
    {
      NetworkInterface iface = ifaces.nextElement();
      Enumeration<InetAddress> addresses = iface.getInetAddresses();

      while( addresses.hasMoreElements() )
      {
        InetAddress addr = addresses.nextElement();
        if( addr instanceof Inet4Address && !addr.isLoopbackAddress() )
        {
          return addr;
        }
      }
    }

    return null;
  }
  
  public static String getMacAddress() throws Exception{
    NetworkInterface network = NetworkInterface.getByInetAddress(getLocalAddress());
        
    byte[] mac = network.getHardwareAddress();
        
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < mac.length; i++) {
        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
    }
    
    return sb.toString();
  }
  
  public static DefaultHttpClient createHttpClient(String url) throws Exception {
    return createHttpClient(url, null);
  }
  
  public static DefaultHttpClient createHttpClient(String url, HttpParams myParams) throws Exception {
    DefaultHttpClient httpClient = null;
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
        httpClient = new DefaultHttpClient(ccm, myParams);
      } else {
          httpClient = new DefaultHttpClient(myParams);
      }
    
    return httpClient;
  }
  
  public static Map<String, Object> request(String target, String data) throws Exception {
    return request(target, data, null, null, true);
  }
  public static Map<String, Object> request(String target, String data, String authorization, Map<String, Object> body, boolean check) throws Exception {
    HttpPost httpPost = null;
    HttpResponse response = null;
    HttpClient httpClient = null;
    try {
      httpClient = SdkUtils.createHttpClient(target);
    
      // FIXIT? http://mini.prever.io:3000/issues/2342
      // TODO upgrade version to handle timeout.
      HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 100 * 1000);
      HttpConnectionParams.setSoTimeout(httpClient.getParams(), 100 * 1000);
    
      httpPost = new HttpPost(target);
      if(authorization != null) {
        httpPost.setHeader("Authorization", authorization);
      }
      if(data != null) {
        HttpEntity entity = new StringEntity(data);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "text/plain");
      }
      
      if(body != null) {
        String convertObjToJsonStr = JsonUtils.convertObjToJsonStr(body);
        StringEntity input = new StringEntity(convertObjToJsonStr);
        input.setContentType("application/json");
        httpPost.setEntity(input);
      }
      
//      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//      nvps.add(new BasicNameValuePair("content-type", "application/json"));
//      if(authorization != null) {
//        nvps.add(new BasicNameValuePair("Authorization", authorization));
//      }
//      
//      httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    
      logger.info("Requesting.......");
      logger.info("URI: {}", httpPost.getURI().toString());
      response = httpClient.execute(httpPost);
    
      if (response.getStatusLine().getStatusCode() != 200) {
        logger.error("HTTP error: {}", EntityUtils.toString(response.getEntity(), "UTF-8"));
        throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatusLine().getStatusCode());
      }
    
      String profile = EntityUtils.toString(response.getEntity(), "UTF-8");
      logger.info("Response : {}", profile);
    
      Map<String, Object> responseMap =
          (Map<String, Object>) JsonUtils.toObject(profile, Map.class);
      if(check) {
        if (!(Boolean) responseMap.get("Success")) {
          throw new RuntimeException(String.format("Success: %s, Error message: %s",
              responseMap.get("Success"), responseMap.get("Message")));
        }
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
