/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Preversoft
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.pangdata.sdk.util;

import java.util.Properties;

public class PangProperties {
  private static final int _DEFAULT_PERIOD = 10000;
  private static Properties props;
  
  public static void setProperties(Properties props) {
    PangProperties.props = props;
  }

  public static Properties getProperties() {
    return props;
  }

  /**
   * Time unit is seconds.
   * @return period of schedule
   */
  public static long getPeriod() {
    checkNull();
    String period = (String) props.get("prever.period");
    if(period == null) {
      return _DEFAULT_PERIOD; // ten seconds
    }
    long lPeriod = Long.valueOf(period.trim());
    if(lPeriod <= 0) {
      return _DEFAULT_PERIOD; // ten seconds
    }
    return lPeriod * 1000;
  }

  private static void checkNull() {
    if(props == null) {
      throw new IllegalStateException("Properties object must not be null. This class must be used after initialized the class PreverHttp.");
    }
  }

  public static Object getProperty(String key) {
    checkNull();
    return props.get(key);
  }

}
