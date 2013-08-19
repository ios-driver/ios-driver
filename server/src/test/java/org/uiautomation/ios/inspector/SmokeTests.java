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

package org.uiautomation.ios.inspector;


public class SmokeTests {

  private static final int driver_port = 4444;

  public static void main(String[] args) throws Exception {
    IDEServer ide = new IDEServer(driver_port);
    MockedCache cache = new MockedCache();
    ide.setCache(cache);
    ide.start();
    System.out.println("server started. To view some mocked inspector, go to  \n workspace/ios-driver/server/src/test/resources/index.html\n");
  }

}
