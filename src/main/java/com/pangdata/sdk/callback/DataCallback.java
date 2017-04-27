package com.pangdata.sdk.callback;

public interface DataCallback<T> {

  public T getData();

  public boolean isRunning(int sentCount);
  
  public void onSuccess(T sent);

}
