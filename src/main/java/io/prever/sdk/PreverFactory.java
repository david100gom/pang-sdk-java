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
package io.prever.sdk;

import io.prever.sdk.http.PreverHttp;
import io.prever.sdk.mqtt.MqttFailoverHttpClient;
import io.prever.sdk.mqtt.MqttReassignableHttpClient;


public class PreverFactory {

  private static final String uri = "http://prever.io";
  
  public static Prever createHttpClient(String username, String userkey) throws Exception {
    return createHttpClient(username, userkey, uri);
  }
  
  public static Prever createHttpClient(String username, String userkey, String uri) throws Exception {
    PreverHttp client = new PreverHttp(username, userkey, uri);
    client.connect(uri);
    return client;
  }
  
  public static Prever createReassignableMqttClient(String username, String userkey) throws Exception {
    return createReassignableMqttClient(username, userkey, uri);
  }
  
  public static Prever createReassignableMqttClient(String username, String userkey, String uri) throws Exception {
    Prever client = new MqttReassignableHttpClient(username, userkey, uri);
    client.connect(uri);
    return client;
  }
  
  public static Prever createFailoverMqttClient(String username, String userkey) throws Exception {
    return createFailoverMqttClient(username, userkey, uri);
  }
  
  public static Prever createFailoverMqttClient(String username, String userkey, String uri) throws Exception {
    Prever client = new MqttFailoverHttpClient(username, userkey, uri);
    client.connect(uri);
    return client;
  }

}
