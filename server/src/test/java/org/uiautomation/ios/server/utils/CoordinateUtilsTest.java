/*
 * Copyright 2012 ios-driver committers.
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

import java.util.Map;

import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.Response;
import org.testng.annotations.Test;

public class CoordinateUtilsTest {

  @Test
  public void getCenterPointFromResponseReturnsCenterOfRectangle() {
    Map<String, Object> origin = Maps.newHashMap();
    origin.put("x", "100");
    origin.put("y", "200");

    Map<String, Object> size = Maps.newHashMap();
    size.put("width", "10");
    size.put("height", "20");

    Map<String, Object> obj = Maps.newHashMap();
    obj.put("origin", origin);
    obj.put("size", size);

    Response response = new Response();
    response.setValue(obj);

    Point expectedPoint = new Point(105, 210);
    Point actualPoint = CoordinateUtils.getCenterPointFromResponse(response);

    Assert.assertEquals("Point should be center of response rectangle", expectedPoint, actualPoint);
  }
}
