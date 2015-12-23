# Pang Data SDK for Java

This project provides a client library in Java that makes it easy to connect Pang Data web cloud services. For documentation please see the <a href="http://pangdata.com/public/pa/sdk.html" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the <a href="https://github.com/pangdata/pang-sdk-java/wiki/Pang-Data-SDK-for-Java-Features" target="_blank">Pang Data SDK for Java Features Wiki page</a>.

# Translations

- [Korean](https://github.com/pangdata/pang-sdk-java/blob/master/README-i18n/README-ko-kr.md)
- [Chinese](https://github.com/pangdata/pang-sdk-java/blob/master/README-i18n/README-zh-cn.md)


# Getting Start SDK

## Step 0 : Check prerequisites
You will need Java **v1.5+**. If you would like to develop on the SDK, you will also need gradle.

## Step 1 : Get SDK
#### Using Maven
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
#### Using Gradle
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
#### Using Git
If using package management is not your thing, then you can grab the sdk directly from source using git. To get the source code of the SDK via git just type:
```bash
git clone https://github.com/pangdata/pang-sdk-java.git
cd ./pang-sdk-java/
```
### Download jar & code
If you want to download this sdk source code, go to the below link and download the zip file.
<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">latest version</a>
## Step 2 : Write Example
#### Example 1 : Sending random number using Pang's task timer
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/src/main/java/com/pangdata/client/example/PangTaskTimerExample.java" target="_blank">link to source</a>
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
  private static Random random = new Random();
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

#### Example 2 : Sending random number Using JDK Timer class
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/src/main/java/com/pangdata/client/example/JavaUtilTimerExample.java" target="_blank">link to source</a>
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
  private static Random random = new Random();
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

## Step 3 : Sign up pangdata.com account
For running example, you need the account and user key of pangdata.com.
Please sign up pangdata.com through the link below.

<a href="http://pangdata.com/pa/signUp" target="_blank">go to signup</a>

## Step 4 : Configuration & Run
Let's run pang example. We provides a sample application in the form of an executable.
Download **pang-sdk-java.zip/pang-sdk-java.tar** via below link.

<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">download link</a>

Unzip this file, config **pang.properties** and run this example. 
#### Config **pang.properties** file
Pang Data SDK requires **pang.properties** in classpath. This file contains **username** and **user key** to authenticate Pangdata.com.
###### How to find your user key.
1. login pangdata.com
2. go to Settings > Profile
3. copy user key to paste on 'pang.userkey'
```
#Pang Data reserved properties
pang.username=[[your user name in pangdata.com]]
pang.userkey=[[your user key in pangdata.com]]

# Search schedule period(seconds)
pang.period = 10
```
You can declare your own properties in that file.
PangProperties API provides getter method to get your properties. Below code is to get the properties 
```java
final String devicename = (String) PangProperties.getProperty("your property key");
```
#### Running example
Run the following command in this example directory.
###### windows
```
pang.bat
```
###### linux
```bash
./pang.sh
```
This example application is running successfully if following log is written.
```log
10:54:48.991 [main] TimerDataSender is started.
10:54:49.205 [pool-1-thread-1] Send data to server http://pangdata.com/api/data/put/XbuDm0/example
10:54:49.678 [pool-1-thread-1] Response: {"Data":"{\"randomInteger\":71,\"randomBoolean\":false,\"randomString\":\"GOOD\",\"randomFloat\":62.508314100247794}","Message":"Ok!","Success":true}
```

## Step 5 : See your data

Finally, it's time to see our example data with real-time dashboard of pangdata.com.
#### Log in pangdata.com
<a href="http://pangdata.com/pa/login" target="_blank">go to login</a>

#### Register Device

After login, you can see The overview. The overview shows the overall status of your account. click the **Total** count in **Unregistered Device**.
This screen is a new device list. You can see the detected data(randomInteger,randomFloat, randomString, randomBooean).
Pangdata automatically detect the data sent by the Pangdata client(example). 

Click the **+ button** to the right of the **"randomInteger"** in the list. **New device** window opens.
Check **do you wnat to add a widget?** and press **OK button** in the bottom right corner.
**"randomInteger"** is registered to the device of Pangdata.

#### Create Widget
Immediately the **Add a Widget** window opens.
Enter **"Line chart"** in the title input. To create a widget, you have to select a dashboard. Click and select **select dashboard** you want to add a widget.
First-time users will not have a dashboard. To create a new dashboard, input **"Example Dashboard"** in **select dashboard** and click the + button on right.
Finally click "OK" button. You can see dashboard named **"Example Dashboard"** and widget named **"Line chart"**.

#### See real-time update chart
let's see this widget. The line chart in this widget is changed to the 10-second intervals.(If **pang.period** is set to 10 in **pang.properties**)
You can also register another data(randomFloat, randomString, randomBooean) to device and widget with the same steps.
Pangdata provides a variety of widget type in addition to the line chart. Let's try another type widget.
![dashboard](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/getting_start_result.gif)

## Screen Shots
![dashboard_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/example_dashboard.gif)
![device_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/device_rowdata.JPG)
![analytics_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/analytics.JPG)

## Next Step : Do you need another Pang Data applications?
We have a collection of getting started samples which will show you how to develop your IoT devices and any applications that you want to play with it. Please visit and install it then you will find out what Pangdata.com is at <a href="https://github.com/pang-apps/" target="_blank">Pang Data applications</a>.


# Need some help?
Please send us your issues and problems using our feedback in Pangdata.com.

#Contribute Code
If you would like to become an active contributor to this project please contact us using feedback in Pangdata.com.
You can become a developer of Pang-apps and contribute your great applciations.
