package com.pangdata.client.device;

import java.util.concurrent.TimeUnit;

import com.pangdata.sdk.Pangdata;
import com.pangdata.sdk.callback.DataCallback;
import com.pangdata.sdk.mqtt.MqttFailoverHttpClient;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Read an Analog to Digital Converter
 */
public class RaspberryPiMain {
	private final static boolean DISPLAY_DIGIT = false;
	private final static boolean DEBUG = false;
	// Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
	// 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
	// 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
	// SPI: Serial Peripheral Interface
	private static Pin spiClk = RaspiPin.GPIO_01; // Pin #18, clock
	private static Pin spiMiso = RaspiPin.GPIO_04; // Pin #23, data in. MISO:
													// Master In Slave Out
	private static Pin spiMosi = RaspiPin.GPIO_05; // Pin #24, data out. MOSI:
													// Master Out Slave In
	private static Pin spiCs = RaspiPin.GPIO_06; // Pin #25, Chip Select

	private static int ADC_CHANNEL = 0; // Between 0 and 7, 8 channels on the
										// MCP3008

	private static GpioPinDigitalInput misoInput = null;
	private static GpioPinDigitalOutput mosiOutput = null;
	private static GpioPinDigitalOutput clockOutput = null;
	private static GpioPinDigitalOutput chipSelectOutput = null;

	private static boolean go = true;

	private static Pangdata client;

	public static void main(String[] args) throws Exception {
		// 1. set userId, userKey, dataKey
		String userId = "demo";
		String userKey = "Fj8QBK";
		String dataKey = "temperature";
		String serverUri = "http://211.205.94.150:9191";
		client = new MqttFailoverHttpClient(userId, userKey, serverUri);
		client.connect(serverUri);
		
		final GpioController gpio = GpioFactory.getInstance();
		mosiOutput = gpio.provisionDigitalOutputPin(spiMosi, "MOSI",
				PinState.LOW);
		clockOutput = gpio.provisionDigitalOutputPin(spiClk, "CLK",
				PinState.LOW);
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs, "CS",
				PinState.LOW);

		misoInput = gpio.provisionDigitalInputPin(spiMiso, "MISO");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
		        System.out.println("Bye...");
		        client.stopTimerTask();
		        client.disconnect();
		        gpio.shutdown();
				System.out.println("Shutting down.");
			}
		});
		
		client.startTimerTask(dataKey, new DataCallback() {
			
			public boolean isRunning(int count) {
				return true;
			}
			
			public String getData() {
				int adc = readAdc();
				Double temp_c = (((adc * (3300.0 / 1024.0)) - 100.0) / 10.0) - 40.0;
				temp_c = Math.round(temp_c*10d)/10d;
				System.out.println("temp " + temp_c);
				return temp_c.toString();
			}

            public void onSuccess(Object sent) {
            }
		}, 10, TimeUnit.SECONDS);
		client.waitTimerTask();
	}

	private static int readAdc() {
		chipSelectOutput.high();

		clockOutput.low();
		chipSelectOutput.low();

		int adccommand = ADC_CHANNEL;
		adccommand |= 0x18; // 0x18: 00011000
		adccommand <<= 3;
		// Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
		for (int i = 0; i < 5; i++) //
		{
			if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
				mosiOutput.high();
			else
				mosiOutput.low();
			adccommand <<= 1;
			clockOutput.high();
			clockOutput.low();
		}

		int adcOut = 0;
		for (int i = 0; i < 12; i++) // Read in one empty bit, one null bit and
										// 10 ADC bits
		{
			clockOutput.high();
			clockOutput.low();
			adcOut <<= 1;

			if (misoInput.isHigh()) {
				// System.out.println("    " + misoInput.getName() +
				// " is high (i:" + i + ")");
				// Shift one bit on the adcOut
				adcOut |= 0x1;
			}
			if (DISPLAY_DIGIT)
				System.out.println("ADCOUT: 0x"
						+ Integer.toString(adcOut, 16).toUpperCase() + ", 0&"
						+ Integer.toString(adcOut, 2).toUpperCase());
		}
		chipSelectOutput.high();

		adcOut >>= 1; // Drop first bit
		return adcOut;
	}

	private static String lpad(String str, String with, int len) {
		String s = str;
		while (s.length() < len)
			s = with + s;
		return s;
	}
}