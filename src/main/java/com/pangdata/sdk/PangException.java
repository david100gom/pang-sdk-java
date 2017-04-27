package com.pangdata.sdk;


public class PangException extends RuntimeException {

  private static final long serialVersionUID = 1185911520195086924L;

  public PangException(Throwable e) {
    super(e);
  }

  public PangException(String message) {
    super(new Throwable(message));
  }

}
