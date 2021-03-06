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

package org.ardulink.gui;

import java.util.Arrays;
import java.util.List;

import org.ardulink.core.linkmanager.LinkConfig;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class DummyLinkConfig implements LinkConfig {

	@Named("a")
	private int a = 42;

	@Named("b")
	private String b;

	@Named("c")
	private Boolean c = Boolean.TRUE;

	@Named("d")
	private String d;

	public int getA() {
		return a;
	}

	public String getB() {
		return b;
	}

	@ChoiceFor("b")
	public List<String> someValuesForB() {
		return Arrays.asList("foo", "bar");
	}

	public Boolean getC() {
		return c;
	}

	public void setC(Boolean c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setA(int a) {
		this.a = a;
	}

	public void setB(String b) {
		this.b = b;
	}

	public void setD(String d) {
		this.d = d;
	}

}
