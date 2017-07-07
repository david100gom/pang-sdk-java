package com.pangdata.sdk.util;

import org.junit.Test;

public class JSonUtilsTest {

  @Test
  public void testLargeData() {
    final String values = "true";
    System.out.println(NumberUtils.toObject("abc").getClass());
    System.out.println(NumberUtils.toObject("true").getClass());
    System.out.println(NumberUtils.toObject("1").getClass());
    System.out.println(NumberUtils.toObject("1.1").getClass());
    
/*    System.out.println(JsonUtils.toObject("abc", Object.class).getClass());
    System.out.println(JsonUtils.toObject("true", Object.class).getClass());
    System.out.println(JsonUtils.toObject("1", Object.class).getClass());
    System.out.println(JsonUtils.toObject("1.1", Object.class).getClass());*/
  }
}
