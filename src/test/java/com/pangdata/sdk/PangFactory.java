package com.pangdata.sdk;

import com.pangdata.sdk.http.PangHttp;
import com.pangdata.sdk.mqtt.MqttFailoverHttpClient;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.mqtt.PangMqttV2;


public class PangFactory {

  private static final String uri = "http://pangdata.com";
  
  public static Pang createHttpClient(String username, String userkey) throws Exception {
    return createHttpClient(username, userkey, uri);
  }
  
  public static Pang createHttpClient(String username, String userkey, String uri) throws Exception {
    PangHttp client = new PangHttp(username, userkey, uri);
    client.connect(uri);
    return client;
  }
  
  public static Pang createReassignableMqttClient(String username, String userkey) throws Exception {
    return createReassignableMqttClient(username, userkey, uri);
  }
  
  public static Pang createReassignableMqttClient(String username, String userkey, String uri) throws Exception {
    Pang client = new PangMqtt(username, userkey, uri);
    return client;
  }
  
  public static Pang createReassignableMqttClientV2(String username, String userkey, String uri) throws Exception {
	  Pang client = new PangMqttV2(username, userkey, uri);
	  return client;
  }
  
  public static Pang createFailoverMqttClient(String username, String userkey) throws Exception {
    return createFailoverMqttClient(username, userkey, uri);
  }
  
  public static Pang createFailoverMqttClient(String username, String userkey, String uri) throws Exception {
    Pang client = new MqttFailoverHttpClient(username, userkey, uri);
    client.connect(uri);
    return client;
  }

}
