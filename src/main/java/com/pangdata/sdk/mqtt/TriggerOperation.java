package com.pangdata.sdk.mqtt;


public enum TriggerOperation {
  Changed("changed"),
  Equal("equal"),
  Less("less"),
  LessEqual("lessequal"),
  Greater("greater"),
  GreaterEqual("greaterequal"),
  Between("between"),
  MuchChanged("muchchanged"),
  In("in"),
  Out("out"),
  Period("period");

  
  private String operation;

  TriggerOperation(String operation) {
    this.operation = operation;
  }
  
  public String getTopic() {
    return operation;
  }
  
}
