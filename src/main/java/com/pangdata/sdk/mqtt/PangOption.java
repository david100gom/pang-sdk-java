package com.pangdata.sdk.mqtt;

public class PangOption {

  private String  addresss;
  private boolean anonymous;

  public PangOption(String address, boolean anonymous) {
    this.addresss = address;
    this.anonymous = anonymous;
  }

  public String getAddresss() {
    return addresss;
  }

  public void setAddresss(String addresss) {
    this.addresss = addresss;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }
  
}
