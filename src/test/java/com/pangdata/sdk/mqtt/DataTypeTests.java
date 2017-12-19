package com.pangdata.sdk.mqtt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.PangFactory;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClientV1;

public class DataTypeTests {

  final Random r = new Random();
  
  @Test
  public void sendrows() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    
    Pang pang = new PangMqttClientV1("josh");
    pang.setConnectionCallback(new ConnectionCallback() {
      
      public void onConnectionSuccess() {
        latch.countDown();
      }
      
      public void onConnectionLost(Throwable cause) {
        cause.printStackTrace();
        latch.countDown();
      }
      
      public void onConnectionFailure(Throwable cause) {
        cause.printStackTrace();
        latch.countDown();
      }
    });
    
    pang.connect("tcp://localhost");

    latch.await();
    
    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>> ();
    Map<String, Object> e = new HashMap<String, Object> ();
    e.put("temperature", 1.2);
    rows.add(e);
    e = new HashMap<String, Object> ();
    e.put("temperature", 2.2);
    rows.add(e);
    pang.sendData(rows);
    System.out.println("sent");
  }
  
  @Test
  public void sendrows2() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    Pang pang = PangFactory.createReassignableMqttClient("josh", "o5w_Cx", "http://localhost");
    
    latch.await(3, TimeUnit.SECONDS);

    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>> ();
    Map<String, Object> e = new HashMap<String, Object> ();
    e.put("temperature", 1.2);
    rows.add(e);
    e = new HashMap<String, Object> ();
    e.put("temperature", 2.2);
    rows.add(e);
    pang.sendData(rows);
    System.out.println("sent");
  }
}
