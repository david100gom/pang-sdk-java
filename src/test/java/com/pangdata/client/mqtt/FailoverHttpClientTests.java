package com.pangdata.client.mqtt;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.pangdata.client.domain.Sensor;
import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.PangdataFactory;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.SingleDataCallback;

public class FailoverHttpClientTests {

  final Random r = new Random();
  
  @Test
  public void senddata() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    Pangdata httpClient = PangdataFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
    
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
    Pangdata httpClient = PangdataFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
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
    
    Pangdata httpClient = PangdataFactory.createReassignableMqttClient("josh", "abc2fk", "http://localhost:9191");
    
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
    
    Pangdata httpClient = PangdataFactory.createFailoverMqttClient("josh", "abc2fk", "http://localhost:9191");
    
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
