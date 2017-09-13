package com.pangdata.sdk.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.PangFactory;

public class PangDemoScenario1 extends PangPropertyLoder {
  static double value2 = 100; 
  
  public static void main(String[] args) throws Exception {
    PangDemoScenario1 app = new PangDemoScenario1();
    app.load("pang.properties");
    final Map<String, Object> dataMap = new HashMap<String, Object>();

    final Pang httpClient =
        PangFactory.createHttpClient(app.getProperty("username"), app.getProperty("userKey"), app.getProperty("serverUri", "http://pangdata.com"));


    final Random generator = new Random();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        try {
          double value;
          double nextInt = generator.nextInt(200);
          if (nextInt == 0) {
            double anomal = generator.nextDouble()*20+10;
            value = 50 - anomal;
            value2 -= 0.3 * anomal;
          } else {
            value = 50 + generator.nextDouble()*4 - 2;
            if (value2 != 100) {
              value2 += 0.1;
              if (value2 > 100) {
                value2 = 100;
              }
            }
          }
          dataMap.put("oil_flow", value);
          dataMap.put("success_rate", value2);
         

          httpClient.sendData(dataMap);
        } catch (Throwable e) {

        }

      }
    }, 0, Long.valueOf(app.getProperty("period", "60")) * 1000);
    timer.wait();
    httpClient.disconnect();
  }
  
}
