package com.pangdata.sdk.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private final static ObjectMapper om = new ObjectMapper();

  public static String convertObjToJsonStr(Object dataObj) {
    try {
      return om.writeValueAsString(dataObj);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String getValueInJsonStr(String key, String jsonStr) {
    Map<String, String> map;
    try {
      map = om.readValue(jsonStr, new TypeReference<HashMap<String, String>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    if (map != null) {
      return map.get(key);
    } else {
      return null;
    }
  }

  public static <T> T toObject(String content, Class obj) {
    try {
      return (T) om.readValue(content, obj);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
