package com.pangdata.sdk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DevicenameUtils {

  private static final int devicenameMaxSize = 90;
  private static final Pattern devicenameValidPattern = Pattern.compile("^[a-zA-Z0-9_-]+$");
  
  private static final Map<String, Boolean> ok = new HashMap<String, Boolean>();
  
  public static boolean isInvalid(String devicename) {
    if(devicename == null) {
    	return true;
    }
    if(ok.containsKey(devicename)) {
    	return false;
    }
	if(devicename.trim().isEmpty() || devicename.length() > devicenameMaxSize || !devicenameValidPattern.matcher(devicename).matches()) {
		return true;
	}
	//For performance
	ok.put(devicename, true);
	return false;
  }
}
