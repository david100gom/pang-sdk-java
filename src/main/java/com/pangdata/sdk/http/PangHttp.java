package com.pangdata.sdk.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

  public boolean sendData(Map<String, Object> map) {
    if(!isValidLicense()) {
      return false;
    }
    DevicenameUtils.checkDeviceNames(map);
    
    return request(map);
  }
  
  public boolean sendData(List<Map<String, Object>> rows) {
	  if(!isValidLicense()) {
		  return false;
	  }
	  
	  for(Map<String, Object> map : rows) {
        DevicenameUtils.checkDeviceNames(map);
      }
	  
	 return request(rows);
  }

  public boolean isValidLicense() {
    return sendable;
  }

  public void setValidLicense(boolean sendable) {
    this.sendable  = sendable;
  }

  private boolean request(Object data) {
	  HttpPost httpPost =
			  new HttpPost(getUrl());
	  StringEntity params = new StringEntity(JsonUtils.convertObjToJsonStr(data), "UTF-8");
	  httpPost.setEntity(params);
	  httpPost.addHeader("content-type", "application/json");
	  
	  return sendData(httpPost);
  }
}
