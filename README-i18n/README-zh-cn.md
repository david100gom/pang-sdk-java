# 使用JAVA语言的 庞数据 SDK 
提供连接云服务的java客户端程序。
# 使用 SDK 
## 第一阶段 : 需要安装
需要Java 1.5或以上的版本.如果通过SDK开发那需要,gradle 或maven项目构建。
## 第二阶段 : 获取 SDK。
#### 使用 Maven
如添加Pang java SDK项目那么需 **pom.xml** 中添加代码如下。:
```
...
<repositories>
...
<repository>
<id>pang-data-repo</id>
<name>pang-data-repo</name>
<url>http://mini.prever.io:8081/nexus/content/groups/prever-io-public-repository/</url>
</repository>
</repositories>
...
<dependencies>
...
<dependency>
<groupId>com.pangdata</groupId>
<artifactId>pang-sdk-java</artifactId>
<version>1.0.0-RELEASE</version>
</dependency>
</dependencies>
...
}
```
#### 使用 Gradle
如添加Pang java SDK项目那么需 **build.gradle**中添加代码如下。:
```
...
apply plugin: 'maven'
...
repositories {
...
maven{
url "http://mini.prever.io:8081/nexus/content/groups/prever-io-public-repository/"
}
...
}
dependencies {
...
compile group: 'com.pangdata', name: 'pang-sdk-java', version: '1.0.0-RELEASE'
...
}
```
#### 使用 Git
 如果禁用软件包,则可以直接通过Git来获取SDK源代码。请通过下面的命令接收源代码。:
```bash
git clone https://github.com/pangdata/pang-sdk-java.git
cd ./pang-sdk-java/
```
### 下载jar文件与源代码。
如果你想下载这个SDK的源代码，去下面的链接和下载压缩文件。
<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">下载最新版本</a>
## 第三阶段 : 使用示例
#### 示例1 : 使用pangmqtt API发送随机数。
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/PangMqttExample.java" target="_blank">源代码链接</a>
此示例不用pang.properties文件。可以直接使用它没有任何配置。
```java
Pang pang = new PangMqtt("username", "userkey");

Random r = new Random(); 
pang.sendData("example_temperature", r.nextInt(200));
```
#### 示例2 : 使用 Pang's task timer发送随机数。
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/PangTaskTimerExample.java" target="_blank">源代码链接</a>
```java
package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.callback.MultipleDataCallback;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.util.PangProperties;

public class PangTaskTimerExample {
  private static final Logger logger = LoggerFactory.getLogger(PangTaskTimerExample.class);

  private static Random random = new Random();
  private static final String[] status = new String[] {"GOOD", "BAD", "NONE"};

  public static void main(String[] args) throws Exception {
    final Pang pang = new PangMqtt();

    long period = PangProperties.getPeriod(); // Milli seconds
    pang.startTimerTask(new MultipleDataCallback() {

      public void onSuccess(Object sent) {
        logger.info("Sent: {}", sent);
      }

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
#### 示例3 : 使用JDK Timer类发送随机数。
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/JavaUtilTimerExample.java" target="_blank">link to source</a>
```java
package examples;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pangdata.sdk.Pang;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.util.PangProperties;
package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.util.PangProperties;

public class JavaUtilTimerExample {
  private static final Logger logger = LoggerFactory.getLogger(JavaUtilTimerExample.class);
  
  private static Random random = new Random();
  private static final String[] status = new String[]{"GOOD", "BAD", "NONE"};

