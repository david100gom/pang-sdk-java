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

package com.pangdata.client.example;

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
