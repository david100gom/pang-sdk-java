package com.pangdata.sdk.util;

import java.util.regex.Pattern;

public class DevicenameUtils {

  private static final int devicenameMaxSize = 90;
  private static final Pattern devicenameValidPattern = Pattern.compile("^[a-zA-Z0-9_-]+$");
  
  public static boolean isValid(String devicename) {
    if(devicename == null || 
        devicename.trim().isEmpty() ||
        devicename.length() > devicenameMaxSize || 
        !devicenameValidPattern.matcher(devicename).matches()
        ) {
      return true;
    } else {
      return false;
    }
  }
}
