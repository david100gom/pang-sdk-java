package com.pangdata.sdk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DevicenameUtils {

  private static final int devicenameMaxSize = 90;
  private static final Pattern devicenameValidPattern = Pattern.compile("^[a-zA-Z0-9_-]+$");
  
  private static final Map<String, Boolean> ok = new HashMap<String, Boolean>();
  private static final Map<String, Boolean> error = new HashMap<String, Boolean>();
  
  public static void validate(String devicename) {
    if(devicename == null) {
      throw new IllegalArgumentException("Devicename is null");
    }
    if(ok.containsKey(devicename)) {
    	return;
    }
    
    if(error.containsKey(devicename)) {
      throw new IllegalArgumentException("Devicename("+devicename+") is invalid");
    }
    
	if(devicename.trim().isEmpty() || devicename.length() > devicenameMaxSize || !devicenameValidPattern.matcher(devicename).matches()) {
		error.put(devicename, true);
		throw new IllegalArgumentException("Devicename("+devicename+") is invalid");
	}
	//For performance
	ok.put(devicename, true);
  }
  
  public static void checkDeviceNames(Map<String, Object> map) {
    for (String devicename : map.keySet()) {
      DevicenameUtils.validate(devicename);
    }
  }
}
