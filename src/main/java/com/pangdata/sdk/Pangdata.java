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
package com.pangdata.sdk;

import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;



public interface Pangdata {

  public void connect(String address) throws Exception;

  public boolean isConnected();

  public boolean sendData(String devicename, String data);
  
  public boolean sendData(Object data);

  public void startTimerTask(String devicename, DataCallback dataCallback, long period,
      TimeUnit timeUnit);

  public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
      TimeUnit timeUnit);

  public void waitTimerTask();

  public void waitTimerTask(long timeout, TimeUnit unit);

  public void stopTimerTask();

  public void subscribeDataSharing(String giverUserId, String devicename,
      DataSharingCallback sharingDataCallback);

  public void unsubscribeDataSharing(String giverUserId, String devicename);

  public void subscribeControl(String devicename, ControlCallback controlCallback);

  public void unsubscribeControl(String devicename);

  public void disconnect();

  public void setConnectionCallback(ConnectionCallback connectionCallback);

}
