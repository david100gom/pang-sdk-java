package io.prever.client.mqtt;

import io.prever.client.domain.Sensor;
import io.prever.sdk.Prever;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.callback.DataSharingCallback;
import io.prever.sdk.mqtt.MqttTopics;
import io.prever.sdk.mqtt.PDefaultMqttClient;
import io.prever.sdk.util.JsonUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.Assert;
import org.junit.Test;

public class MqttClientDataSharingTests {

  private static final int COUNTER = 3;

  class PublishRunnable implements Runnable {
    Sensor sensor = new Sensor();
    MqttClient sendingClient;
    Queue<String> senderDataQueue;
    CountDownLatch countDownLatch;
    private int count;

    public PublishRunnable(MqttClient sendingClient, Queue<String> senderDataQueue,
        CountDownLatch countDownLatch, int count) {
      super();
      this.count = count;
      this.sendingClient = sendingClient;
      this.senderDataQueue = senderDataQueue;
      this.countDownLatch = countDownLatch;
    }

    public void run() {
      int i = 0;
      while (i < count) {
        MqttMessage message = new MqttMessage();
        sensor.setHumidity((int) (Math.random() * 30 + 30));
        sensor.setTemperature((int) (Math.random() * 20 + 20));
        sensor.setTimeStamp(new Date());
        String data = JsonUtils.convertObjToJsonStr(sensor);
        message.setPayload(data.getBytes(Charset.forName("utf-8")));
        senderDataQueue.add(data);
        try {
          sendingClient.publish(MqttTopics.DataShare.getTopic() + "leon0517/ThingA", message);
          TimeUnit.SECONDS.sleep(1);
        } catch (MqttPersistenceException e) {
          e.printStackTrace();
        } catch (MqttException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        i++;
      }
      countDownLatch.countDown();
    }
  }

  @Test
  public void receiveSharingDataTests() throws Exception {
    // Connect Process
    final Queue<String> senderDataQueue = new ConcurrentLinkedQueue<String>();
    final Queue<String> reciverDataQueue = new ConcurrentLinkedQueue<String>();
    CountDownLatch countDownLatch = new CountDownLatch(1);

    Prever receivingClient =
        new PDefaultMqttClient("derek", "userkey");
    MqttClient sendingClient = null;

    try {
      sendingClient = new MqttClient("tcp://mini.prever.co.kr", "leon0517");
      sendingClient.connect();
    } catch (MqttSecurityException e1) {
      e1.printStackTrace();
    } catch (MqttException e1) {
      e1.printStackTrace();
    }
    final CountDownLatch latch = new CountDownLatch(1);
    receivingClient.setConnectionCallback(new ConnectionCallback() {
		
		public void onConnectionSuccess() {
			latch.countDown();
		}

		public void onConnectionLost(Throwable cause) {
		}
		
		public void onConnectionFailure(Throwable cause) {
		}
	});
    latch.await(3, TimeUnit.SECONDS);
    
    receivingClient.connect("tcp://mini.prever.co.kr");

    // Sharing Sending - Receiving Process

    receivingClient.subscribeDataSharing("leon0517", "ThingA", new DataSharingCallback() {
      public void onSharedDataArrived(String data) {
        if (data != null) {
          reciverDataQueue.add(data);
        }
      }
    });


    Thread tr =
        new Thread(new PublishRunnable(sendingClient, senderDataQueue, countDownLatch, COUNTER));
    tr.start();

    try {
      countDownLatch.await();
      sendingClient.disconnect();
      receivingClient.disconnect();
    } catch (MqttException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    }

    // Assert Process
    Iterator<String> iterator = senderDataQueue.iterator();
    while (iterator.hasNext()) {
      String next = iterator.next();
      String poll = reciverDataQueue.poll();
      Assert.assertEquals(next, poll);
    }

    Assert.assertTrue(reciverDataQueue.size() == 0);
  }

}
