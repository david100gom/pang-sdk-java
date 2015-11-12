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
package io.prever.sdk.mqtt;

import io.prever.sdk.Prever;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.callback.ControlCallback;
import io.prever.sdk.callback.DataCallback;
import io.prever.sdk.callback.DataSharingCallback;
import io.prever.sdk.callback.MultipleDataCallback;
import io.prever.sdk.http.AbstractPreverHttp;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class MqttDelegatedAbstractHttpClient extends AbstractPreverHttp {
  private static final Logger logger = LoggerFactory.getLogger(MqttDelegatedAbstractHttpClient.class);

  protected HttpClient httpClient;
  protected Prever brokerClient;
  
  protected DataSharingCallback dataSharingCallback;

  public MqttDelegatedAbstractHttpClient(String username, String userkey, String uri, DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri);
    
    this.dataSharingCallback = dataSharingCallback;
    
//    brokerClient = new PDefaultMqttClient(username, userkey, connector);
  }
  
  public void setBrokerConnector(BrokerConnector connector) {
    brokerClient = new PDefaultMqttClient(username, userkey, connector);
  }

  public boolean isConnected() {
    return brokerClient.isConnected();
  }

  public boolean sendData(String devicename, String data) {
    return brokerClient.sendData(devicename, data);
  }
  
  public boolean sendData(Object data) {
    return brokerClient.sendData(data);
  }

  public void startTimerTask(String devicename, DataCallback dataCallback, long period,
      TimeUnit timeUnit) {
    brokerClient.startTimerTask(devicename, dataCallback, period, timeUnit);
  }

  public void waitTimerTask() {
    brokerClient.waitTimerTask();
  }

  public void waitTimerTask(long timeout, TimeUnit unit) {
    brokerClient.waitTimerTask(timeout, unit);
  }

  public void stopTimerTask() {
    brokerClient.stopTimerTask();
  }

  public void subscribeDataSharing(String giverUserId, String devicename,
      DataSharingCallback sharingDataCallback) {
    throw new UnsupportedOperationException();
  }

  public void unsubscribeDataSharing(String giverUserId, String devicename) {
    brokerClient.unsubscribeDataSharing(giverUserId, devicename);
  }

  public void subscribeControl(String devicename, ControlCallback controlCallback) {
    brokerClient.subscribeControl(devicename, controlCallback);
  }

  public void unsubscribeControl(String devicename) {
    brokerClient.unsubscribeControl(devicename);
  }

  public void disconnect() {
    brokerClient.disconnect();
  }

  public void setConnectionCallback(ConnectionCallback connectionCallback) {
    brokerClient.setConnectionCallback(connectionCallback);
  }

  public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
      TimeUnit timeUnit) {
    brokerClient.startTimerTask(multipleDataCallback, period, timeUnit);    
  }
  
}
