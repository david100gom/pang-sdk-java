package com.pangdata.sdk.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdkUtils {

  private static final Logger logger = LoggerFactory.getLogger(SdkUtils.class);

  public static InetAddress getLocalAddress() throws SocketException
  {
    Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while( ifaces.hasMoreElements() )
    {
      NetworkInterface iface = ifaces.nextElement();
      Enumeration<InetAddress> addresses = iface.getInetAddresses();

      while( addresses.hasMoreElements() )
      {
        InetAddress addr = addresses.nextElement();
        if( addr instanceof Inet4Address && !addr.isLoopbackAddress() )
        {
          return addr;
        }
      }
    }

    return null;
  }
  
  public static String getMacAddress() throws Exception{
    NetworkInterface network = NetworkInterface.getByInetAddress(getLocalAddress());
        
    byte[] mac = network.getHardwareAddress();
        
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < mac.length; i++) {
        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
    }
    
    return sb.toString();
  }
  
  public static synchronized Properties loadPangProperties() throws IOException {
    Properties properties = PangProperties.getProperties();
    if(properties != null) {
      return properties;
    }
    Properties props = new Properties();
    logger .info("Loading pang.properties in your classpath");
    InputStream is = SdkUtils.class.getResourceAsStream("/pang.properties");
    if(is == null) {
      throw new IOException();
    }
    props.load(is);
    is.close();
    
    PangProperties.setProperties(props);
    return props;
  }
}
