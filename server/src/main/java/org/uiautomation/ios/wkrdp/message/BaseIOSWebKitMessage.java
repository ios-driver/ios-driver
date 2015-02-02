/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.wkrdp.message;

import com.dd.plist.NSDictionary;
import com.dd.plist.XMLPropertyListParser;


public class BaseIOSWebKitMessage implements IOSMessage {

  protected final String rawMessage;
  protected final NSDictionary rootDict;
  protected final String selector;
  protected final NSDictionary arguments;

  public BaseIOSWebKitMessage(String rawMessage) throws Exception {
    this.rawMessage = rawMessage;

    byte[] bytes = rawMessage.getBytes();
    rootDict = (NSDictionary) XMLPropertyListParser.parse(bytes);
    selector = rootDict.objectForKey("__selector").toString();
    arguments = (NSDictionary) rootDict.objectForKey("__argument");

  }

  @Override
  public String getSelector() {
    return selector;
  }

  public NSDictionary getArguments() {
    return arguments;
  }
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "\n\tArguments:\n\t" + toString(arguments);
  }

  protected String toString(NSDictionary args) {
    if (args == null) {
      return null;
    }
    StringBuilder b = new StringBuilder();
    for (String key : args.allKeys()) {
      b.append(key + "=");
      b.append(args.objectForKey(key));
    }
    return b.toString();
  }
}
