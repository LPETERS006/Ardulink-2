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

package com.github.pfichtner.ardulink.core;

import static org.zu.ardulink.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pfichtner.ardulink.core.proto.api.Protocol;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class StreamConnection implements Connection {

	private static final Logger logger = LoggerFactory
			.getLogger(StreamConnection.class);

	private final StreamReader streamReader;
	private final OutputStream outputStream;

	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

	public StreamConnection(InputStream inputStream, OutputStream outputStream,
			Protocol protocol) {
		this.outputStream = outputStream;
		this.streamReader = new StreamReader(inputStream) {
			@Override
			protected void received(byte[] bytes) throws Exception {
				for (Listener listener : StreamConnection.this.listeners) {
					try {
						listener.received(bytes);
					} catch (Exception e) {
						logger.error("Listener {} failure", listener, e);
					}
				}
			}
		};
		if (inputStream != null) {
			String delimiter = new String(protocol.getSeparator());
			streamReader.runReaderThread(delimiter);
		}
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		logger.debug("Stream write {}", bytes);
		synchronized (outputStream) {
			outputStream.write(checkNotNull(bytes, "bytes must not be null"));
			outputStream.flush();
		}
		for (Listener listener : this.listeners) {
			try {
				listener.sent(bytes);
			} catch (Exception e) {
				logger.error("Listener {} failure", listener, e);
			}
		}
	}

	@Override
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void close() throws IOException {
		this.streamReader.close();
	}

}