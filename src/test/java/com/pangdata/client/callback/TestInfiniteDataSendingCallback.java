package com.pangdata.client.callback;

import java.util.Date;

import com.pangdata.client.domain.Sensor;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.util.JsonUtils;

public class TestInfiniteDataSendingCallback implements DataCallback<String> {

  private Sensor sensor = new Sensor();

  public String getData() {
    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());
    return JsonUtils.convertObjToJsonStr(sensor);
  }

  public boolean isRunning(int count) {
    return true;
  }

  public void onSuccess(String data) {
    System.out.println("onSuccess:" + data);
  }
}
