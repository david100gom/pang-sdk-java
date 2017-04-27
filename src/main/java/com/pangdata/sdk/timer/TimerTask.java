package com.pangdata.sdk.timer;

public interface TimerTask<T> {

  boolean execute(T data);

}
