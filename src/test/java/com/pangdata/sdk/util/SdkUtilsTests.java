package com.pangdata.sdk.util;

import org.junit.Test;

public class SdkUtilsTests {
  @Test
  public void spaceReplaceTest() {
    String devicename1 = "devicename ";
    String devicename2 = "devicename  ";
    String replaceSpaceCharacter1 = SdkUtils.replaceSpaceCharacter(devicename1);
    String replaceSpaceCharacter2 = SdkUtils.replaceSpaceCharacter(devicename2);
    
    System.out.println(replaceSpaceCharacter1);
    System.out.println(replaceSpaceCharacter2);
  }
  
  @Test
  public void regexTest() {
    String devicename = "devicename ";
    String replaceAll = devicename.replaceAll("[\\s+]", "");
    System.out.println(devicename);
    System.out.println(replaceAll);
  }
}
