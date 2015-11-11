package io.prever.client.mqtt;

import io.prever.client.domain.Sensor;
import io.prever.sdk.Prever;
import io.prever.sdk.PreverFactory;
import io.prever.sdk.callback.ConnectionCallback;
import io.prever.sdk.callback.SingleDataCallback;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class FailoverHttpClientTests {

  final Random r = new Random();
  
  @Test
  public void senddata() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    Prever httpClient = PreverFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
    
    httpClient.setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        latch.countDown();
      }

      public void onConnectionLost(Throwable cause) {}

      public void onConnectionFailure(Throwable cause) {}
    });
    
    latch.await();

    httpClient.sendData("temperature", "12");
  }
  
  Sensor sensor = new Sensor();
  
  @Test
  public void sendMultipledata() throws Exception {
    Prever httpClient = PreverFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
//    PreverClient httpClient = PreverClientFactory.createReassignableMqttClient("demo", "nGVR-y", "http://192.168.0.21");
    
    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());
    
    httpClient.sendData(sensor);
    
    httpClient.disconnect();
  }
  
  @Test
  public void sendByTimerTask() throws Exception {
    final Random r = new Random();
    
    Prever httpClient = PreverFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
    
    httpClient.startTimerTask("temperature", new SingleDataCallback() {
      
      public void onSuccess(String data) {
        System.out.println("Sent data:" + data);
      }
      
      public boolean isRunning(int currentCount) {
        return currentCount < 20;
      }
      
      public String getData() {
        return Integer.toString(r.nextInt(10));
      }
    }, 5, TimeUnit.SECONDS);
    
    
    httpClient.waitTimerTask();
    
    httpClient.disconnect();    
  }
  
  @Test
  public void sendByTimerTaskUsingFailover() throws Exception {
    final Random r = new Random();
    
    Prever httpClient = PreverFactory.createFailoverMqttClient("josh", "abc2fk", "http://localhost:9191");
    
    httpClient.startTimerTask("temperature", new SingleDataCallback() {
      
      public void onSuccess(String data) {
        System.out.println("Sent data:" + data);
      }
      
      public boolean isRunning(int currentCount) {
        return currentCount < 20;
      }
      
      public String getData() {
        return Integer.toString(r.nextInt(10));
      }
    }, 5, TimeUnit.SECONDS);
    
    
    httpClient.waitTimerTask();
    
    httpClient.disconnect();    
  } 
 
}
