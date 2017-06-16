package com.pangdata.sdk.mqtt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.mqtt.client.PangMqttClient;
import com.pangdata.sdk.util.PangProperties;

public class PangMqttTests {

  @Test
  public void pangMqttConstructor() throws Exception {
    Pang pang = new PangMqtt("your username", "your userkey");
    
    Random r = new Random();   
    
    
    pang.sendData("example_temperature", String.valueOf(r.nextInt(200)));    
  }
  
  @Test
  public void sendLargeData() throws Exception {
	  Pang pang = new PangMqtt();
	  Random r = new Random();
//	  String values = "23434343szdfasdfasdfadsfadsfasdfadsfadfasdfa";
	  String values = "javaw#2=cpu:15.16,mem:106248,tcount:28   "+
	      "MsMpEng=cpu:3.43, mem:157384, tcount:30   "+
	      " explorer=cpu=1.67, mem:34984, tcount:82"+
	      " Taskmgr=cpu:1.46,mem:7964,tcount:20"+
	      " dwm=cpu:0.84,mem:37976,tcount:10"+
	      " System=cpu:0.47,mem:28,tcount:163"+
	      " java#2=cpu:0.3,mem:57884,tcount:34"+
	      " svchost#4=cpu:0.3,mem:20304,tcount:84"+
	      " WmiPrvSE#2=cpu:0.2,mem:8644,tcount:11"+
	      " SecureCRT=cpu:0.14,mem:9708,tcount:17";
	  
	  pang.sendData("josh-top-process", values);
  }
  
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
			  nextInt = random.nextInt(200);
			  data.put("test-device2", nextInt);
			  
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
