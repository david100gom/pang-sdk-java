package com.pangdata.client.mqtt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.pangdata.client.callback.StopConditionCallback;
import com.pangdata.client.callback.TestInfiniteDataSendingCallback;
import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.mqtt.PDefaultMqttClient;

public class MqttClientStateTests {

  private Pang client;
  CountDownLatch latch;

  @Before
  public void init() throws InterruptedException {
    client = new PDefaultMqttClient("leon0517", "ThingA");
    latch = new CountDownLatch(1);

    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
  }

  @Test
  public void startAndStopTest() throws Exception {
    client.connect("tcp://mini.prever.co.kr");
    latch.await(3, TimeUnit.SECONDS);
    client.startTimerTask("ThingA", new TestInfiniteDataSendingCallback(), 2, TimeUnit.SECONDS);
    client.stopTimerTask();
    client.startTimerTask("ThingA", new TestInfiniteDataSendingCallback(), 2, TimeUnit.SECONDS);
    client.stopTimerTask();
    client.disconnect();
  }

  @Test(expected = IllegalStateException.class)
  public void startAndStartTest() throws Exception {
    try {
      client.connect("tcp://mini.prever.co.kr");
      latch.await(3, TimeUnit.SECONDS);
      client.startTimerTask("ThingA", new TestInfiniteDataSendingCallback(), 2, TimeUnit.SECONDS);
      client.startTimerTask("ThingA", new TestInfiniteDataSendingCallback(), 2, TimeUnit.SECONDS);
    } finally {
      client.disconnect();
    }
  }

  @Test
  public void startAndWaitTwiceTest() throws Exception {
    try {
      client.connect("tcp://mini.prever.co.kr");
      latch.await(3, TimeUnit.SECONDS);
      
      client.startTimerTask("ThingA", new StopConditionCallback(1), 10, TimeUnit.MILLISECONDS);
      client.waitTimerTask();
      client.startTimerTask("ThingA", new StopConditionCallback(1), 10, TimeUnit.MILLISECONDS);
      client.waitTimerTask();
      client.startTimerTask("ThingA", new StopConditionCallback(1), 1, TimeUnit.SECONDS);
      client.waitTimerTask();
      client.startTimerTask("ThingA", new StopConditionCallback(1), 100, TimeUnit.MILLISECONDS);
      client.waitTimerTask();
    } finally {
      client.disconnect();
    }
  }

  @Test
  public void stopTaskTest() {
    client.stopTimerTask();
  }

  @Test
  public void stopConditionAndStartTest() throws Exception {
    client.connect("tcp://mini.prever.co.kr");
    latch.await(3, TimeUnit.SECONDS);
    client.startTimerTask("ThingA", new StopConditionCallback(2), 1, TimeUnit.SECONDS);
    client.stopTimerTask();
    client.waitTimerTask(4, TimeUnit.SECONDS);
    client.startTimerTask("ThingA", new StopConditionCallback(1), 1, TimeUnit.SECONDS);
    client.waitTimerTask();
    client.disconnect();
  }

}
