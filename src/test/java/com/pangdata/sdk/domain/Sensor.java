package com.pangdata.sdk.domain;

import java.util.Date;

public class Sensor {

  private int temperature;

  private int humidity;

  private Date timeStamp;

  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public String getTimeStamp() {
    return timeStamp.toString();
  }

  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }

}
