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

package org.uiautomation.ios.utils;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;


public class RealTimeTestMonitor implements IInvokedMethodListener2 {


  private final boolean monitor;

  public RealTimeTestMonitor() {
    String flag = System.getProperty("realTimeMonitor");
    if (flag != null) {
      monitor = true;
    } else {
      monitor = false;
    }
    String help = "You can enable using the realTimeMonitor system property flag."
                  + " -DrealTimeMonitor in your mvn build.";
    System.out.println("BUILD STUFF : real time monitor is " + (monitor ? "ON" : "OFF." + help) + ".");
  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult,
                               ITestContext context) {
    if (monitor) {
      System.out.println("starting :" + toString(method));
    }

  }

  private String toString(IInvokedMethod method) {
    try {
      return method.getTestMethod().getConstructorOrMethod().getMethod().toString();
    } catch (Exception e) {
      return method.toString();
    }
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult res, ITestContext context) {
    if (monitor) {
      String status = "UNKNOWN";
      switch (res.getStatus()) {
        case 1:
          status = "PASSED";
        case 2:
          status = "FAILED";
        case 3:
          status = "SKIPPED";
      }
      long total = res.getEndMillis() - res.getStartMillis();
      if (res.isSuccess()) {
        status = "PASSED ";
      } else {
        status = "FAILED";
      }
      System.out.println(status + ": " + toString(method) + " in " + total + " ms");
    }

  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

  }
}
