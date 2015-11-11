#Prever.io SDK for Java

This project provides a client library in Java that makes it easy to connect Prever.io web cloud services. For documentation please see the <a href="http://prever.io/public/pa/sdk.html" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the [Prever SDK for Java Features Wiki page] (https://github.com/prever/prever-sdk-java/wiki/Prever-SDK-for-Java-Features).

#Getting Started
You will need Java **v1.5+**. If you would like to develop on the SDK, you will also need gradle.

##Via Git
If using package management is not your thing, then you can grab the sdk directly from source using git. To get the source code of the SDK via git just type:
```bash
git clone https://github.com/prever/prever-sdk-java.git
cd ./prever-sdk-for-java/
```

## Example
```bash
package io.prever.apps.monitor;

import io.prever.sdk.Prever;
import io.prever.sdk.callback.MultipleDataCallback;
import io.prever.sdk.http.PreverHttp;
import io.prever.sdk.util.PreverProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        
        data.put("your device name", your value);

        return data;
      }
      
    }, PreverProperties.getPeriod(), TimeUnit.MILLISECONDS);

  }

}
```

### Getting Started Samples
We have a collection of getting started samples which will show you how to develop your IoT devices and any applications that your want play with it. Please visit and install it then you will find out what Prever.io is at <a href="https://github.com/prever-apps/" target="_blank">Prever applications</a>.

## Need some help?
Please send us your issues and problems using our feedback in Prever.io.

#Contribute Code
If you would like to become an active contributor to this project please contact us using feedback in Prever.io.
You can become a developer of Prever-apps and contribute your great applciations.
