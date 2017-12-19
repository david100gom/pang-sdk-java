package com.pangdata.sdk.mqtt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.PangProperties;
import com.pangdata.sdk.util.SdkUtils;

abstract class MqttDelegatedAbstractHttpClient extends AbstractHttp {
  private static final Logger logger = LoggerFactory
      .getLogger(MqttDelegatedAbstractHttpClient.class);

  protected Pang pang;

  protected DataSharingCallback dataSharingCallback;

  private ConnectionCallback connectionCallback;

  private Map<String, Map<String, Object>> rMap = new HashMap<String, Map<String, Object>> ();

  public MqttDelegatedAbstractHttpClient(boolean mustinvoke) {
    super(mustinvoke);
  }

  public MqttDelegatedAbstractHttpClient(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) {
    super(username, userkey, uri);

    this.dataSharingCallback = dataSharingCallback;
  }

  protected void createConnector(BrokerConnector connector) {
    pang = createClient(username, connector);
    if (connectionCallback != null) {
      pang.setConnectionCallback(connectionCallback);
    }
  }

  protected abstract Pang createClient(String username, BrokerConnector connector);

public boolean isConnected() {
    return pang.isConnected();
  }

  public boolean sendData(String devicename, Object data) {
    return pang.sendData(devicename, data);
  }

  public boolean sendData(Map<String, Object> data) {
    try {
      registerDevices(data);
      return pang.sendData(data);
    } catch (Exception e) {
      logger.error("Error", e);
      return false;
    }
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
    if (pang == null) {
      this.connectionCallback = connectionCallback;
    } else {
      pang.setConnectionCallback(connectionCallback);
    }
  }

  public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
      TimeUnit timeUnit) {
    pang.startTimerTask(multipleDataCallback, period, timeUnit);
  }

  protected void setProxyClient() {
    pang = new Pang() {

      public void waitTimerTask(long timeout, TimeUnit unit) {}

      public void waitTimerTask() {}

      public void unsubscribeDataSharing(String giverUserId, String devicename) {}

      public void unsubscribeControl(String devicename) {}

      public void subscribeDataSharing(String giverUserId, String devicename,
          DataSharingCallback sharingDataCallback) {}

      public void subscribeControl(String devicename, ControlCallback controlCallback) {}

      public void stopTimerTask() {}

      public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
          TimeUnit timeUnit) {}

      public void startTimerTask(String devicename, DataCallback dataCallback, long period,
          TimeUnit timeUnit) {}

      public void setConnectionCallback(ConnectionCallback connectionCallback) {}

      public boolean sendData(String devicename, Object value) {
        return true;
      }

      public boolean sendData(Map<String, Object> data) {
        return true;
      }

      public boolean isConnected() {
        return true;
      }

      public void disconnect() {}

      public void connect(String addresses) throws Exception {}

      public boolean isValidLicense() {
        return false;
      }

      public void setValidLicense(boolean sendable) {}
    };
  }
  
  protected Map<String, Object> request(String target) throws Exception {
    return SdkUtils.request(this.url + "/"+ target, null);
  }
  
  private void registerDevices(Object data) throws Exception {
    String prefix = PangProperties.getPrefix();
    
    if(prefix == null || prefix.length() == 0) {
      return;
    }
    
    Map<String, Map<String, Object>> toRegister = new HashMap<String, Map<String, Object>>();
    if(data instanceof Map) {
      
      Map<String, Object> map = (Map<String, Object>)data; 
      
      Iterator<Entry<String, Object>> devices = map.entrySet().iterator();
      while(devices.hasNext()) {
        // Register
        doPrepareToRegister(devices.next(), toRegister);
      }
    } else {
      List<Map<String, Object>> list = (List<Map<String, Object>>)data;
      for(Map<String, Object> map : list) {
        Iterator<Entry<String, Object>> devices = map.entrySet().iterator();
        while(devices.hasNext()) {
          Entry<String, Object> next = devices.next();
          doPrepareToRegister(devices.next(), toRegister);
        }
      }
    }
    doRegister(toRegister);
  }

  private void doPrepareToRegister(Entry<String, Object> next, Map<String, Map<String, Object>> toRegister) {
    String devicename = next.getKey();
    if(devicename.equalsIgnoreCase(PangProperties.Cons_timestamp)) {
      return;
    }
    
    if(!rMap.containsKey(devicename)) {
      Object value = next.getValue();
      if(value instanceof Map) {
        value = ((Map)value).get(PangProperties.Cons_value);
      }
      Map<String, Object> meta =  new HashMap<String, Object>();
      meta.put(PangProperties.Cons_value, value);
      
      Map<String, Object> dMeta = PangProperties.getDeviceMeta(devicename);
      if(dMeta != null) {
	      if(dMeta.containsKey("title")) {
	        meta.put("title", dMeta.get("title"));
	      }
	      if(dMeta.containsKey(PangProperties.Cons_tag)) {
	        meta.put(PangProperties.Cons_tag, dMeta.get(PangProperties.Cons_tag));
	      }
	      if(dMeta.containsKey(PangProperties.Cons_desc)) {
	        meta.put(PangProperties.Cons_desc, dMeta.get(PangProperties.Cons_desc));
	      }
      }
      toRegister.put(devicename, meta);
    }
  }
  
  private void doRegister(Map<String, Map<String, Object>> toRegister) throws Exception {
    if(toRegister.size()>0) {
      logger.debug("To registered: {}", toRegister.toString());
      Iterator<Entry<String, Map<String, Object>>> iterator = toRegister.entrySet().iterator();
      
      SdkUtils.request(this.url+"/pa/device/register/"+userkey+"/"+username+"/"+PangProperties.getPrefix(), JsonUtils.convertObjToJsonStr(toRegister));
      
      while(iterator.hasNext()) {
        Entry<String, Map<String, Object>> next = iterator.next();
        String devicename = next.getKey();
        rMap.put(devicename, next.getValue());
      }
    }
  }
}
