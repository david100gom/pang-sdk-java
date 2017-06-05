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

  private ConnectionCallback connectionCallback;

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
    if(connectionCallback != null) {
    	pang.setConnectionCallback(connectionCallback);
    }
  }

  public boolean isConnected() {
    return pang.isConnected();
  }

  public boolean sendData(String devicename, String data) {
    return pang.sendData(devicename, data);
  }

  public boolean sendData(String devicename, Object data) {
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
	  if(pang == null) {
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
      
      public void waitTimerTask(long timeout, TimeUnit unit) {
        
      }
      
      public void waitTimerTask() {
        // TODO Auto-generated method stub
        
      }
      
      public void unsubscribeDataSharing(String giverUserId, String devicename) {
        
      }
      
      public void unsubscribeControl(String devicename) {
        
      }
      
      public void subscribeDataSharing(String giverUserId, String devicename,
          DataSharingCallback sharingDataCallback) {
        
      }
      
      public void subscribeControl(String devicename, ControlCallback controlCallback) {
        
      }
      
      public void stopTimerTask() {
        
      }
      
      public void startTimerTask(MultipleDataCallback multipleDataCallback, long period,
          TimeUnit timeUnit) {
        
      }
      
      public void startTimerTask(String devicename, DataCallback dataCallback, long period,
          TimeUnit timeUnit) {
        
      }
      
      public void setConnectionCallback(ConnectionCallback connectionCallback) {
        
      }
      
      public boolean sendData(String devicename, Object value) {
        return true;
      }
      
      public boolean sendData(Object data) {
        return true;
      }
      
      public boolean sendData(String devicename, String data) {
        return true;
      }
      
      public boolean isConnected() {
        // TODO Auto-generated method stub
        return true;
      }
      
      public void disconnect() {
        
      }
      
      public void connect(String addresses) throws Exception {
        
      }
    };
  }
}
