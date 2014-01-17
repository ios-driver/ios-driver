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
import org.testng.annotations.Listeners;



public class RealTimeTestMonitor implements IInvokedMethodListener2 {


  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult,
                               ITestContext context) {
    System.out.println("starting :" + method.toString());

  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult res, ITestContext context) {
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
    System.out.println(status + ": " + method.toString() + " in " + total + " ms");

  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

  }
}
