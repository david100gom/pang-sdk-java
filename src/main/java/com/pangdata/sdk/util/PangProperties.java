package com.pangdata.sdk.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PangProperties {
	
  private static final Logger logger = LoggerFactory.getLogger(PangProperties.class);
  private static final int _DEFAULT_PERIOD = 10000;
  private static Properties props;
  private static boolean loaded;
  
  public static synchronized Properties loadPangProperties() throws IOException {
    if(props != null) {
      return props;
    }
    loaded = true;
    logger.info("Loading pang.properties in your classpath");
    InputStream is = SdkUtils.class.getResourceAsStream("/pang.properties");
    if(is == null) {
      throw new IOException("Could not load the file pang.properites in your classpath");
    }
    props = new Properties();
    props.load(is);
    is.close();
    
    return props;
  }
  
  public static void setProperties(Properties props) {
    PangProperties.props = props;
  }

  public static Properties getProperties() {
    checkNull();
    return props;
  }

  public static boolean isEnabledToSend() {
	  String enabled = (String) get("pang.enabled");
	    if(enabled != null && enabled.equalsIgnoreCase("false")) {
	      return false;
	    }
	  return true;
  }
  
  private static String get(String key) {
	checkNull();
	return props.getProperty(key);
  }

/**
   * Time unit is seconds.
   * @return period of schedule
   */
  public static long getPeriod() {
    String period = (String) get("pang.period");
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
	if(!loaded) {
		try {
			loadPangProperties();
		} catch (IOException e) {
			logger.error("error", e);
		}
	}
    if(props == null) {
      throw new IllegalStateException("Properties object must not be null");
    }
  }

  public static Object getProperty(String key) {
    checkNull();
    return props.get(key);
  }

  public static Map<Integer, Map<String, String>> extractPrefixedMultipleProperties(String prefix) {
	  checkNull();
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
  
  public static List<String> extractPrefixedKey(String prefix) {
	  checkNull();
		Set<Entry<Object, Object>> entrySet = props.entrySet();
		Iterator<Entry<Object, Object>> iterator = entrySet.iterator();

		List<String> properties = new ArrayList<String>();

		while (iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			String key = (String) next.getKey();
			if (key.startsWith(prefix)) {
				String sub1 = key.substring(key.indexOf(".") + 1);

				properties.add(sub1);
			}
		}
		return properties;
  }
  
  public static Map<String, Object> extractPrefixedProperties(String prefix) {
    checkNull();
      Set<Entry<Object, Object>> entrySet = props.entrySet();
      Iterator<Entry<Object, Object>> iterator = entrySet.iterator();

      Map<String, Object> properties = new HashMap<String, Object>();

      while (iterator.hasNext()) {
          Entry<Object, Object> next = iterator.next();
          String key = (String) next.getKey();
          if (key.startsWith(prefix)) {
              properties.put(key, next.getValue());
          }
      }
      return properties;
  }
  
  public static Map<Integer, Map<String, String>> extractVariableProperties(String prefix) {
	    Properties properties = PangProperties.getProperties();
	    Set<Entry<Object, Object>> entrySet = properties.entrySet();
	    Iterator<Entry<Object, Object>> iterator = entrySet.iterator();

	    Map<Integer, Map<String, String>> targets = new HashMap<Integer, Map<String, String>>();

	    while (iterator.hasNext()) {
	      Entry<Object, Object> next = iterator.next();
	      String key = (String) next.getKey();
	      if (key.startsWith(prefix)) {
	        String sub1 = key.substring(key.indexOf(".") + 1);
	        int indexOf = sub1.indexOf(".");
	        String number = sub1.substring(0, indexOf);
	        Map<String, String> target = targets.get(Integer.valueOf(number));
	        if (target == null) {
	          target = new HashMap<String, String>();
	          targets.put(Integer.valueOf(number), target);
	        }

	        target.put(sub1.substring(sub1.lastIndexOf(".") + 1).trim(), (String) next.getValue());
	      }
	    }

	    return targets;
	  }

}
