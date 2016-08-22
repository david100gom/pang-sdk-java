package com.pangdata.sdk.util;

import java.io.IOException;

import org.junit.Test;

public class PangPropertiesTests {

	@Test
	public void isEnabledToSend() throws IOException {
		SdkUtils.loadPangProperties();
		boolean enabledToSend = PangProperties.isEnabledToSend();
		System.out.println(enabledToSend);
	}
}
