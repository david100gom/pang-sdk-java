#Pang Data SDK for Java

This project provides a client library in Java that makes it easy to connect Pang Data web cloud services. For documentation please see the <a href="http://pangdata.com/public/pa/sdk.html" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the <a href="https://github.com/pangdata/pang-sdk-java/wiki/Pang-Data-SDK-for-Java-Features" target="_blank">Pang Data SDK for Java Features Wiki page</a>.

# Translations

- [Korean](https://github.com/pangdata/pang-sdk-java/blob/master/README-i18n/README-ko-kr.md)
- [Chinese](https://github.com/pangdata/pang-sdk-java/blob/master/README-i18n/README-zh-cn.md)

### Getting Started with Pang Data applications
We have a collection of getting started samples which will show you how to develop your IoT devices and any applications that you want to play with it. Please visit and install it then you will find out what Pangdata.com is at <a href="https://github.com/pang-apps/" target="_blank">Pang Data applications</a>.

# Getting Start SDK

# Step 0 : Check prerequisites
You will need Java **v1.5+**. If you would like to develop on the SDK, you will also need gradle.

# Step 1 : Get SDK
### Using Maven
If you want to include Pang sdk to your project, add your **pom.xml** as follows:
```
...
<repositories>
    ...
    <repository>
        <id>pang-data-repo</id>
        <name>pang-data-repo</name>
        <url>http://mini.prever.co.kr:8081/nexus/content/groups/prever-io-public-repository/</url>
    </repository>
</repositories>
...
<dependencies>
    ...
    <dependency>
        <groupId>com.pangdata</groupId>
        <artifactId>pang-sdk-java</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
    ...
}
```
### Using Gradle
If you want to include Pang sdk to your project, add your **build.gradle** as follows:
```
...
apply plugin: 'maven'
...
repositories {
    ...
    maven{
        url "http://mini.prever.co.kr:8081/nexus/content/groups/prever-io-public-repository/"
    }
    ...
}
dependencies {
    ...
    compile group: 'com.pangdata', name: 'pang-sdk-java', version: '1.0.0-SNAPSHOT'
    ...
}
```
### Using Git
If using package management is not your thing, then you can grab the sdk directly from source using git. To get the source code of the SDK via git just type:
```bash
git clone https://github.com/pangdata/pang-sdk-java.git
cd ./pang-sdk-java/
```
### Download jar & code
If you want to download this sdk source code, go to the below link and download the zip file.
https://github.com/pangdata/pang-sdk-java/releases/latest

# Step 2 : Write Example
## Example 1 : Sending random number using Pang's task timer
src link : https://github.com/pangdata/pang-sdk-java/blob/master/src/examples/java/com/pangdata/client/example/PangTaskTimerExample.java
```java
package com.pangdata.client.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.http.PangHttp;
import com.pangdata.sdk.util.PangProperties;

public class PangTaskTimerExample {
  private static Random random = new Random();;
  private static final String[] status = new String[]{"GOOD", "BAD", "NONE"};

  public static void main(String[] args) throws Exception {
    final Pang pang = new PangHttp();
    
    long period = PangProperties.getPeriod(); //seconds
    pang.startTimerTask(new MultipleDataCallback() {

      public void onSuccess(Object sent) {}

      public boolean isRunning(int sentCount) {
        return true;
      }

      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        int nextInt = random.nextInt(100);
        data.put("randomInteger", nextInt);
        
        double nextFloat = random.nextGaussian() * 8.0f + 50;
        data.put("randomFloat", nextFloat);
        
        int index = random.nextInt(3);
        data.put("randomString", status[index]);
        
        boolean nextBoolean = random.nextBoolean();
        data.put("randomBoolean", nextBoolean);
        
        return data;
      }
      
    }, period, TimeUnit.MILLISECONDS);
  }
}
```

## Example 2 : Sending random number Using JDK Timer class
src link : https://github.com/pangdata/pang-sdk-java/blob/master/src/examples/java/com/pangdata/client/example/JavaUtilTimerExample.java
```java
package com.pangdata.client.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.http.PangHttp;
import com.pangdata.sdk.util.PangProperties;

public class JavaUtilTimerExample {
  private static Random random = new Random();;
  private static final String[] status = new String[]{"GOOD", "BAD", "NONE"};

  public static void main(String[] args) throws Exception {
    final Pang pang = new PangHttp();

    long period = PangProperties.getPeriod(); //seconds
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      
      @Override
      public void run() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        int nextInt = random.nextInt(100);
        data.put("randomInteger", nextInt);
        
        double nextFloat = random.nextGaussian() * 8.0f + 50;
        data.put("randomFloat", nextFloat);
        
        int index = random.nextInt(3);
        data.put("randomString", status[index]);
        
        boolean nextBoolean = random.nextBoolean();
        data.put("randomBoolean", nextBoolean);
        
        pang.sendData(data);
      }
    }, 0, period);

  }
}
```

# Step 3 : Sign up pangdata.com account
For running example, you need the account and user key of pangdata.com.
Please sign up pangdata.com through the link below.

http://pangdata.com/pa/signUp

# Step 4 : Setup
### Properties(pang.properties)
Pang Data SDK requires **pang.properties** in classpath. This file contains username and user key to authenticate Pangdata.com.

You can declare your own properties in that file. 
```
#Pang Data reserved properties
pang.username=[[your user name in pangdata.com]]
pang.userkey=[[your user key in pangdata.com]]

# Search schedule period(seconds)
pang.period = 10
```
PangProperties API provides getter method to get your properties. Below code is to get the properties 
```java
final String devicename = (String) PangProperties.getProperty("your property key");
```
# Step 5 : See your data

## Need some help?
Please send us your issues and problems using our feedback in Pangdata.com.

#Contribute Code
If you would like to become an active contributor to this project please contact us using feedback in Pangdata.com.
You can become a developer of Pang-apps and contribute your great applciations.
