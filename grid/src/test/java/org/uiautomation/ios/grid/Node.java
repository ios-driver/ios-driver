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

package org.uiautomation.ios.grid;

import org.uiautomation.ios.IOSServer;

public class Node {


  public static void main(String[] args) throws Exception {
    String[] a = new String[]{
        "-beta",
        "-hub", "http://localhost:4444/grid/register",
        "-proxy", "org.uiautomation.ios.grid.IOSRemoteProxy",
        "-host", "localhost",
        "-port", "5555"};
    IOSServer.main(a);
  }
}