  public static void main(String[] args) throws Exception {
    final Pang pang = new PangMqtt();

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
        
        boolean result = pang.sendData(data);
        logger.info("Message delivery sucess: {}", result);
      }
    }, 0, period);

  }
}
```
## 第四阶段 : pangdata.com 帐户注册。
运行实例，你需要pangdata.com账户和用户密钥。
请通过下面的链接注册pangdata.com。
<a href="http://pangdata.com/pa/signUp" target="_blank">去注册</a>
## 第五阶段 : 配置与运行。
运行以上例子。在上面提供了可执行的应用程序。
通过以下链接可下载 **pang-sdk-java.zip/pang-sdk-java.tar**文件 。
<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">下载链接</a>
解压这个文件， 配置**pang.properties** 后运行。. 
#### 配置**pang.properties** 文件
庞数据 SDK 需要在**pang.properties**类路径.  为通过Pangdata.com认证。需配置 **用户名** 和 **用户密钥* 。
###### 如何找到您的用户密钥。
1. 注册 pangdata.com
2. 转到设置 > 配置文件
3. 复制用户密钥粘贴到 'pang.userkey'
```
#Pang Data reserved properties
pang.username=[[your user name in pangdata.com]]
pang.userkey=[[your user key in pangdata.com]]

# Search schedule period(seconds)
pang.period = 10
```
您可以在该文件中声明您自己的属性。
pangproperties API提供了获取的方法。下面的代码是获取属性。
```java
final String devicename = (String) PangProperties.getProperty("your property key");
```
#### 运行实例
在以下示例目录中运行下列命令。
###### windows
```
pang.bat
```
###### linux
```bash
./pang.sh
```
应用程序运行成功，就会显示以下日志。
```log
10:54:48.991 [main] TimerDataSender is started.
10:54:49.205 [pool-1-thread-1] Send data to server http://pangdata.com/api/data/put/XbuDm0/example
10:54:49.678 [pool-1-thread-1] Response: {"Data":"{\"randomInteger\":71,\"randomBoolean\":false,\"randomString\":\"GOOD\",\"randomFloat\":62.508314100247794}","Message":"Ok!","Success":true}
```
## 第六阶段 : 浏览数据。
最后，通过pangdata.com中的仪表板，浏览实时进入的数据。
#### 注册 pangdata.com
<a href="http://pangdata.com/pa/login" target="_blank">注册</a>
#### 设备登记
登录后，您可以看到概述。概述显示您的帐户的整体状态。在**未注册的设备中点击** **总数**。
跳转到新的设备列表。你可以看到检测到的数据（randominteger，randomfloat，randomstring，randombooean）。
庞数据视自动检测数据，既发送的庞数据客户端（例）。
列表**"randomInteger"**右侧 单击+ 按钮。跳转到**新设备**窗口。
你想添加一个插件？画对号，点击右下角的确认， "randomInteger"就会注册到庞数据的设备列表。

#### 创建小部件
立即添加一个小部件*窗口打开。
在标题输入中输入**"Line chart"**。要创建部件，必须选择仪表板。点击**select dashboard**选择你想添加的小部件。
首次登录不会有仪表盘。要创建一个新的仪表板，输入**"Example Dashboard"**在**select dashboard** 点击右侧  +  按钮。
最后点击“确定”。您可以看到仪表板命名为*"Example Dashboard"**和部件名为"Line chart" 。
#### 浏览实时更新图
让我们看看这个小部件。此控件中的线条图被更改为10秒的间隔。（如果 **pang.properties**的 **pang.period**设置为10）
可以相同的步骤，登记小部件既设备数据（randomfloat，randomstring，randombooean）。
pangdata 除 line chart以外还提供各种小部件。可尝试一下其他类型小部件。
![dashboard](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/getting_start_result.gif)
## 屏幕截图
![dashboard_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/example_dashboard.gif)
![device_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/device_rowdata.JPG)
![analytics_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/analytics.JPG)
## 下一个阶段 : 你需要另一个庞数据应用程序吗？
我们提供一个集合样品，它会告诉你如何开发你的物联网设备和任何应用程序，你想使用吗？ 那就点击以下链接安装吧，安装后请在pangdata.com 确认。 <a href="https://github.com/pang-apps/" target="_blank">Pang Data applications</a>.

# 需要帮忙吗？
在反馈中把你的问题陈述给我们吧！pangdata.com。
#贡献代码
如果你想加入这个项目，那请使用反馈联系我们pangdata.com积极提出您的宝贵的意见与建议。
你作Pang-apps开发者真诚的感谢你提供的宝贵的建议。
