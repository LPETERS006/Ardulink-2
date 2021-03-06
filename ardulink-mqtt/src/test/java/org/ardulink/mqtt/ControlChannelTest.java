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

import static org.ardulink.core.proto.impl.ALProtoBuilder.alpProtocolMessage;
import static org.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.START_LISTENING_ANALOG;
import static org.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.START_LISTENING_DIGITAL;
import static org.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.STOP_LISTENING_ANALOG;
import static org.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.STOP_LISTENING_DIGITAL;
import static org.ardulink.mqtt.util.MqttMessageBuilder.mqttMessageWithBasicTopic;
import static org.ardulink.util.Throwables.propagate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.ardulink.core.Connection;
import org.ardulink.core.ConnectionBasedLink;
import org.ardulink.core.StreamConnection;
import org.ardulink.core.proto.impl.ArdulinkProtocol2;
import org.ardulink.mqtt.util.Message;
import org.ardulink.mqtt.util.MqttMessageBuilder;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class ControlChannelTest {

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	private final Connection connection = new StreamConnection(null,
			outputStream, ArdulinkProtocol2.instance());

	private final ConnectionBasedLink link = new ConnectionBasedLink(
			connection, ArdulinkProtocol2.instance());

	private final AbstractMqttAdapter mqttClient = new AbstractMqttAdapter(
			link, Config.DEFAULT.withControlChannelEnabled()) {
		@Override
		void fromArduino(String topic, String message) {
			throw new UnsupportedOperationException("Receiving not supported");
		}
	};

	private final MqttMessageBuilder mqttMessage = mqttMessageWithBasicTopic(Config.DEFAULT_TOPIC);

	@After
	public void tearDown() throws IOException {
		link.close();
	}

	@Test
	public void canEnableListenerForDigitalPin() throws IOException {
		int pin = 2;
		simulateMqttToArduino(mqttMessage.digitalListener(pin).enable());
		assertThat(serialReceived(),
				is(alpProtocolMessage(START_LISTENING_DIGITAL).forPin(pin)
						.withoutValue() + "\n"));
	}

	@Test
	public void noMessageWhenConfigDoesNotSupportControlChannel()
			throws IOException {
		int pin = 2;
		Message message = mqttMessage.digitalListener(pin).enable();
		new AbstractMqttAdapter(link, Config.DEFAULT) {
			@Override
			void fromArduino(String topic, String message) {
				throw new UnsupportedOperationException(
						"Receiving not supported");
			}
		}.toArduino(message.getTopic(), message.getMessage());
		assertThat(serialReceived(), is(empty()));
	}

	@Test
	public void canEnableListenerForAnalogPin() throws IOException {
		int pin = 3;
		simulateMqttToArduino(mqttMessage.analogListener(pin).enable());
		assertThat(serialReceived(),
				is(alpProtocolMessage(START_LISTENING_ANALOG).forPin(pin)
						.withoutValue() + "\n"));
	}

	@Test
	public void canDisableListenerForDigitalPin() throws IOException {
		int pin = 4;
		simulateMqttToArduino(mqttMessage.digitalListener(pin).disable());
		assertThat(serialReceived(),
				is(alpProtocolMessage(STOP_LISTENING_DIGITAL).forPin(pin)
						.withoutValue() + "\n"));
	}

	@Test
	public void canDisableListenerForAnalogPin() throws IOException {
		int pin = 5;
		simulateMqttToArduino(mqttMessage.analogListener(pin).disable());
		assertThat(serialReceived(),
				is(alpProtocolMessage(STOP_LISTENING_ANALOG).forPin(pin)
						.withoutValue() + "\n"));
	}

	@Test
	public void canHandleInvaldTypeOnEnabling() throws IOException {
		int pin = 6;
		simulateMqttToArduino(mqttMessage.listener().appendTopic("X" + pin)
				.enable());
		assertThat(serialReceived(), is(empty()));
	}

	@Test
	public void canHandleInvaldTypeOnDisabling() throws IOException {
		int pin = 6;
		simulateMqttToArduino(mqttMessage.listener().appendTopic("X" + pin)
				.disable());
		assertThat(serialReceived(), is(empty()));
	}

	@Test
	public void canHandleInvaldDigitalPins() throws IOException {
		String pin = "X";
		simulateMqttToArduino(mqttMessage.listener().appendTopic("D" + pin)
				.enable());
		assertThat(serialReceived(), is(empty()));
	}

	@Test
	public void canHandleInvaldAnalogPins() throws IOException {
		String pin = "X";
		simulateMqttToArduino(mqttMessage.listener().appendTopic("A" + pin)
				.enable());
		assertThat(serialReceived(), is(empty()));
	}

	private void simulateMqttToArduino(Message message) throws IOException {
		mqttClient.toArduino(message.getTopic(), message.getMessage());
	}

	private String serialReceived() {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw propagate(e);
		}
		return new String(outputStream.toByteArray());
	}

	private static String empty() {
		return "";
	}
}
