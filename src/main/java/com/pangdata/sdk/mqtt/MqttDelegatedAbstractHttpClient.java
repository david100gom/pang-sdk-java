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

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
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

abstract class MqttDelegatedAbstractHttpClient extends AbstractHttp {
  private static final Logger logger = LoggerFactory
      .getLogger(MqttDelegatedAbstractHttpClient.class);

  protected HttpClient httpClient;
  protected Pang pang;

  protected DataSharingCallback dataSharingCallback;

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
  }

  public boolean isConnected() {
    return pang.isConnected();
  }

  public boolean sendData(String devicename, String data) {
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
    pang.setConnectionCallback(connectionCallback);
  }

  public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
      TimeUnit timeUnit) {
    pang.startTimerTask(multipleDataCallback, period, timeUnit);
  }

}
