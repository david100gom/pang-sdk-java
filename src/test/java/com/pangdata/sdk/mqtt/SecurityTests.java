package com.pangdata.sdk.mqtt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClient;

public class SecurityTests {

  @Test
  public void secured() throws Exception {
    Pang pang = new PangMqttClient("user", "passwd");
    final CountDownLatch latch = new CountDownLatch(1);
    pang.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    pang.connect("tcp://localhost");
    latch.await(3, TimeUnit.SECONDS);
    Thread.sleep(100000);
  }
  
  @Test
  public void unsecured() throws Exception {
    Pang pang = new PangMqttClient("user");
    final CountDownLatch latch = new CountDownLatch(1);
    pang.setConnectionCallback(new ConnectionCallback() {
      
      public void onConnectionSuccess() {
        latch.countDown();
      }
      
      public void onConnectionLost(Throwable cause) {}
      
      public void onConnectionFailure(Throwable cause) {}
    });
    pang.connect("tcp://localhost");
    latch.await(3, TimeUnit.SECONDS);
  }



}
