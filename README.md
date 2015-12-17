#Prever.io SDK for Java

This project provides a client library in Java that makes it easy to connect Prever.io web cloud services. For documentation please see the <a href="http://prever.io/public/pa/sdk.html" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the <a href="https://github.com/prever/prever-sdk-java/wiki/Prever-SDK-for-Java-Features" target="_blank">Prever SDK for Java Features Wiki page</a>.

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
package io.prever.apps.monitor;

import io.prever.sdk.Prever;
import io.prever.sdk.callback.MultipleDataCallback;
import io.prever.sdk.http.PreverHttp;
import io.prever.sdk.util.PreverProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PreverSDKExample {
  private static final Logger logger = LoggerFactory.getLogger(PreverSDKExample.class);

  public static void main(String[] args) throws Exception {
    final Prever prever = new PreverHttp();

    prever.startTimerTask(new MultipleDataCallback() {
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
      
    }, PreverProperties.getPeriod(), TimeUnit.MILLISECONDS);
  }
}
```

### Properties(prever.properties)
Prever SDK uses **prever.properties** in classpath. This file contains username and user key to authenticate Prever.io.

You can declare your own properties in that file. 
```bash
#Prever.io reserved properties
prever.username=your user name in prever.io
prever.userkey=your user key in prever.io

# Search schedule period(seconds)
prever.period = 10

#Naver Top application reserved properties
navertop.devicename=naver_top
```
PreverProperties API provides getter method to get your properties. Below code is to get the properties 
```bash
final String devicename = (String) PreverProperties.getProperty("navertop.devicename");
```

## Need some help?
Please send us your issues and problems using our feedback in Prever.io.

#Contribute Code
If you would like to become an active contributor to this project please contact us using feedback in Prever.io.
You can become a developer of Prever-apps and contribute your great applciations.
