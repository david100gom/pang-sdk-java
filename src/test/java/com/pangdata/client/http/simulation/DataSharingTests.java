package com.pangdata.client.http.simulation;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.MqttFailoverHttpClient;

public class DataSharingTests {
  private static final Logger logger = LoggerFactory.getLogger(DataSharingTests.class);

  public static void main(String[] args) throws Exception {
    Thread t = new Thread() {
      public void run() {
        final CountDownLatch countDownLatch = new CountDownLatch(10);

        Pangdata client =
            new MqttFailoverHttpClient("james", "james-key-" + System.currentTimeMillis(), null, new DataSharingCallback() {
              
              public void onSharedDataArrived(String sharedData) {
                logger.info("Shared data arrvied for james({})", sharedData);
                countDownLatch.countDown();
              }
            });
        try {
          client.connect("http://localhost:9191");
        } catch (Exception e1) {
          e1.printStackTrace();
          return;
        }

      /*  client.subscribeDataSharing("josh", "thing1", new DataSharingCallback() {
          @Override
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1883({})", sharedData);
            countDownLatch.countDown();
          }
        });*/

        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    t.start();

    Thread.sleep(10);

    Thread t2 = new Thread() {
      public void run() {
        final CountDownLatch countDownLatch = new CountDownLatch(10);

        Pangdata client = new MqttFailoverHttpClient("john", "james-key-" + System.currentTimeMillis(), null, new DataSharingCallback() {
          
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied for james({})", sharedData);
            countDownLatch.countDown();
          }
        });
        try {
          client.connect("http://localhost:9191");
        } catch (Exception e1) {
          e1.printStackTrace();
          return;
        }

        /*client.subscribeDataSharing("josh", "thing1", new DataSharingCallback() {
          @Override
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1884({} for john)", sharedData);
            countDownLatch.countDown();
          }
        });*/

        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    t2.start();

    t.join();
    t2.join();
  }
}
