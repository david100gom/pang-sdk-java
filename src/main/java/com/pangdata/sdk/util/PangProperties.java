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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class PangProperties {
  private static final int _DEFAULT_PERIOD = 10000;
  private static Properties props;
  
  public static void setProperties(Properties props) {
    PangProperties.props = props;
  }

  public static Properties getProperties() {
    return props;
  }

  public static boolean isEnabledToSend() {
	  String enabled = (String) props.get("pang.enabled");
	    if(enabled != null && enabled.equalsIgnoreCase("false")) {
	      return false;
	    }
	  return true;
  }
  /**
   * Time unit is seconds.
   * @return period of schedule
   */
  public static long getPeriod() {
    checkNull();
    String period = (String) props.get("pang.period");
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
      throw new IllegalStateException("Properties object must not be null. This class must be used after initialized the class PangdataHttp.");
    }
  }

  public static Object getProperty(String key) {
    checkNull();
    return props.get(key);
  }

  public static Map<Integer, Map<String, String>> extractPrefixedMultipleProperties(String prefix) {
		Set<Entry<Object, Object>> entrySet = props.entrySet();
		Iterator<Entry<Object, Object>> iterator = entrySet.iterator();

		Map<Integer, Map<String, String>> prefxiedProperties = new HashMap<Integer, Map<String, String>>();

		while (iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			String key = (String) next.getKey();
			if (key.startsWith(prefix)) {
				String sub1 = key.substring(key.indexOf(".") + 1);
				int indexOf = sub1.indexOf(".");
				String number = sub1.substring(0, indexOf);
				Map<String, String> property = prefxiedProperties.get(Integer
						.valueOf(number));
				if (property == null) {
					property = new HashMap<String, String>();
					prefxiedProperties.put(Integer.valueOf(number), property);
				}

				property.put(sub1.substring(sub1.lastIndexOf(".") + 1).trim(),
						(String) next.getValue());
			}
		}

		return prefxiedProperties;
	}
}
