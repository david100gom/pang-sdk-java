package io.prever.client.simulation;

import io.prever.client.domain.Sensor;
import io.prever.sdk.Prever;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.callback.InfiniteDataSendingCallback;
import io.prever.sdk.mqtt.PDefaultMqttClient;
import io.prever.sdk.util.JsonUtils;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IoterClientFailoverTests {

  static private Prever client;

  static CountDownLatch latch = new CountDownLatch(1);

  public static void init() {
    client = new PDefaultMqttClient("josh", "josh-key1");
    client.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
  }

  public static void main(String args[]) throws Exception {
    init();

    client.connect("tcp://mini.prever.co.kr:1884;tcp://mini.prever.co.kr:1883");
    latch.await(3, TimeUnit.SECONDS);

    client.startTimerTask("thing1", new InfiniteDataSendingCallback() {

      public String getData() {
        Sensor sensor = new Sensor();
        sensor.setHumidity((int) (Math.random() * 30 + 30));
        sensor.setTemperature((int) (Math.random() * 20 + 20));
        sensor.setTimeStamp(new Date());
        return JsonUtils.convertObjToJsonStr(sensor);
      }

      public void onSuccess(String data) {
        System.out.println(data);
      }
    }, 1, TimeUnit.SECONDS);

    client.waitTimerTask();
    client.disconnect();
  }
}
