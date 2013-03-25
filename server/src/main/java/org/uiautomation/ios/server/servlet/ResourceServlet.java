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

package org.uiautomation.ios.server.servlet;

import org.apache.commons.io.IOUtils;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;


@SuppressWarnings("serial")
public class ResourceServlet extends DriverBasedServlet {


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    process(request, response);
  }

  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String resource = request.getPathInfo().replace(request.getServletPath(), "");
    if (resource.startsWith("/")) {
      resource = resource.replaceFirst("/", "");
    }
    File f = getDriver().getCache().getResourceForKey(resource);

    try {
      //if (validImage(f)) {
        FileInputStream fis = new FileInputStream(f);
        IOUtils.copy(fis, response.getOutputStream());
        fis.close();
      //}

    } finally {
      response.flushBuffer();
    }
  }

  private boolean validImage(File f) throws IOException {
    InputStream is = new FileInputStream(f);
    try {
       //is = new FileInputStream(f);

      final ImageInputStream imageInputStream = ImageIO
          .createImageInputStream(is);
      final Iterator<ImageReader> imageReaders = ImageIO
          .getImageReaders(imageInputStream);
      if (!imageReaders.hasNext()) {
        // not an image
        return false;
      }
      final ImageReader imageReader = imageReaders.next();
      imageReader.setInput(imageInputStream);
      final BufferedImage image = imageReader.read(0);
      if (image == null) {
        // empty
        return false;
      }
      image.flush();
      return true;
    } catch (final IndexOutOfBoundsException e) {
      // truncated
      return false;
    } catch (final IIOException e) {
      if (e.getCause() instanceof EOFException) {
        // truncated
        return false;
      }
    } catch (Exception e) {
      return false;
    } finally {
      is.close();
    }
    return true;
  }


}
