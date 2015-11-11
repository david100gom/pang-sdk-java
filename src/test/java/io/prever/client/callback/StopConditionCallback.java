package io.prever.client.callback;

import io.prever.client.domain.Sensor;
import io.prever.sdk.callback.DataCallback;
import io.prever.sdk.util.JsonUtils;

import java.util.Date;

public class StopConditionCallback implements DataCallback<String> {

  private Sensor sensor = new Sensor();

  private int loopCount;

  public StopConditionCallback() {
    this(3);
  }

  public StopConditionCallback(int i) {
    this.loopCount = i;
  }

  public String getData() {
    sensor.setHumidity((int) (Math.random() * 30 + 30));
    sensor.setTemperature((int) (Math.random() * 20 + 20));
    sensor.setTimeStamp(new Date());
    return JsonUtils.convertObjToJsonStr(sensor);
  }

  public boolean isRunning(int count) {
    return count <= loopCount;
  }

  public void onSuccess(String data) {}
};
