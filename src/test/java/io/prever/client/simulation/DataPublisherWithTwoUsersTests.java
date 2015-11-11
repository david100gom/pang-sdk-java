package io.prever.client.simulation;

import io.prever.client.callback.TestInfiniteDataSendingCallback;
import io.prever.sdk.Prever;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.mqtt.PDefaultMqttClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DataPublisherWithTwoUsersTests {

  static private Prever client;
  static private Prever client2;

  static CountDownLatch latch = new CountDownLatch(1);
  static CountDownLatch latch2 = new CountDownLatch(1);

  public static void init() {
    client = new PDefaultMqttClient("josh", "josh-key1");
    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    client2 = new PDefaultMqttClient("derek", "derek-key1");
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
