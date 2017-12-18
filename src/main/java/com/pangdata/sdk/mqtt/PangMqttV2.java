package com.pangdata.sdk.mqtt;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.connector.BrokerReassignFailoverConnector;
import com.pangdata.sdk.mqtt.connector.BrokerReassignFailoverConnectorV2;
import com.pangdata.sdk.util.PangProperties;

public class PangMqttV2 extends MqttDelegatedAbstractHttpClientV2 {
  private static final Logger logger = LoggerFactory.getLogger(PangMqttV2.class);
  
  class DefaultReassignableBrokerProvider implements ReassignableBrokerProvider {

    public PangOption getAddress() throws Exception {
      return getNewAddress();
    }
  }

  private CountDownLatch cd;

  public PangMqttV2() throws Exception {
    super(true);
    prepare();
  }

  public PangMqttV2(String username, String userkey, String uri,
      DataSharingCallback dataSharingCallback) throws Exception {
    super(username, userkey, uri, dataSharingCallback);
    prepare();
  }
  
  public PangMqttV2(String username, String userkey) throws Exception {
    this(username, userkey, null);
  }

  public PangMqttV2(String username, String userkey, String uri) throws Exception {
    this(username, userkey, uri, null);
  }

  private void waitUntilConnected() {
    try {
      if (cd != null) {
        cd.await(3, TimeUnit.SECONDS);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private PangOption getNewAddress() throws Exception {
    try {
      Map<String, Object> responseMap =  request("pa/user/profile"+"/" + userkey + "/" + username);
      Map data = (Map) responseMap.get("Data");
      
      String brokers = (String) data.get("MDS");
      if (brokers == null || brokers.length() == 0) {
        throw new IllegalStateException("No available MDS");
      }
      String anonymous = (String) data.get("ALLOW_ANONYMOUS");
      return new PangOption(brokers, Boolean.valueOf(anonymous));
    } catch (Exception e) {
      logger.error("Address lookup error", e);
      throw e;
    }
  }

  @Override
  public void connect(String uri) throws Exception {
    super.connect(uri);
    // String id = username + "-" + SdkUtils.getMacAddress() + "-" +
    // System.currentTimeMillis();
    String id = username + "-" + UUID.randomUUID();
    PangOption newAddress = getNewAddress();

    String passwd = null;
    if (!newAddress.isAnonymous()) {
      passwd = userkey;
    }

    Properties properties = PangProperties.getProperties();
    if (properties != null) {
      String preferAddress = (String) properties.get("pang.preferAddress");
      if (preferAddress != null) {
        newAddress.setAddresss(preferAddress);
      }
    }

    createConnector(new BrokerReassignFailoverConnectorV2(newAddress.getAddresss(), username, passwd,
        id, new DefaultReassignableBrokerProvider()));
    logger.info("Connecting Pangdata scalable message server...");
    pang.connect(newAddress.getAddresss());
  }

  private void setWaitor() {
    cd = new CountDownLatch(1);
    setConnectionCallback(new ConnectionCallback() {

      public void onConnectionSuccess() {
        cd.countDown();
        logger.info("Pangdata scalable message server connected.");
      }

      public void onConnectionLost(Throwable cause) {
        logger.info("Pangdata scalable message server disconnected.");
      }

      public void onConnectionFailure(Throwable cause) {
        cd.countDown();
        logger.info("Pangdata scalable message server connecting failure.");
      }
    });
  }

  private void startStatusUpdater() {
    Thread t = new Thread() {

      public void run() {
        while (true) {
          try {
            request("pa/user/validate" + "/" + userkey + "/" + username);
            PangMqttV2.this.pang.setValidLicense(true); 
          } catch (Exception e) {
            PangMqttV2.this.pang.setValidLicense(false);
            logger.error("Validate error", e);
          }
          try {
            TimeUnit.HOURS.sleep(12);
          } catch (InterruptedException e1) {}
        }
      };
    };

    t.setName("status-updater");
    t.setDaemon(true);
    t.start();
  }

  private void prepare() throws Exception {
    if (!PangProperties.isEnabledToSend()) {
      setProxyClient();
      return;
    }
    setWaitor();
    connect(url);
    waitUntilConnected();
    startStatusUpdater();
  }

  public boolean isValidLicense() {
    return false;
  }

  public void setValidLicense(boolean sendable) {}
}
