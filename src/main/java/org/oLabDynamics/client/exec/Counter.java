/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oLabDynamics.client.exec;

import java.io.Serializable;

public class Counter implements Serializable{
	
	public enum CounterType{
		DATE,
		INSTANT_VALUE,
		PROGRESS_VALUE,
		OTHER
	}
	
	private String name;
	private String displayName;
	private long value = 0;
	private CounterType counterType;
    
	protected Counter() {
		
	}

	protected Counter(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
    
	public String getName(){
		return name;
	}

	public String getDisplayName(){
		return displayName;
	}
    
	public long getValue(){
		return value;
	}

	public CounterType getCounterType() {
		return counterType;
	}
	
	
    
}
