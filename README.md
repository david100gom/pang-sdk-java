#Prever.io SDK for Java

This project provides a client library in Java that makes it easy to connect Pang Data web cloud services. For documentation please see the <a href="http://pangdata.com/public/pa/sdk.html" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the <a href="https://github.com/pangdata/pang-sdk-java/wiki/Pang-Data-SDK-for-Java-Features" target="_blank">Pang Data SDK for Java Features Wiki page</a>.

### Getting Started with Prever applications
We have a collection of getting started samples which will show you how to develop your IoT devices and any applications that you want to play with it. Please visit and install it then you will find out what Prever.io is at <a href="https://github.com/pang-apps/" target="_blank">Prever applications</a>.

#Prerequisites
You will need Java **v1.5+**. If you would like to develop on the SDK, you will also need gradle.

##Via Git
If using package management is not your thing, then you can grab the sdk directly from source using git. To get the source code of the SDK via git just type:
```bash
git clone https://github.com/pangdata/pang-sdk-java.git
cd ./pang-sdk-for-java/
```

## Example
```bash
package com.pangdata.apps.monitor;

import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.http.PangHttp;
import com.pangdata.sdk.util.PangProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PangSDKExample {
  
  public static void main(String[] args) throws Exception {
    final Pang pang = new PangHttp();

    pang.startTimerTask(new MultipleDataCallback() {
      public void onSuccess(Object sent) {}

      public boolean isRunning(int sentCount) {
        return true;
      }

      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("your device name", your device value);
        data.put("your device name2", your device value);
        return data;
      }
      
    }, PangProperties.getPeriod(), TimeUnit.MILLISECONDS);
  }
}
```

### Properties(pang.properties)
Prever SDK uses **pang.properties** in classpath. This file contains username and user key to authenticate Pangdata.com.

You can declare your own properties in that file. 
```bash
#Pang Data reserved properties
pang.username=your user name in pangdata.com
pang.userkey=your user key in pangdata.com

# Search schedule period(seconds)
pang.period = 10

```
PangProperties API provides getter method to get your properties. Below code is to get the properties 
```bash
final String devicename = (String) PangProperties.getProperty("your property key");
```

## Need some help?
Please send us your issues and problems using our feedback in Pangdata.com.

#Contribute Code
If you would like to become an active contributor to this project please contact us using feedback in Pangdata.com.
You can become a developer of Pang-apps and contribute your great applciations.
