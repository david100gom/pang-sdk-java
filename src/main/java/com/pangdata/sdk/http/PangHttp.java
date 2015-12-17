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
package com.pangdata.sdk.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.ControlCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.util.JsonUtils;

public class PangHttp extends AbstractHttp {

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

  public boolean sendData(String devicename, String data) {
    HttpGet get =
        new HttpGet(String.format("%s/api/data/put/%s/%s/%s/%s", url, userkey, username,
            devicename, data));
    return sendData(get);
  }

  public boolean sendData(Object obj) {
    HttpPost httpPost =
        new HttpPost(getUrl());
    try {
      String data = JsonUtils.convertObjToJsonStr(obj);
      StringEntity params = new StringEntity(data, "UTF-8");
      httpPost.setEntity(params);
      httpPost.addHeader("content-type", "application/json");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return false;
    }

    return sendData(httpPost);
  }

}
