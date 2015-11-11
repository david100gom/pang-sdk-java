package io.prever.client.example;

import io.prever.sdk.Prever;
import io.prever.sdk.PreverFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PreverDemoScenario1 extends PreverPropertyLoder {
  static double value2 = 100; 
  
  public static void main(String[] args) throws Exception {
    PreverDemoScenario1 app = new PreverDemoScenario1();
    app.load("prever.properties");
    final Map<String, Number> dataMap = new HashMap<String, Number>();

    final Prever httpClient =
        PreverFactory.createHttpClient(app.getProperty("username"), app.getProperty("userKey"), app.getProperty("serverUri", "http://prever.io"));


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
