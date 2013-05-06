package org.uiautomation.ios.server.utils;

import org.junit.Assert;
import org.testng.annotations.Test;

public class IOSVersionTest {

  @Test
  public void test() {
    String v1 = "4.0";
    String v2 = "4.1";
    Assert.assertTrue(new IOSVersion(v2).isGreaterThan(v1));

    v1 = "3.9.1";
    v2 = "4.1";
    Assert.assertTrue(new IOSVersion(v2).isGreaterThan(v1));

    v1 = "4.1.1";
    v2 = "4.1.0";
    Assert.assertTrue(new IOSVersion(v1).isGreaterThan(v2));

    v1 = "6.1.3";
    v2 = "6.1";
    Assert.assertTrue(new IOSVersion(v1).isGreaterThan(v2));

    v1 = "6.1.3";
    v2 = "6.0";
    Assert.assertTrue(new IOSVersion(v1).isGreaterOrEqualTo(v2));

    v1 = "6.0.0";
    v2 = "6.0";
    Assert.assertTrue(new IOSVersion(v1).isGreaterOrEqualTo(v2));
  }
}
