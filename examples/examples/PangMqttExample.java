/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Preversoft
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.pangdata.sdk.Pang;
import com.pangdata.sdk.mqtt.PangMqtt;
import com.pangdata.sdk.mqtt.PangMqttV1;

public class PangMqttExample {
  public static void main(String[] args) throws Exception {
    Pang pang = new PangMqttV1("username", "userkey");

	Random r = new Random();
	pang.sendData("example_temperature", r.nextInt(40));
	
	// You can use a map to send multiple data.
	// sendUsingMap();
  }
  
  private static void sendUsingMap() throws Exception {
    Pang pang = new PangMqttV1("username", "userkey");
    
    Map<String, Object> data = new HashMap<String, Object>();

    Random r = new Random();
    data.put("example_temperature", r.nextInt(40));
    
    pang.sendData(data);
  }
}