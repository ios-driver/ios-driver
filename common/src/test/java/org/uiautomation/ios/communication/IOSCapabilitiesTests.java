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

package org.uiautomation.ios.communication;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

public class IOSCapabilitiesTests {

  String ipadFromInstruments = "{" + "\"language\":\"de\"," + "\"locale\":\"fr_CA\","
                               + "\"version\":null," + "\"bundleid\":\"com.yourcompany.UICatalog\""
                               + ",\"model\":\"iPad Simulator\"" + ",\"device\":\"iPad Simulator\""
                               + ",\"systemName\":\"iPhone OS\"" + ",\"sdkVersion\":\"5.1\""
                               + ",\"rect\":{\"origin\":{\"x\":0,\"y\":0},\"size\":{\"width\":768,\"height\":1024}}}";


  String iphoneFromInstruments = "{" + "\"language\":\"de\"," + "\"locale\":\"fr_CA\","
                                 + "\"version\":null,"
                                 + "\"bundleid\":\"com.yourcompany.UICatalog\","
                                 + "\"model\":\"iPhone Simulator\","
                                 + "\"device\":\"iPhone Simulator\",\"systemName\":\"iPhone OS\","
                                 + "\"sdkVersion\":\"5.1\","
                                 + "\"rect\":{\"origin\":{\"x\":0,\"y\":0},\"size\":{\"width\":320,\"height\":480}}}";

  @Test(groups = {"capabilitiesMappingIsWrong", "broken"})
  public void propertiesAreAlignedWithUIAutomation() throws Exception {
    IOSCapabilities ipad = new IOSCapabilities(new JSONObject(ipadFromInstruments));
    Assert.assertEquals(ipad.getDevice(), DeviceType.ipad);
    Assert.assertEquals(ipad.getLanguage(), "de");
    Assert.assertEquals(ipad.getLocale(), "fr_CA");
    Assert.assertEquals(ipad.getSDKVersion(), "5.1");

    IOSCapabilities iphone = new IOSCapabilities(new JSONObject(iphoneFromInstruments));
    Assert.assertEquals(iphone.getDevice(), DeviceType.iphone);
    Assert.assertEquals(iphone.getLanguage(), "de");
    Assert.assertEquals(iphone.getLocale(), "fr_CA");
    Assert.assertEquals(iphone.getSDKVersion(), "5.1");

  }
}
