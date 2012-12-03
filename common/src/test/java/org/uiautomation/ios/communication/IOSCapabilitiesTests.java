package org.uiautomation.ios.communication;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.Device;

public class IOSCapabilitiesTests {

  String ipadFromInstruments = "{" + "\"language\":\"de\"," + "\"locale\":\"fr_CA\","
      + "\"version\":null," + "\"bundleid\":\"com.yourcompany.UICatalog\""
      + ",\"model\":\"iPad Simulator\"" + ",\"device\":\"iPad Simulator\""
      + ",\"systemName\":\"iPhone OS\"" + ",\"sdkVersion\":\"5.1\""
      + ",\"rect\":{\"origin\":{\"x\":0,\"y\":0},\"size\":{\"width\":768,\"height\":1024}}}";


  String iphoneFromInstruments = "{" + "\"language\":\"de\"," + "\"locale\":\"fr_CA\","
      + "\"version\":null," + "\"bundleid\":\"com.yourcompany.UICatalog\","
      + "\"model\":\"iPhone Simulator\","
      + "\"device\":\"iPhone Simulator\",\"systemName\":\"iPhone OS\","
      + "\"sdkVersion\":\"5.1\","
      + "\"rect\":{\"origin\":{\"x\":0,\"y\":0},\"size\":{\"width\":320,\"height\":480}}}";

  @Test(groups = {"capabilitiesMappingIsWrong","broken"})
  public void propertiesAreAlignedWithUIAutomation() throws Exception {
    IOSCapabilities ipad = new IOSCapabilities(new JSONObject(ipadFromInstruments));
    Assert.assertEquals(ipad.getDevice(), Device.ipad);
    Assert.assertEquals(ipad.getLanguage(), "de");
    Assert.assertEquals(ipad.getLocale(), "fr_CA");
    Assert.assertEquals(ipad.getSDKVersion(), "5.1");



    IOSCapabilities iphone = new IOSCapabilities(new JSONObject(iphoneFromInstruments));
    Assert.assertEquals(iphone.getDevice(), Device.iphone);
    Assert.assertEquals(iphone.getLanguage(), "de");
    Assert.assertEquals(iphone.getLocale(), "fr_CA");
    Assert.assertEquals(iphone.getSDKVersion(), "5.1");

  }
}
