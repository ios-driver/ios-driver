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
