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


import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;

import sun.print.resources.serviceui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SafariTest {


  public static void main(String[] args) throws MalformedURLException, InterruptedException {
    List<DesiredCapabilities> caps = new ArrayList<>();
    caps.add(DesiredCapabilities.firefox());
    caps.add(IOSCapabilities.iphone("Safari"));
    caps.add(DesiredCapabilities.firefox());
    caps.add(IOSCapabilities.iphone("Safari"));
    caps.add(DesiredCapabilities.firefox());
    caps.add(IOSCapabilities.iphone("Safari"));
    caps.add(DesiredCapabilities.firefox());
    caps.add(IOSCapabilities.iphone("Safari"));
    caps.add(DesiredCapabilities.firefox());
    caps.add(IOSCapabilities.iphone("Safari"));

    for (final DesiredCapabilities cap : caps) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            openeBay(cap);
          } catch (MalformedURLException e) {
            System.err.println("ERROR " + cap.asMap() + " - " + e.getMessage());
//            e.printStackTrace();
          }
        }
      }).start();

    }
    Thread.sleep(120000);
  }


  private static void openeBay(DesiredCapabilities cap) throws MalformedURLException {
    RemoteWebDriver d = null;
    try {
      System.out.println("starting " + cap);
      d = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
      System.out.println("started " + cap);
      d.get("http://ebay.co.uk");
      System.out.println(d.getCurrentUrl());
    } catch (Exception e){
      System.err.println("ERROR "+cap+" - "+e.getMessage());
    } finally {
      if (d != null) {
        d.quit();
      }
    }
  }
}
