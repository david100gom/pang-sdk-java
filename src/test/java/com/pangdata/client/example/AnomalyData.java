package com.pangdata.client.example;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.PangdataFactory;

public class AnomalyData {
  static String lastValue = null;

  public static void main(String[] args) throws Exception {

    final Pangdata httpClient =
//        PreverClientFactory.createHttpClient("demo", "Fj8QBK", "http://192.168.56.101:9191");
        PangdataFactory.createHttpClient("demo", "Fj8QBK", "http://192.168.0.4:9191");
    final Random generator = new Random();
    //2pi = 6.28....
    final double period = 1000;
    final double amplitude = 10;
    

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        String value= null;
        double sin = Math.sin(System.currentTimeMillis()/period);
        value = ((Double)(amplitude * sin)).toString();
        System.out.println((new Date()).toString() + "@" + value);
        
        
        try {
//          httpClient.sendData("test_sin", value);
        } catch (Throwable e) {

        }

      }
    }, 0, 1 * 1000);
    timer.wait();
    httpClient.disconnect();

  }

}
