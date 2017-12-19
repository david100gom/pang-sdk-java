package com.pangdata.sdk.util;

import org.junit.Test;

public class DevicenameUtilsTests {

  @Test
  public void space() {
    DevicenameUtils.validate("ab");
    DevicenameUtils.validate("a b");
  }
}
