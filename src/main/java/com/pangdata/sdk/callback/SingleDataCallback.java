package com.pangdata.sdk.callback;

public interface SingleDataCallback extends DataCallback<String>{

  public String getData();

  public boolean isRunning(int count);
  
  public void onSuccess(String data);

}
