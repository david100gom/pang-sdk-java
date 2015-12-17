package com.pangdata.client.simulation;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.PDefaultMqttClient;

public class DataSharingTests {
  private static final Logger logger = LoggerFactory.getLogger(DataSharingTests.class);

  public static void main(String[] args) throws Exception {
    Thread t = new Thread() {
      public void run() {
        final CountDownLatch countDownLatch = new CountDownLatch(10);

        Pang client =
            new PDefaultMqttClient("james", "james-key-" + System.currentTimeMillis());
        try {
          client.connect("tcp://mini.prever.co.kr:1883");
        } catch (Exception e1) {
          e1.printStackTrace();
          return;
        }

        client.subscribeDataSharing("josh", "thing1", new DataSharingCallback() {
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1883({})", sharedData);
            countDownLatch.countDown();
          }
        });

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

        Pang client = new PDefaultMqttClient("john", "james-key-" + System.currentTimeMillis());
        try {
          client.connect("tcp://mini.prever.co.kr:1884");
        } catch (Exception e1) {
          e1.printStackTrace();
          return;
        }

        client.subscribeDataSharing("josh", "thing1", new DataSharingCallback() {
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1884({} for john)", sharedData);
            countDownLatch.countDown();
          }
        });

        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    t2.start();

    Thread t3 = new Thread() {
      public void run() {
        final CountDownLatch countDownLatch = new CountDownLatch(10);

        Pang client =
            new PDefaultMqttClient("james", "james-key-" + System.currentTimeMillis());
        try {
          client.connect("tcp://mini.prever.co.kr:1884");
        } catch (Exception e1) {
          return;
        }

        client.subscribeDataSharing("josh", "thing1", new DataSharingCallback() {
          public void onSharedDataArrived(String sharedData) {
            logger.info("Shared data arrvied from 1884({}) for james", sharedData);
            countDownLatch.countDown();
          }
        });

        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    t3.start();

    t.join();
    t2.join();
    t3.join();
  }
}
