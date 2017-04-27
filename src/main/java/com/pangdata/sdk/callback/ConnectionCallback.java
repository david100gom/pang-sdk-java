package com.pangdata.sdk.callback;

public interface ConnectionCallback {

  public void onConnectionSuccess();

  public void onConnectionFailure(Throwable cause);

  public void onConnectionLost(Throwable cause);
  
}
