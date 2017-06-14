package com.pangdata.sdk.util;

public enum SizeUnit {
 
  KB { 
    public double to(long value) {
      if(value == 0) {
        return 0;
      } else {
        return Math.round((value/1024.0) * 100.0) / 100.0;
      }
    }
  },
  MB {
    @Override
    public double to(long value) {
      if(value == 0) {
        return 0;
      } else {
        return Math.round((value/1048576.0) * 100.0) / 100.0;
      }
    }
  }, GB {
    @Override
    public double to(long value) {
      if(value == 0) {
        return 0;
      } else {
        return Math.round((value/1073741824.0) * 100.0) / 100.0;
      }
    }
  };

  public abstract double to(long value);
}
