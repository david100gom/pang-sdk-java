# Pang Data SDK for Java

이 프로젝트는 Pang Data 웹 클라우드 서비스를 쉽게 연결할수 있는 자바 클라이언트 라이브러리를 제공합니다. 
<!--For documentation please see the <a href="https://pangdata.com/home#/sdk" target="_blank">SDK</a>. For a list of libraries and how they are organized, please see the <a href="https://github.com/pangdata/pang-sdk-java/wiki/Pang-Data-SDK-for-Java-Features" target="_blank">Pang Data SDK for Java Features Wiki page</a>.-->

<!--# Getting Start SDK-->
# SDK 시작하기

<!--## Step 0 : Check prerequisites-->
## 0 단계 : 개발에 필요한 요건
JAVA **1.5** 버전 이상이 필요합니다. SDK로 개발을 하고 싶다면, gradle 이나 maven 빌드 툴도 필요합니다.

<!--## Step 1 : Get SDK-->
## 1 단계 : SDK 얻기
#### Maven을 이용
Pang java SDK를 당신의 프로젝트에 추가하고자 한다면 다음과 같이 **pom.xml** 에 추가합니다.
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
#### Gradle을 이용
Pang java SDK를 당신의 프로젝트에 추가하고자 한다면 다음과 같이 **build.gradle** 에 추가합니다.
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
#### git 을 이용
패키지 관리를 사용하지 않는 경우에는 git을 통해 직접 소스를 받는 것이 가능합니다. 
아래와 같이 git 명령을 통해 소스를 받으세요.
```bash
git clone https://github.com/pangdata/pang-sdk-java.git
cd ./pang-sdk-java/
```
<!--### Download jar & code-->
### jar파일과 소스코드 다운로드
SDK 소스 코드를 직접 다운로드하고 싶다면 아래 링크로 이동하시고 zip/tar 파일을 다운로드 받으세요.

<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">최신 버전으로 다운로드</a>
## 2 단계 : 예제 작성
#### 예제 1 : PangMqtt API 이용한 렌덤한 데이터 전송 예제
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/PangMqttExample.java" target="_blank">소스코드로 링크</a>
#### 예제 2 : Pang task timer를 이용한 렌덤한 데이터 전송 예제
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/PangTaskTimerExample.java" target="_blank">소스코드로 링크</a>
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

#### 예제 3 : JDK task timer를 이용한 렌덤한 데이터 전송 예제
<a href="https://github.com/pangdata/pang-sdk-java/blob/master/examples/examples/JavaUtilTimerExample.java" target="_blank">소스코드로 링크</a>
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

## 3 단계 : pangdata.com 가입하기
예제를 실행하기 위해서는 pangdata.com의 계정과 유저키가 필요합니다. 
아래 링크를 통해 pangdata.com 에 가입하세요.

<a href="http://pangdata.com/pa/signUp" target="_blank">가입하러 가기</a>

<!--## Step 4 : Configuration & Run-->
## 4 단계 : 환경 설정과 실행
위의 예제를 실행시켜 봅시다. 우리는 위의 예제를 실행가능한 형태로 제공하고 있습니다.
아래 링크를 통해 **pang-sdk-java.zip/pang-sdk-java.tar** 를 다운로드 받습니다.

<a href="https://github.com/pangdata/pang-sdk-java/releases/latest" target="_blank">다운로드 링크</a>

이 파일의 압축을 풀고, **pang.properties**을 세팅하고 실행시켜 봅니다.
<!--#### Config **pang.properties** file-->
#### **pang.properties** 설정
Pang Data SDK 는 **pang.properties**가 java 클래스패스 경로에 위치해야 합니다. 이 파일은 Pangdata.com의 인증을 위해 **username**과 **user key**를 가지고 있습니다.
<!--###### How to find your user key.-->
###### user key 찾는 법
1. pangdata.com 에 로그인
2. Settings > Profile 으로 이동
3. 'pang.userkey' 를 복사 & 붙여넣기 한다.
```
#Pang Data reserved properties
pang.username=[[your user name in pangdata.com]]
pang.userkey=[[your user key in pangdata.com]]

# Search schedule period(seconds)
pang.period = 10
```
당신은 자신이 고유한 property를 이 파일에 설정할 수 있습니다.
PangProperties API에 당신의 property를 읽어올 수 있는 getter 메소드가 있습니다. 아래 코드를 참고하세요.
```java
final String devicename = (String) PangProperties.getProperty("your property key");
```
<!--#### Running example-->
#### 예제 실행
해당 예제의 디렉토리에서 아래 명령어로 예제를 실행시킵니다.
###### windows
```
pang.bat
```
###### linux
```bash
./pang.sh
```
성공적으로 실행된다면 아래와 같은 로그가 출력됩니다.
```log
10:54:48.991 [main] TimerDataSender is started.
10:54:49.205 [pool-1-thread-1] Send data to server http://pangdata.com/api/data/put/XbuDm0/example
10:54:49.678 [pool-1-thread-1] Response: {"Data":"{\"randomInteger\":71,\"randomBoolean\":false,\"randomString\":\"GOOD\",\"randomFloat\":62.508314100247794}","Message":"Ok!","Success":true}
```

