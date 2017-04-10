package com.pangdata.sdk.util;

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilsTests {

  @Test
  public void dataTypeTest() {
    Object object = NumberUtils.toObject("12");
    Assert.assertTrue(object instanceof Integer);
    
    object = NumberUtils.toObject("12.1");
    Assert.assertTrue(object instanceof Double);
    
    object = NumberUtils.toObject("12.1d");
    Assert.assertTrue(object instanceof Double);
    
    
    object = NumberUtils.toObject("122M");
    Assert.assertTrue(object instanceof String);
    
    object = NumberUtils.toObject("TRUE");
    Assert.assertTrue(object instanceof Boolean);
    
  }

}
