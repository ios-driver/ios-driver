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
package org.uiautomation.ios.communication;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class HttpClientFactory {

  public static DefaultHttpClient getClient() {
    DefaultHttpClient client = new DefaultHttpClient();
    client.setRedirectStrategy(new MyRedirectHandler());
    return client;
  }

  static class MyRedirectHandler implements RedirectStrategy {

    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
        throws ProtocolException {
      return false;
    }

    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
        throws ProtocolException {
      return null;
    }
  }
}
