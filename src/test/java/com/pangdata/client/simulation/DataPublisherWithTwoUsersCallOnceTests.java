package com.pangdata.client.simulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.pangdata.client.callback.StopConditionCallback;
import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.mqtt.PDefaultMqttClient;

public class DataPublisherWithTwoUsersCallOnceTests {

  static private Pang client;
  static private Pang client2;

  static CountDownLatch latch = new CountDownLatch(1);
  static CountDownLatch latch2 = new CountDownLatch(1);

  public static void init() {
    client = new PDefaultMqttClient("leon0517", "leon0517-key1");
    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    client2 = new PDefaultMqttClient("leon0517", "leon0517-key2");
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
    
    client.connect("tcp://localhost:1883");
    latch.await(3, TimeUnit.SECONDS);
    client2.connect("tcp://localhost:1883");
    latch2.await(3, TimeUnit.SECONDS);

    client.startTimerTask("leonThing", new StopConditionCallback(1), 1, TimeUnit.SECONDS);
    client2.startTimerTask("leonThing2", new StopConditionCallback(1), 1, TimeUnit.SECONDS);

    client.waitTimerTask();
    client2.waitTimerTask();

    client.disconnect();
    client2.disconnect();
  }
}
