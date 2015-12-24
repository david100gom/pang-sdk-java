package com.pangdata.sdk.http.simulation;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.MqttFailoverHttpClient;

public class AudoReceivedDataSharingTests {
  private static final Logger logger = LoggerFactory.getLogger(AudoReceivedDataSharingTests.class);

  public static void main(String[] args) throws Exception {
    Thread t2 = new Thread() {
      public void run() {
        final CountDownLatch countDownLatch = new CountDownLatch(10);

        Pang client = new MqttFailoverHttpClient("james", "james-key-" + System.currentTimeMillis(), null, new DataSharingCallback() {
          
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1884({} for james)", sharedData);
            countDownLatch.countDown();
          }
        });
        try {
          client.connect("http://localhost:9191");
        } catch (Exception e1) {
          e1.printStackTrace();
          return;
        }

        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    t2.start();

    t2.join();
  }
}
