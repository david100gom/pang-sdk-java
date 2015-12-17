package com.pangdata.client.mqtt;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.SingleDataCallback;
import com.pangdata.sdk.mqtt.PDefaultMqttClient;

public class MqttClientConnectionTests {

  private Pangdata client;

  CountDownLatch latch = new CountDownLatch(1);
  
  @Before
  public void init() {
    client = new PDefaultMqttClient("leon0517", "ThingA");
    client.setConnectionCallback(new ConnectionCallback() {
      public void onConnectionSuccess() {
        System.out.println("connectionSuccess");
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {
        System.out.println("connectionLost");
      }

      public void onConnectionFailure(Throwable cause) {
        System.out.println("connectionFailure");
      }
    });
  }

  @Test
  public void sendDataBeforeConnectTest() {
    boolean sendData = client.sendData("ThingA", "{\"date\" : " + new Date() + "}");
    Assert.assertFalse(sendData);
  }

  @Test
  public void startDataSendingTaskBeforeConnectTest() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    client.startTimerTask("ThingA", new SingleDataCallback() {

      public String getData() {
        System.out.println("getData");
        return "{\"date\" : " + new Date() + "}";
      }

      public boolean isRunning(int currentCount) {
        return true;
      }

      public void onSuccess(String data) {
        latch.countDown();
      }
    }, 3, TimeUnit.SECONDS);
    
    boolean await = latch.await(2, TimeUnit.SECONDS);
    Assert.assertFalse(await);
  }
  
  @Test
  public void waitTimerTask() {
    client.waitTimerTask();
  }
  
  @Test
  public void stopDataSendingTaskBeforeConnectTest() {
    client.stopTimerTask();
  }

  @Test
  public void disconnectBeforeConnectTest() {
    client.disconnect();
  }

  @Test
  public void connectionSuccessTest() throws Exception {
    client.connect("tcp://mini.prever.co.kr");
    latch.await(3, TimeUnit.SECONDS);
    Assert.assertTrue(client.isConnected());
    client.disconnect();
  }

  @Test
  public void connectDisconnectTest() throws Exception {
    client.connect("tcp://mini.prever.co.kr");
    latch.await(3, TimeUnit.SECONDS);
    client.disconnect();
    Assert.assertTrue("client is connected.", !client.isConnected());
  }
}
