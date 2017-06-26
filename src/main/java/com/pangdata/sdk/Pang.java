package com.pangdata.sdk;

import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;



public interface Pang {

  public void connect(String addresses) throws Exception;
  
  public boolean isConnected();

  public boolean sendData(String devicename, String data);
  
  public boolean sendData(Object data);

  public boolean sendData(String devicename, Object value);
  
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

  public boolean isSendable();
  
  public void setSendable(boolean sendable);
}
