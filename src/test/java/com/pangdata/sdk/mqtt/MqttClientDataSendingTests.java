package com.pangdata.sdk.mqtt;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.callback.StopConditionCallback;
import com.pangdata.sdk.domain.Sensor;
import com.pangdata.sdk.mqtt.MqttTopics;
import com.pangdata.sdk.mqtt.client.PangMqttClient;
import com.pangdata.sdk.util.JsonUtils;

public class MqttClientDataSendingTests {

  private Pang sendingDataClient;

  private MqttClient reciveMqttClient;

  Sensor sensor = new Sensor();

  @Before
  public void init() throws Exception {
    sendingDataClient = new PangMqttClient("leon0517", "ThingA");
    final CountDownLatch latch = new CountDownLatch(1);
    sendingDataClient.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    sendingDataClient.connect("tcp://mini.prever.co.kr");
    latch.await(3, TimeUnit.SECONDS);
  }

  @Test
  public void sendDataSubscribeSharingTest() {

    try {
      reciveMqttClient = new MqttClient("tcp://mini.prever.co.kr", "derek-"+System.currentTimeMillis());
      reciveMqttClient.connect();
      reciveMqttClient.subscribe(MqttTopics.DataSubscriber.getTopic());
    } catch (MqttException e) {
      e.printStackTrace();
    }

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());
    final String sendData = JsonUtils.convertObjToJsonStr(sensor);


    reciveMqttClient.setCallback(new MqttCallback() {

      public void messageArrived(String topic, MqttMessage message) throws Exception {
        String data = new String(message.getPayload());
        System.out.println("sendData : " + sendData);
        System.out.println("rcveData : " + data);
        Assert.assertEquals(sendData, data);
        countDownLatch.countDown();
      }

      public void deliveryComplete(IMqttDeliveryToken token) {}

      public void connectionLost(Throwable cause) {}
    });

    sendingDataClient.sendData("ThingA", sendData);
    try {
      countDownLatch.await(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      sendingDataClient.disconnect();
      try {
        reciveMqttClient.disconnect();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
  }


  @Test(timeout = 5000)
  public void periodicSendDataTest() {
    final int MAX_COUNT = 4;
    final Queue<String> senderDataQueue = new ConcurrentLinkedQueue<String>();
    final CountDownLatch countDownLatch = new CountDownLatch(MAX_COUNT - 1);

    try {
      reciveMqttClient = new MqttClient("tcp://mini.prever.co.kr", "derek");
      reciveMqttClient.connect();
      reciveMqttClient.subscribe(MqttTopics.DataSubscriber.getTopic());
    } catch (MqttException e) {
      e.printStackTrace();
    }

    reciveMqttClient.setCallback(new MqttCallback() {

      public void messageArrived(String topic, MqttMessage message) throws Exception {
        String sendData = senderDataQueue.poll();
        String data = new String(message.getPayload());
        System.out.println("sendData : " + sendData);
        System.out.println("rcveData : " + data);
        Assert.assertEquals(sendData, data);
        countDownLatch.countDown();
      }

      public void deliveryComplete(IMqttDeliveryToken token) {}

      public void connectionLost(Throwable cause) {}
    });

    sendingDataClient.startTimerTask(new MultipleDataCallback() {

      public Object getData() {
        return sensor;
      }

      public boolean isRunning(int count) {
        System.out.println("count : " + count);
        return count <= MAX_COUNT;
      }

      public void onSuccess(Object sent) {

      }
    }, 1, TimeUnit.SECONDS);

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    } finally {
      Assert.assertEquals(0, countDownLatch.getCount());
      sendingDataClient.disconnect();
      try {
        reciveMqttClient.disconnect();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
  }

  @Test(timeout = 3000)
  public void waitDataSendingTaskTests() {
    final AtomicInteger i = new AtomicInteger();
    sendingDataClient.startTimerTask("ThingA", new StopConditionCallback(2) {
      public String getData() {
        i.incrementAndGet();
        return super.getData();
      };
    }, 1, TimeUnit.SECONDS);

    sendingDataClient.waitTimerTask();

    Assert.assertEquals(2, i.get());

    sendingDataClient.disconnect();
  }


}
