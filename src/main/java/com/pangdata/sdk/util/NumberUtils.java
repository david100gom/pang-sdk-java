package com.pangdata.sdk.util;


public class NumberUtils {

  public static double rountTo2decimal(double d) {
    return Math.round(d * 100.0) / 100.0;
  }

  public static Object toObject(String value) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {}
    try {
      return Double.valueOf(value);
    } catch (NumberFormatException e) {}
    if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
      return Boolean.valueOf(value);
    }
    return value;
  }
  
}
