package com.pangdata.sdk.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PangPropertyLoder {
  Properties prop = null;
  protected void load(String filename) {
    prop = new Properties();
    InputStream input = null;

    try {
        input = getClass().getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            System.out.println("Sorry, unable to find " + filename);
            return;
        }

        prop.load(input);
    } catch (IOException ex) {
        ex.printStackTrace();
    } finally {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        
  }
  
  protected String getProperty(String key, String defaultValue) {
    return prop.getProperty(key, defaultValue);
  }
  
  protected String getProperty(String key) {
    return prop.getProperty(key);
  }
}
