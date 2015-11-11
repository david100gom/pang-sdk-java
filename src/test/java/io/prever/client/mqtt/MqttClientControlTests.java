package io.prever.client.mqtt;

import io.prever.client.domain.Sensor;
import io.prever.sdk.Prever;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.callback.ControlCallback;
import io.prever.sdk.mqtt.MqttTopics;
import io.prever.sdk.mqtt.PDefaultMqttClient;
import io.prever.sdk.util.JsonUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MqttClientControlTests {

  private MqttClient sendingClient;

  private Prever receivingClient;

  Sensor sensor = new Sensor();

  @Before
  public void init() {
    try {
      sendingClient = new MqttClient("tcp://mini.prever.co.kr", "leon0517");
    } catch (MqttException e) {
      e.printStackTrace();
    }
    receivingClient = new PDefaultMqttClient("derek", "userkey");
  }


  @Test(timeout = 2000)
  public void sendControlTest() throws Exception {
    try {
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
    receivingClient.connect("tcp://mini.prever.co.kr");

    latch.await(3, TimeUnit.SECONDS);
    
    final CountDownLatch countDownLatch = new CountDownLatch(1);
    final String sendControlKey = "turnOffLight";

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("controlKey", sendControlKey);

    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());

    final String sendData = JsonUtils.convertObjToJsonStr(sensor);
    map.put("data", sendData);

    String publishData = JsonUtils.convertObjToJsonStr(map);


    receivingClient.subscribeControl("ThingA", new ControlCallback() {

      public void onDeliveryComplete(String topic, String data) {}

      public String execute(String controlKey, String data) {
        System.out.println("sendControlKey : " + sendControlKey + ", controlKey : " + controlKey);
        System.out.println("sendData : " + sendData);
        System.out.println("rcveData : " + data);
        Assert.assertEquals(sendControlKey, controlKey);
        Assert.assertEquals(sendData, data);
        countDownLatch.countDown();
        return "{ \"result\" : \"true\" }";
      }
    });


    MqttMessage message = new MqttMessage(publishData.getBytes(Charset.forName("utf-8")));
    try {
      sendingClient.publish(MqttTopics.ControlRequestPublisher.getTopic() + "derek/ThingA", message);
      countDownLatch.await();
      sendingClient.disconnect(); 
    } catch (MqttPersistenceException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (MqttException e) {
      e.printStackTrace();
      Assert.fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
