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
package com.pangdata.sdk;

import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.timer.TimerDataSender;
import com.pangdata.sdk.timer.TimerTask;

public abstract class AbstractPrever implements Pangdata {
  private TimerDataSender timerDataSender;
  
  public AbstractPrever() {
    timerDataSender = new TimerDataSender();
  }
  
  public void startTimerTask(final String devicename, final DataCallback dataCallback, long period,
      TimeUnit unit) {
    validateArgs(dataCallback, period, unit);

    timerDataSender.available();

    timerDataSender.setTask(new TimerTask<String>() {
      public boolean execute(String data) {
        return sendData(devicename, data);
      }
    }, period, unit);

    timerDataSender.setDataCallback(dataCallback);
    timerDataSender.start();
  }
  
  public void startTimerTask(final MultipleDataCallback dataCallback, long period,
      TimeUnit unit) {
    validateArgs(dataCallback, period, unit);
    
    timerDataSender.available();
    
    timerDataSender.setTask(new TimerTask<Object>() {
      public boolean execute(Object obj) {
        return sendData(obj);
      }
    }, period, unit);
    
    timerDataSender.setDataCallback(dataCallback);
    timerDataSender.start();
  }

  public void waitTimerTask() {
    timerDataSender.await();
  }

  public void waitTimerTask(long timeout, TimeUnit unit) {
    timerDataSender.await(timeout, unit);
  }

  public void stopTimerTask() {
    timerDataSender.cancel();
  }
  
  protected void close() {
    if (timerDataSender != null) {
      timerDataSender.destroy();
    }
  }
  
  protected void validateArgs(DataCallback dataCallback, long period, TimeUnit unit) {
    if (dataCallback == null) {
      throw new IllegalArgumentException("DataCallback must not be null");
    }
    
    long convert = TimeUnit.SECONDS.convert(period, unit);
    if(convert < 4) {
      throw new IllegalArgumentException("the period argument must be greater than 4");
    }    
  }
  
}
