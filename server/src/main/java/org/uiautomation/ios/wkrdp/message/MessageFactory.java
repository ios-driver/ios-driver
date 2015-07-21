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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Created with IntelliJ IDEA. User: freynaud Date: 17/01/2013 Time: 15:08 To change this template use File | Settings |
 * File Templates.
 */
public class MessageFactory {

  private static final Logger LOG = Logger.getLogger(MessageFactory.class.getName());

  private static final List<String> DEFAULT = Arrays.asList("Default");

  private static final List<String> IOS7_TYPES = Arrays.asList(
    "7.0", "7.0.1", "7.0.2", "7.0.3", "7.0.4", "7.0.5", "7.0.6", 
    "7.1", "7.1.1", "7.1.2");

  private static final List<String> IOS8_TYPES = Arrays.asList(
    "8.0", "8.0.1", "8.0.2", 
    "8.1", "8.1.1", "8.1.2", "8.1.3", 
    "8.2", "8.3", "8.4", "8.4.1");

  private final Map<List<String>, Map<String, Class<? extends BaseIOSWebKitMessage>>> iOSTypesMap;

  private String iOSVersion;

  private ReentrantLock versionDeterminingLock;

  private Condition determiningVersion;

  public MessageFactory() {
    iOSTypesMap = new HashMap<>();
    versionDeterminingLock = new ReentrantLock();
    determiningVersion = versionDeterminingLock.newCondition();
    populateDefaultTypes();
    populateIOS7Types();
    populateIOS8Types();
  }

  private void populateDefaultTypes() {
    Map<String, Class<? extends BaseIOSWebKitMessage>> types = new HashMap<>();
    types.put("_rpc_reportSetup:", org.uiautomation.ios.wkrdp.message.ReportSetupMessageImpl.class);
    types.put("_rpc_applicationSentData:", org.uiautomation.ios.wkrdp.message.ApplicationDataMessageImpl.class);
    iOSTypesMap.put(DEFAULT, types);
  }

  private void populateIOS7Types() {
    Map<String, Class<? extends BaseIOSWebKitMessage>> types = new HashMap<>();
    types.put("_rpc_reportConnectedApplicationList:",
        org.uiautomation.ios.wkrdp.message.ios7.ReportConnectedApplicationsMessageImpl.class);
    types.put("_rpc_applicationSentListing:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationSentListingMessageImpl.class);
    types.put("_rpc_applicationConnected:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationConnectedMessageImpl.class);
    types.put("_rpc_applicationDisconnected:",
        org.uiautomation.ios.wkrdp.message.ios7.ApplicationDisconnectedMessageImpl.class);
    iOSTypesMap.put(IOS7_TYPES, types);
  }

  private void populateIOS8Types() {
    Map<String, Class<? extends BaseIOSWebKitMessage>> types = new HashMap<>();
    types.put("_rpc_reportConnectedApplicationList:",
        org.uiautomation.ios.wkrdp.message.ios8.ReportConnectedApplicationsMessageImpl.class);
    types.put("_rpc_applicationSentListing:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationSentListingMessageImpl.class);
    types.put("_rpc_applicationConnected:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationConnectedMessageImpl.class);
    types.put("_rpc_applicationDisconnected:",
        org.uiautomation.ios.wkrdp.message.ios8.ApplicationDisconnectedMessageImpl.class);
    iOSTypesMap.put(IOS8_TYPES, types);
  }

  public IOSMessage create(String rawMessage) {
    try {
      if (LOG.isLoggable(Level.FINE)) {
        LOG.log(Level.FINE, "Raw message:   " + rawMessage);
      }
      BaseIOSWebKitMessage m = new BaseIOSWebKitMessage(rawMessage);
      waitOnVersionDetermination(m);
      Class<? extends BaseIOSWebKitMessage> implementationClass = getImplementationClassFor(m);
      if (implementationClass == null) {
        throw new RuntimeException("NI " + m.getSelector());
      }
      IOSMessage iOSMessage = createObjectOf(implementationClass, rawMessage);
      return iOSMessage;
    } catch (Exception e1) {
      LOG.log(Level.SEVERE, "format error", e1);
    }
    return null;
  }

  private void waitOnVersionDetermination(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    try {
      versionDeterminingLock.lock();
      if (isWaitingRequiredFor(baseIOSWebKitMessage)) {
        try {
          determiningVersion.await(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
      }
    } finally {
      versionDeterminingLock.unlock();
    }
  }

  private boolean isWaitingRequiredFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    return !ReportSetupMessage.SELECTOR.equals(baseIOSWebKitMessage.selector) && iOSVersion == null;
  }
  private Class<? extends BaseIOSWebKitMessage> getImplementationClassFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    if (iOSVersion == null) {
      determineIOSVersion(baseIOSWebKitMessage);
    }
    if (isImplementationClassDistinctFor(baseIOSWebKitMessage)) {
      return searchSpecificListFor(baseIOSWebKitMessage);
    } else {
      return searchDefaultFor(baseIOSWebKitMessage);
    }
  }

  private boolean isImplementationClassDistinctFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    if (ReportSetupMessage.SELECTOR.equals(baseIOSWebKitMessage.selector)
        || ApplicationDataMessage.SELECTOR.equals(baseIOSWebKitMessage.selector)) {
      return false;
    }
    return true;
  }

  private Class<? extends BaseIOSWebKitMessage> searchDefaultFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    return iOSTypesMap.get(DEFAULT).get(baseIOSWebKitMessage.selector);
  }

  private Class<? extends BaseIOSWebKitMessage> searchSpecificListFor(BaseIOSWebKitMessage baseIOSWebKitMessage) {
    List<String> iOSTypeList = getIOSTypeList();
    return iOSTypesMap.get(iOSTypeList).get(baseIOSWebKitMessage.selector);
  }

  private List<String> getIOSTypeList() {
    if (IOS8_TYPES.contains(iOSVersion)) {
      return IOS8_TYPES;
    } else {
      return IOS7_TYPES;
    }
  }

  private void determineIOSVersion(BaseIOSWebKitMessage baseIOSWebkitMesssage) {
    try {
      versionDeterminingLock.lock();
      iOSVersion = baseIOSWebkitMesssage.arguments.objectForKey(WebkitDevice.WIRSIMULATORPRODUCTVERSIONKEY).toString();
      determiningVersion.signal();
      if (LOG.isLoggable(Level.FINE)) {
        LOG.log(Level.FINE, "IOS version determined = " + iOSVersion);
      }
    } finally {
      versionDeterminingLock.unlock();
    }
  }

  private IOSMessage createObjectOf(Class<? extends BaseIOSWebKitMessage> implementationClass, String argument)
      throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    Object[] args = new Object[] { argument };
    Class<?>[] argsClass = new Class[] { String.class };
    Constructor<?> c = implementationClass.getConstructor(argsClass);
    IOSMessage iOSMessage = (IOSMessage) c.newInstance(args);
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine("IOS Message: " + iOSMessage);
    }
    return iOSMessage;
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