<!--## Step 5 : See your data-->
## 5 단계 : 데이터 보기

마지막으로 이 예제 데이터를 실시간으로 팡 데이터 대쉬보드로 볼 시간입니다.
<!--#### Log in pangdata.com-->
#### pangdata.com 에 로그인
<a href="http://pangdata.com/pa/login" target="_blank">로그인 이동</a>

<!--#### Register Device-->
#### Device 등록

로그인 후에 **overview** 화면을 볼 수 있습니다. **overview** 화면은 당신 계정의 전체 상태를 보여 줍니다. 그 중에 **미등록 디바이스** 안의 **전체** 숫자를 클릭합니다.
바뀐 화면은 **새로운 디바이스 리스트** 입니다. 여기서 감지된 데이터(randomInteger,randomFloat, randomString, randomBooean)를 볼 수 있습니다. 팡 데이터는 클라이언트(예제) 에서 보내준 데이터를 자동으로 감지하여 **새로운 디바이스 리스트**에 추가합니다.

리스트에 있는 **"randomInteger"**의 오른쪽 **+ 버튼** 을 클릭합니다. **New device** 창이 열립니다.
**위젯을 추카하시겠습니까?** 를 체크 표시하고 우측 하단의 **OK 버튼** 을 클릭합니다.
**"randomInteger"** 가 팡 데이터의 디바이스로 등록됩니다.

<!--#### Create Widget-->
#### Widget 등록
이후에 바로 **위젯 추가** 창이 열립니다.
Immediately the **Add a Widget** window opens. **title**항목에 **"Line chart"**라고 입력합니다. 위젯을 만들기 위해서는 대쉬보드를 선택해야 합니다. **select dashboard** 클릭하여 위젯을 추가하기 원하는 대쉬보드를 선택합니다. 처음 사용하는 사용자는 대쉬보드가 없습니다. 먼저 대쉬보드르 만들어야 하는데 **select dashboard**항목에 **"Example Dashboard"** 라고 입력한 다음 오른쪽 **+ 버튼** 을 클릭하면 새로운 데쉬보드가 만들어지면서 동시에 선택됩니다.
마지막으로 **OK 버튼**을 클릭합니다. 바로 이름이 **"Example Dashboard"** 인 대쉬보드와 이름이 **"Line chart"** 인 위젯을 볼 수 있습니다.

<!--#### See real-time update chart-->
#### 실시간 업데이트 되는 차트 보기
방금 전의 위젯을 봅시다. 이 위젯은 10초 간격으로 line chart가 업데이트 됩니다(**pang.properties** 의 **pang.period** 값을 10으로 설정 했을 시). 당신은 나머지 데이터들(randomFloat, randomString, randomBooean)도 동일한 절차로 디바이스와 위젯을 만들 수 있습니다. 팡 데이터는 line chart외에도 다양한 유형의 위젯을 제공합니다. 다른 유형의 위젯도 만들어 보세요.
![dashboard](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/getting_start_result.gif)

<!--## Screen Shots-->
## 스크린 샷
![dashboard_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/example_dashboard.gif)
![device_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/device_rowdata.JPG)
![analytics_screenshot](https://raw.githubusercontent.com/pangdata/pang-sdk-java/master/screenshots/analytics.JPG)

<!--## Next Step : Do you need another Pang Data applications?-->
## 다음 단계 : 다른 Pang Data 어플리케이션이 필요하십니까?
우리는 당신의 IoT디바이스나 어플리케이션에서 어떻게 개발해야 하는지 예제가 되는 어플리케이션 모음을 제공합니다. 아래 링크를 방문하고 어플리케이션을 설치 후 pangdata.com 에서 확인해보세요.

<a href="https://github.com/pang-apps/" target="_blank">Pang Data 어플리케이션</a>.

<!--# Need some help?-->
# 도움이 필요하신가요?
당신의 문의사항이나 문제점이 있을 경우에 Pangdata.com 의 피드백으로 보내주세요.

<!--#Contribute Code-->
#코드 기여
만일 이 프로젝트의 엑티브 컨트리뷰터가 되고 싶다면 Pangdata.com 의 피드백으로 보내주세요. Pang-apps의 개발자로서 당신의 멋진 어플리케이션을 만드는 데 기여 할 수 있습니다.
