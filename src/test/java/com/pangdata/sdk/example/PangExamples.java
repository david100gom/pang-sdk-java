package com.pangdata.sdk.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.http.PangHttp;
import com.pangdata.sdk.util.PangProperties;

public class PangExamples {
  private static final Logger logger = LoggerFactory.getLogger(PangExamples.class);
 
  public static void main(String[] args) throws Exception {
    // Pang must be initialized first to use prever.properties by PangProperties
    final Pang pang = new PangHttp();

    long period = PangProperties.getPeriod(); //seconds
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      
      @Override
      public void run() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("your device name", "your device value");
        data.put("your device name2", "your device value");
        pang.sendData(data);
      }
    }, 0, period);

  }

}
