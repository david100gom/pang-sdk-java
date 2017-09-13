package com.pangdata.sdk;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.timer.TimerDataSender;
import com.pangdata.sdk.timer.TimerTask;

public abstract class AbstractPang implements Pang {
  private TimerDataSender timerDataSender;
  
  public AbstractPang() {
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
    
    timerDataSender.setTask(new TimerTask<Map<String, Object>>() {
      public boolean execute(Map<String, Object> obj) {
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
