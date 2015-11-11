# prever-device-sdk-java
Prever.io SDK supports for HTTP and MQTT protocol

public class PreverSDKExample {
  private static final Logger logger = LoggerFactory.getLogger(PreverSDKExample.class);

  public static void main(String[] args) throws Exception {
    final Prever prever = new PreverHttp();

    prever.startTimerTask(new MultipleDataCallback() {
      public void onSuccess(Object sent) {}

      public boolean isRunning(int sentCount) {
        return true;
      }

      public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        
        // Your code here.
        data.put("your device name", your value);
        data.put("your device name2", your value);

        return data;
      }
      
    }, PreverProperties.getPeriod(), TimeUnit.MILLISECONDS);

  }

}
