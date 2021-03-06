package com.pangdata.sdk.mqtt.connector;


public interface ConnectionCallback {
	public void onSuccess();

/**
 * Is called every 3 seconds until it connects to the broker. 
 */
	public void onFailure(Throwable e);
}
