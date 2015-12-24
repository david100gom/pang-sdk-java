package com.pangdata.sdk.simulation;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.SingleDataCallback;
import com.pangdata.sdk.domain.Sensor;
import com.pangdata.sdk.mqtt.PDefaultMqttClient;
import com.pangdata.sdk.util.JsonUtils;

public class DataPublisherTests {

  private static final String ADDR = "tcp://mini.prever.co.kr:1883";

  private Pang client;

  Sensor sensor = new Sensor();

  CountDownLatch latch = new CountDownLatch(1);
  
  @Before
  public void init() {
    client = new PDefaultMqttClient("josh", "josh-key1");
    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {
      }

      public void onConnectionFailure(Throwable cause) {
      }
    });
  }

  @Test()
  public void sendDataBeforeConnectTest() throws Exception {
    client.connect(ADDR);
    latch.await(3, TimeUnit.SECONDS);
    client.sendData("thing1", "{\"date\" : " + System.currentTimeMillis() + "}");
    client.disconnect();
  }

  @Test
  public void periodicSendDataTest() throws Exception {
    client.connect(ADDR);
    latch.await(3, TimeUnit.SECONDS);
    final int MAX_COUNT = 10;
    final Queue<String> senderDataQueue = new ConcurrentLinkedQueue<String>();
    final CountDownLatch countDownLatch = new CountDownLatch(MAX_COUNT - 1);

    client.startTimerTask("thing1", new SingleDataCallback() {

      public String getData() {
        sensor.setHumidity((int) (Math.random() * 30 + 30));
        sensor.setTemperature((int) (Math.random() * 20 + 20));
        sensor.setTimeStamp(new Date());
        String sendingData = JsonUtils.convertObjToJsonStr(sensor);
        senderDataQueue.add(sendingData);
        countDownLatch.countDown();
        return sendingData;
      }

      public boolean isRunning(int count) {
        System.out.println("count : " + count);
        return count <= MAX_COUNT;
      }

      public void onSuccess(String data) {
        // TODO Auto-generated method stub
        
      }
    }, 1, TimeUnit.SECONDS);

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      Assert.assertEquals(0, countDownLatch.getCount());
      client.disconnect();
    }
  }
}
