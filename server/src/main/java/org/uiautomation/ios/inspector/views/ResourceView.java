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
package org.uiautomation.ios.inspector.views;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriverException;

public class ResourceView implements View {

  private final InputStream is;
  private final String mime;

  public ResourceView(InputStream is, String mime) {
    this.is = is;
    this.mime = mime;
  }

  public void render(HttpServletResponse response) {
    try {
      response.setContentType(mime);
      IOUtils.copy(is, response.getOutputStream());
      IOUtils.closeQuietly(is);
    } catch (IOException e) {
      throw new WebDriverException("Bug.", e);
    }

  }

}
