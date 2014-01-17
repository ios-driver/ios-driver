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

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA. User: freynaud Date: 17/01/2013 Time: 15:08 To change this template
 * use File | Settings | File Templates.
 */
public class MessageFactory {

  private static final Logger log = Logger.getLogger(MessageFactory.class.getName());
  private final
  Map<String, Class<? extends BaseIOSWebKitMessage>>
      types =
      new HashMap<String, Class<? extends BaseIOSWebKitMessage>>();

  public MessageFactory() {
    types.put("_rpc_reportSetup:", ReportSetupMessage.class);
    types.put("_rpc_reportConnectedApplicationList:", ReportConnectedApplicationsMessage.class);
    types.put("_rpc_applicationSentListing:", ApplicationSentListingMessage.class);
    types.put("_rpc_applicationSentData:", ApplicationDataMessage.class);
    types.put("_rpc_applicationConnected:", ApplicationConnectedMessage.class);
    types.put("_rpc_applicationDisconnected:", ApplicationDisconnectedMessage.class);
  }

  public IOSMessage create(String rawMessage) {
    try {
      BaseIOSWebKitMessage m = new BaseIOSWebKitMessage(rawMessage);
      Class<? extends BaseIOSWebKitMessage> impl = types.get(m.getSelector());

      if (impl == null) {
        throw new RuntimeException("NI " + m.getSelector());
      }
      Object[] args = new Object[]{rawMessage};
      Class<?>[] argsClass = new Class[]{String.class};

      Constructor<?> c = impl.getConstructor(argsClass);
      IOSMessage message = (IOSMessage) c.newInstance(args);
      log.fine("Message: " + message);
      return message;
    } catch (Exception e1) {
      e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return null;
  }

  private Document getDocument(String rawMessage) throws DocumentException {
    String message = rawMessage.replace(
        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
        "");
    SAXReader reader = new SAXReader();
    Document document = reader.read(IOUtils.toInputStream(message));
    return document;
  }

  private Node getWebKitDebugMessage(Document d) {
    Node n = d.selectSingleNode("/plist/dict/dict/data");
    return n;
  }
}
