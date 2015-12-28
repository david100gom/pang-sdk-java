package com.pangdata.sdk.simulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.TestInfiniteDataSendingCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClient;

public class DataPublisherWithTwoUsersTests {

  static private Pang client;
  static private Pang client2;

  static CountDownLatch latch = new CountDownLatch(1);
  static CountDownLatch latch2 = new CountDownLatch(1);

  public static void init() {
    client = new PangMqttClient("josh", "josh-key1");
    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    client2 = new PangMqttClient("derek", "derek-key1");
    client2.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
  }

  public static void main(String args[]) throws Exception {
    init();
    
    client.connect("tcp://mini.prever.co.kr:1884");
    latch.await(3, TimeUnit.SECONDS);
    client2.connect("tcp://mini.prever.co.kr:1884");
    latch2.await(3, TimeUnit.SECONDS);

    client.startTimerTask("thing1", new TestInfiniteDataSendingCallback(), 1, TimeUnit.SECONDS);
    client2.startTimerTask("thing1", new TestInfiniteDataSendingCallback(), 1, TimeUnit.SECONDS);

    client.waitTimerTask();
    client2.waitTimerTask();

    client.disconnect();
    client2.disconnect();
  }
}
