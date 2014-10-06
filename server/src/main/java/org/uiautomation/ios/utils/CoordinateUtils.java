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

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.wkrdp.model.RemoteWebElement;

import java.util.Map;

public class CoordinateUtils {

  public static Point forcePointOnScreen(Point point, Dimension screenSize) {
    int x;
    int y;

    if (point.getX() < 0) {
      x = 0;
    } else if (point.getX() > screenSize.getWidth()) {
      x = screenSize.getWidth();
    } else {
      x = point.getX();
    }

    if (point.getY() < 0) {
      y = 0;
    } else if (point.getY() > screenSize.getHeight()) {
      y = screenSize.getHeight();
    } else {
      y = point.getY();
    }

    return new Point(x, y);
  }

  public static Point getScreenCenter(Dimension screenSize) {
    return new Point(screenSize.getWidth() / 2, screenSize.getHeight() / 2);
  }

  public static Point getCenterPoint(Point topLeft, Dimension size) {
    return getCenterPoint(topLeft.getX(), topLeft.getY(), size.getWidth(), size.getHeight());
  }

  private static Point getCenterPoint(int xCoord, int yCoord, int width, int height) {
    int centerX = xCoord + (width / 2);
    int centerY = yCoord + (height / 2);

    return new Point(centerX, centerY);
  }

  public static Point getCenterPointFromResponse(Response response) {
    Map<String, Object> o = (Map<String, Object>) response.getValue();

    Map<String, Object> origin = (Map<String, Object>) o.get("origin");
    int x = Integer.parseInt(origin.get("x").toString());
    int y = Integer.parseInt(origin.get("y").toString());

    Map<String, Object> size = (Map<String, Object>) o.get("size");
    int width = Integer.parseInt(size.get("width").toString());
    int height = Integer.parseInt(size.get("height").toString());

    return getCenterPoint(x, y, width, height);
  }

  public static Point getCenterPointFromElement(RemoteWebElement element) throws Exception {
    Point location = element.getLocation(RemoteWebElement.ElementPosition.TOP_LEFT);
    Dimension size = element.getSize();
    return getCenterPoint(location.getX(), location.getY(), size.getWidth(), size.getHeight());
  }
}
