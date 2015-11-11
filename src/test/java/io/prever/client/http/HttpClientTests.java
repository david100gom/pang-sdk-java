package io.prever.client.http;

import io.prever.client.domain.Sensor;
import io.prever.sdk.Prever;
import io.prever.sdk.PreverFactory;
import io.prever.sdk.callback.MultipleDataCallback;
import io.prever.sdk.callback.SingleDataCallback;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class HttpClientTests {

  Sensor sensor = new Sensor();
  
  @Test
  public void senddata() throws Exception {
    Prever httpClient = PreverFactory.createHttpClient("josh", "abc2fk", "http://localhost:9191");
    
    httpClient.sendData("temperature", "12");
    
    httpClient.disconnect();
  }
  
  @Test
  public void sendMultipledata() throws Exception {
    Prever httpClient = PreverFactory.createHttpClient("josh", "abc2fk", "http://localhost:9191");
    
    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());
    
    httpClient.sendData(sensor);
    
    httpClient.disconnect();
  }
  
  @Test
  public void sendByTimerTask() throws Exception {
    final Random r = new Random();
    
    Prever httpClient = PreverFactory.createHttpClient("josh", "abc2fk", "http://localhost:9191");
    
    httpClient.startTimerTask("temperature", new SingleDataCallback() {
      
      public void onSuccess(String data) {
        System.out.println(data);
      }
      
      public boolean isRunning(int currentCount) {
        return currentCount < 10;
      }
      
      public String getData() {
        return Integer.toString(r.nextInt(10));
      }
    }, 5, TimeUnit.SECONDS);
    
    
    httpClient.waitTimerTask();
    
    httpClient.disconnect();    
  }
  
  
  @Test
  public void sendMultipleDataByTimerTask() throws Exception {
    
    Prever httpClient = PreverFactory.createHttpClient("josh", "abc2fk");
    httpClient.connect("http://localhost:9191");
    
    httpClient.startTimerTask(new MultipleDataCallback() {     

      public boolean isRunning(int count) {
        return count < 10;
      }

      public void onSuccess(Object value) {
        
      }

      public Object getData() {
          sensor.setHumidity((int) (Math.random() * 30 + 30));
          sensor.setTemperature((int) (Math.random() * 20 + 20));
          sensor.setTimeStamp(new Date());
        return sensor;
      }
      
    }, 5, TimeUnit.SECONDS);
    
    httpClient.waitTimerTask();
    
    httpClient.disconnect();    
  }

}
