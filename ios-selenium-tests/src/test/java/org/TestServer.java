/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org;

import org.openqa.selenium.Pages;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.environment.webserver.WebbitAppServer;

public class TestServer {

  public static void main(String[] args) {
    AppServer appServer = new WebbitAppServer();
    appServer.start();
    Pages pages = new Pages(appServer);
    System.out.println(pages.sleepingPage);
    String slowPage = appServer.whereIs("sleep?time=5");
    System.out.println("slowPage" + slowPage);
    System.out.println(pages.iframePage);
  }

}
