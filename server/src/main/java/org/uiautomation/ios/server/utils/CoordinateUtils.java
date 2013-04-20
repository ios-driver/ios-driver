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

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

import java.util.Map;

public class CoordinateUtils {
  private static Point getCenterPoint(int xCoord, int yCoord, int width, int height) {
    int centerX = xCoord + (width / 2);
    int centerY = yCoord - (height / 2);

    Point center = new Point(centerX, centerY);
    return center;
  }

  public static Point getCenterPointFromResponse(Response response) {
    Map<String, Object> o = (Map<String, Object>) response.getValue();

    Map<String, Object> origin = (Map<String, Object>) o.get("origin");
    int x = Integer.parseInt(origin.get("x").toString());
    int y = Integer.parseInt(origin.get("y").toString());

    Map<String, Object> size = (Map<String, Object>) o.get("size");
    int height = Integer.parseInt(size.get("height").toString());
    int width = Integer.parseInt(size.get("width").toString());

    return getCenterPoint(x, y, height, width);
  }

  public static Point getCenterPointFromElement(RemoteWebElement element) throws Exception {
    Point location = element.getLocation();
    Dimension size = element.getSize();
    return getCenterPoint(location.getX(), location.getY(), size.getWidth(), size.getHeight());
  }
}
