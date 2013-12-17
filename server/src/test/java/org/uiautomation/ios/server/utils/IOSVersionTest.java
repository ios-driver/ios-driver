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


import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.utils.IOSVersion;

public class IOSVersionTest {

  @Test
  public void canParseBaseVersion() {
    IOSVersion v5 = new IOSVersion("5.0");
    Assert.assertEquals(v5.getMajor(), "5");
    Assert.assertEquals(v5.getMinor(), "0");

  }

  @Test
  public void canParseIncrementamVersion() {
    IOSVersion v7 = new IOSVersion("7.0.3");
    Assert.assertEquals(v7.getMajor(), "7");
    Assert.assertEquals(v7.getMinor(), "0");
    Assert.assertEquals(v7.getIncremental(), "3");

  }
}
