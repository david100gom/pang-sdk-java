package com.pangdata.sdk.callback;

public interface ControlCallback {

  public String execute(String controlKey, String data);

  public void onDeliveryComplete(String topic, String data);

}
