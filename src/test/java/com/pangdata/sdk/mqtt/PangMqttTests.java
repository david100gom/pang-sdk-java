package com.pangdata.sdk.mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.util.PangProperties;

public class PangMqttTests {

  @Test
  public void multipleDevicesByTimer() throws Exception {
    final Random random = new Random();      

    Pang pang = new PangMqtt();
    
    long period = PangProperties.getPeriod(); //Milliseconds
    
    pang.startTimerTask(new MultipleDataCallback() {

      public void onSuccess(Object sent) {
        System.out.println("Data sent: " + sent.toString());
      }

      public boolean isRunning(int sentCount) {
        return true;
      }

      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        int nextInt = random.nextInt(100);
        data.put("test-device", nextInt);
        
        return data;
      }
      
    }, period, TimeUnit.MILLISECONDS);
    pang.waitTimerTask();
    
  }
  
  @Test
  public void singleDeviceByTimer() throws Exception {
    final Random random = new Random();      
    
    Pang pang = new PangMqtt();
    
    long period = PangProperties.getPeriod(); //Milliseconds
    pang.startTimerTask("test-device", new DataCallback<String>() {
      
      public String getData() {
        return ""+random.nextInt(100);
      }
      
      public boolean isRunning(int sentCount) {
        return true;
      }
      
      public void onSuccess(String sent) {}
      
    }, period, TimeUnit.MILLISECONDS);
    
    pang.waitTimerTask();
    
  }
  
}
