/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Preversoft
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.mqtt.PangMqttV1;
import com.pangdata.sdk.util.PangProperties;

public class PangTaskTimerExample {
  private static final Logger logger = LoggerFactory.getLogger(PangTaskTimerExample.class);

  private static Random random = new Random();
  private static final String[] status = new String[] {"GOOD", "BAD", "NONE"};

  public static void main(String[] args) throws Exception {
    final Pang pang = new PangMqttV1();

    long period = PangProperties.getPeriod(); // Milli seconds
    pang.startTimerTask(new MultipleDataCallback() {

      public void onSuccess(Object sent) {
        logger.info("Sent: {}", sent);
      }

      public boolean isRunning(int sentCount) {
        return true;
      }

      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();

        int nextInt = random.nextInt(100);
        data.put("randomInteger", nextInt);

        double nextFloat = random.nextGaussian() * 8.0f + 50;
        data.put("randomFloat", nextFloat);

        int index = random.nextInt(3);
        data.put("randomString", status[index]);

        boolean nextBoolean = random.nextBoolean();
        data.put("randomBoolean", nextBoolean);
        return data;
      }

    }, period, TimeUnit.MILLISECONDS);
  }
}
