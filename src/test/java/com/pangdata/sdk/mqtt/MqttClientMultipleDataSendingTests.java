package com.pangdata.sdk.mqtt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.domain.Sensor;
import com.pangdata.sdk.mqtt.client.PangMqttClient;
import com.pangdata.sdk.mqtt.client.PangMqttClientV1;

public class MqttClientMultipleDataSendingTests {

  private Pang sendingDataClient;

  Sensor sensor = new Sensor();

  @Before
  public void init() throws Exception {
    sendingDataClient = new PangMqttClientV1("josh", "mykey");
    final CountDownLatch latch = new CountDownLatch(1);
    sendingDataClient.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    sendingDataClient.connect("tcp://localhost");
    latch.await(3, TimeUnit.SECONDS);
  }


  @Test
  public void periodicSendDataTest() {
    final int MAX_COUNT = 2;
    final CountDownLatch countDownLatch = new CountDownLatch(MAX_COUNT);

    sendingDataClient.startTimerTask(new MultipleDataCallback() {

      
      public boolean isRunning(int count) {
        System.out.println("count : " + count);
        countDownLatch.countDown();
        return count < MAX_COUNT;
      }

      public void onSuccess(Object value) {
        
      }

      public Object getData() {
          sensor.setHumidity((int) (Math.random() * 30 + 30));
          sensor.setTemperature((int) (Math.random() * 20 + 20));
          sensor.setTimeStamp(new Date());
        return sensor;
      }
      
    }, 5, TimeUnit.SECONDS);

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      Assert.assertEquals(0, countDownLatch.getCount());
      sendingDataClient.disconnect();
    }
  }
  
  @Test
  public void periodicSendMapDataTest() {
    final int MAX_COUNT = 2;
    final CountDownLatch countDownLatch = new CountDownLatch(MAX_COUNT);
    
    sendingDataClient.startTimerTask(new MultipleDataCallback() {
      
      
      public boolean isRunning(int count) {
        System.out.println("count : " + count);
        countDownLatch.countDown();
        return count < MAX_COUNT;
      }
      
      public void onSuccess(Object value) {
        
      }
      
      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object> ();
        data.put("humidity", (int) 0);
        data.put("temperature", (int) (Math.random() * 20 + 20));
        data.put("timeStamp", new Date().getTime());
        data.put("key", "123");
        return data;
      }
      
    }, 5, TimeUnit.SECONDS);
    
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      Assert.assertEquals(0, countDownLatch.getCount());
      sendingDataClient.disconnect();
    }
  }

}
