package com.pangdata.sdk.mqtt;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.domain.Sensor;
import com.pangdata.sdk.mqtt.client.PangMqttClient;

public class SimpleDataSendingTests {

  private Pang sendingDataClient;

  Sensor sensor = new Sensor();
  
  @Before
  public void init() throws Exception {
    sendingDataClient = new PangMqttClient("demo", "test1");
    final CountDownLatch latch = new CountDownLatch(1);
    sendingDataClient.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    sendingDataClient.connect("tcp://192.168.0.21:1884");
    latch.await(3, TimeUnit.SECONDS);
  }
  
  @Test
  public void sendmanydata() throws Exception {
    Random r = new Random();
    
    for(int i=0;i<1000;i++) {
      TimeUnit.MILLISECONDS.sleep(5);
      sendingDataClient.sendData("pi_temp", ""+r.nextInt(50));
    }
  }

  @Test
  public void sendone() throws Exception {
    Random r = new Random();
     sendingDataClient.sendData("test1", ""+95);
  }

}
