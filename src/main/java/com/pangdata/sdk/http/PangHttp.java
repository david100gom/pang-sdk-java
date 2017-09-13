package com.pangdata.sdk.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.util.DevicenameUtils;
import com.pangdata.sdk.util.JsonUtils;

public class PangHttp extends AbstractHttp {

  private boolean sendable = true;

  public PangHttp() {
    super(true);
  }
  
  public PangHttp(String username, String userkey) {
    this(username, userkey, null);
  }

  public PangHttp(String username, String userkey, String uri) {
    super(username, userkey, uri);
  }

  public boolean isConnected() {
    throw new UnsupportedOperationException();
  }

  public void subscribeDataSharing(String giverUserId, String thingId,
      DataSharingCallback sharingDataCallback) {
    throw new UnsupportedOperationException();
  }

  public void unsubscribeDataSharing(String giverUserId, String thingId) {
    throw new UnsupportedOperationException();
  }

  public void subscribeControl(String thingId, ControlCallback controlCallback) {
    throw new UnsupportedOperationException();
  }

  public void unsubscribeControl(String thingId) {
    throw new UnsupportedOperationException();
  }

  public void setConnectionCallback(ConnectionCallback connectionCallback) {
    throw new UnsupportedOperationException();
  }


  public boolean sendData(String devicename, Object value) {
    Map<String, Object> dataMap = new HashMap<String, Object>();
    dataMap.put(devicename, value);
    return sendData(dataMap);
  }

  public boolean sendData(Map<String, Object> dataMap) {
    if(!isSendable()) {
      return false;
    }
    for(String devicename:dataMap.keySet()) {
      if(DevicenameUtils.isValid(devicename)) {
        throw new IllegalArgumentException("Devicename({}) is invalid"); 
      }
    }
    
    HttpPost httpPost =
        new HttpPost(getUrl());
    String data = JsonUtils.convertObjToJsonStr(dataMap);
    StringEntity params = new StringEntity(data, "UTF-8");
    httpPost.setEntity(params);
    httpPost.addHeader("content-type", "application/json");

    return sendData(httpPost);
  }

  public boolean isSendable() {
    return sendable;
  }

  public void setSendable(boolean sendable) {
    this.sendable  = sendable;
  }

}
