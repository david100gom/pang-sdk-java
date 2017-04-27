package com.pangdata.sdk.callback;



public abstract class InfiniteDataSendingCallback implements DataCallback<String> {

  public boolean isRunning(int count) {
    return true;
  }

  public void onSuccess(String data) {}
}
