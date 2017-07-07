package com.pangdata.sdk.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

public class SdkUtils {

  public static String getDevicename(String devicename) {
    return getDevicename(devicename, null);
  }

  public static String getDevicename(String devicename, String...args) {
    if(args != null) {
      devicename = replaceTwoToOne(replaceSpecialCharacter(replace(devicename, args)));
    }
    String prefix = PangProperties.getPrefix();
    if (prefix != null && !prefix.isEmpty()) {
      devicename = prefix+PangProperties.getConcatenator()+devicename;
    }
    return devicename;
  }
  
  public static String replace(String value, String...args) {
    for(String arg:args) {
      value = value.replace("{}", arg);
    }
    return value;
  }
  
  
  public static String replaceSpaceCharacter(String devicename) {
    return devicename.replaceAll("[\\s+]", "");
  }

  public static String replaceTwoToOne(String devicename) {
    return devicename.replaceAll(PangProperties.getConcatenator()+"{2,}", PangProperties.getConcatenator());
  }
  public static String replaceSpecialCharacter(String devicename) {
    return devicename.replaceAll("[^a-zA-Z0-9]", PangProperties.getConcatenator());
  }
  
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
  
  public static DefaultHttpClient createHttpClient(String url) throws Exception {
    return createHttpClient(url, null);
  }
  
  public static DefaultHttpClient createHttpClient(String url, HttpParams myParams) throws Exception {
    DefaultHttpClient httpClient = null;
    if(url.toLowerCase().startsWith("https")) {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
          public boolean isTrusted(X509Certificate[] certificate, String authType) {
            return true;
          }
        };
        SSLSocketFactory sf =
            new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", 443, sf));
        ClientConnectionManager ccm = new SingleClientConnManager(registry);
        httpClient = new DefaultHttpClient(ccm, myParams);
      } else {
          httpClient = new DefaultHttpClient(myParams);
      }
    
    return httpClient;
  }
}
