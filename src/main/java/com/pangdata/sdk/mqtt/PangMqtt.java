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
package com.pangdata.sdk.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pangdata.sdk.callback.ConnectionCallback;
import com.pangdata.sdk.callback.DataSharingCallback;
import com.pangdata.sdk.mqtt.connector.BrokerReassignFailoverConnector;
import com.pangdata.sdk.util.JsonUtils;
import com.pangdata.sdk.util.PangProperties;
import com.pangdata.sdk.util.SdkUtils;

public class PangMqtt extends MqttDelegatedAbstractHttpClient {
	private static final Logger logger = LoggerFactory
			.getLogger(PangMqtt.class);

	class DefaultReassignableBrokerProvider implements
			ReassignableBrokerProvider {

		public PangOption getAddress() throws Exception {
			return getNewAddress();
		}
	}

	private CountDownLatch cd;

	public PangMqtt() throws Exception {
		super(true);
		setWaitor();
		connect(url);
		waitUntilConnected();
	}

	public PangMqtt(String username, String userkey) throws Exception {
		this(username, userkey, null);
		setWaitor();
		connect(url);
		waitUntilConnected();
	}

	private void waitUntilConnected() {
		try {
			if(cd != null) {
				cd.await(3, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PangMqtt(String username, String userkey, String uri)
			throws Exception {
		this(username, userkey, uri, null);
	}

	public PangMqtt(String username, String userkey, String uri,
			DataSharingCallback dataSharingCallback) throws Exception {
		super(username, userkey, uri, dataSharingCallback);
	}

	private PangOption getNewAddress() throws Exception {
		HttpPost httpPost = null;

		HttpResponse response = null;
		try {
			if (httpClient == null) {
				httpClient = SdkUtils.createHttpClient(url);
			}

			// FIXIT? http://mini.prever.io:3000/issues/2342
			// TODO upgrade version to handle timeout.
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					100 * 1000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(),
					100 * 1000);

			httpPost = new HttpPost(url + "/pa/user/profile/" + userkey + "/"
					+ username);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("content-type", "application/json"));

			logger.info("Starting to get user profile.......");
			logger.info("URI: {}", httpPost.getURI().toString());
			response = httpClient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.error("HTTP error: {}",
						EntityUtils.toString(response.getEntity(), "UTF-8"));
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			String profile = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			logger.info("{} 's response profile: {}", username, profile);

			Map<String, Object> responseMap = (Map<String, Object>) JsonUtils
					.toObject(profile, Map.class);
			if (!(Boolean) responseMap.get("Success")) {
				throw new RuntimeException(String.format(
						"Success: %s, Error message: %s",
						responseMap.get("Success"), responseMap.get("Message")));
			}
			Map data = (Map) responseMap.get("Data");
			String brokers = (String) data.get("MDS");
			if (brokers == null || brokers.length() == 0) {
				throw new IllegalStateException("No available MDS");
			}
			String anonymous = (String) data.get("ALLOW_ANONYMOUS");
			return new PangOption(brokers, Boolean.valueOf(anonymous));
		} catch (Exception e) {
			logger.error("User profile request error", e);
			throw e;
		}
	}

	@Override
	public void connect(String uri) throws Exception {
		super.connect(uri);
		// String id = username + "-" + SdkUtils.getMacAddress() + "-" +
		// System.currentTimeMillis();
		String id = username + "-" + UUID.randomUUID();
		PangOption newAddress = getNewAddress();

		String passwd = null;
		if (!newAddress.isAnonymous()) {
			passwd = userkey;
		}

		Properties properties = PangProperties.getProperties();
		if (properties != null) {
			String preferAddress = (String) properties
					.get("pang.preferAddress");
			if (preferAddress != null) {
				newAddress.setAddresss(preferAddress);
			}
		}

		createConnector(new BrokerReassignFailoverConnector(
				newAddress.getAddresss(), username, passwd, id,
				new DefaultReassignableBrokerProvider()));
		logger.info("Connecting Pangdata scalable message server...");
		pang.connect(newAddress.getAddresss());
	}

	private void setWaitor() {
		cd = new CountDownLatch(1);
		setConnectionCallback(new ConnectionCallback() {

			public void onConnectionSuccess() {
				cd.countDown();
				logger.info("Pangdata scalable message server connected.");
			}

			public void onConnectionLost(Throwable cause) {
				logger.info("Pangdata scalable message server disconnected.");
			}

			public void onConnectionFailure(Throwable cause) {
				cd.countDown();
				logger.info("Pangdata scalable message server connecting failure.");
			}
		});
	}
}
