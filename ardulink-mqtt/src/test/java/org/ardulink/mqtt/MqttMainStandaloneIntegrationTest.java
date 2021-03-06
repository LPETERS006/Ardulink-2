/**
Copyright 2013 project Ardulink http://www.ardulink.org/
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.ardulink.mqtt;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;

import org.ardulink.core.Link;
import org.ardulink.mqtt.util.AnotherMqttClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class MqttMainStandaloneIntegrationTest {

	private static final String topic = "myTestTopic";

	private final Link link = mock(Link.class);

	@Rule
	public Timeout timeout = new Timeout(5, SECONDS);

	@Test
	public void clientCanConnectToNewlyStartedBroker() throws Exception {
		MqttMain mqttMain = new MqttMain() {
			@Override
			protected Link createLink() {
				return link;
			}
		};
		mqttMain.setStandalone(true);
		mqttMain.setBrokerTopic(topic);

		try {
			mqttMain.connectToMqttBroker();
			AnotherMqttClient.builder().topic(topic).connect();
		} finally {
			mqttMain.close();
		}

	}

}
