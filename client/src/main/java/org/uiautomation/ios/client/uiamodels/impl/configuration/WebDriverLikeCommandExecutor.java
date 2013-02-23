package org.uiautomation.ios.client.uiamodels.impl.configuration;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSObject;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIAElement;
import org.uiautomation.ios.communication.Helper;
import org.uiautomation.ios.communication.HttpClientFactory;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebDriverLikeCommandExecutor {

  private final URL remoteURL;
  private ErrorHandler errorHandler = new ErrorHandler();
  private final RemoteWebDriver driver;

  public WebDriverLikeCommandExecutor(RemoteWebDriver driver) {
    URL remoteServer;
    if (driver.getCommandExecutor() instanceof HttpCommandExecutor) {
      HttpCommandExecutor exe = (HttpCommandExecutor) driver.getCommandExecutor();
      remoteServer = exe.getAddressOfRemoteServer();
    } else {
      throw new WebDriverException("ios driver only supports http communication.");
    }
    this.remoteURL = remoteServer;
    this.driver = driver;
  }


  /**
   * send the request to the remote server for execution.
   */
  public <T> T execute(WebDriverLikeRequest request) {
    Response response = null;
    long total = 0;
    try {
      HttpClient client = HttpClientFactory.getClient();
      String url = remoteURL + request.getPath();
      BasicHttpEntityEnclosingRequest
          r =
          new BasicHttpEntityEnclosingRequest(request.getMethod(), url);
      if (request.hasPayload()) {
        r.setEntity(new StringEntity(request.getPayload().toString(), "UTF-8"));
      }

      HttpHost h = new HttpHost(remoteURL.getHost(), remoteURL.getPort());
      long start = System.currentTimeMillis();
      HttpResponse res = client.execute(h, r);
      total = System.currentTimeMillis() - start;

      response = Helper.exctractResponse(res);
    } catch (Exception e) {
      throw new WebDriverException(e);
    }
    response = errorHandler.throwIfResponseFailed(response, total);
    return cast(response.getValue());
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, RemoteUIAElement element,
                                           Map<String, ?> params,
                                           Map<String, String> extraParamInPath) {
    String method = command.method();
    Path p = new Path(command).withSession(driver.getSessionId());
    if (element != null) {
      p.withReference(element.getReference());
    }
    for (String key : extraParamInPath.keySet()) {
      p.validateAndReplace(":" + key, extraParamInPath.get(key));
    }
    WebDriverLikeRequest request = new WebDriverLikeRequest(method, p, params);
    return request;
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, RemoteUIAElement element,
                                           Map<String, ?> params) {
    return buildRequest(command, element, params, new HashMap<String, String>());
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command) {
    return buildRequest(command, null, null);
  }

  public WebDriverLikeRequest buildRequest(WebDriverLikeCommand command, Map<String, ?> params) {
    return buildRequest(command, null, params);
  }

  private <T> T cast(Object o) {
    if (o == null) {
      return null;
    }
    if (o instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) o;
      if (map.containsKey("ELEMENT")) {
        return (T) RemoteIOSObject.createObject(driver, map);
      } else if (map.containsKey("tree")) {
        String serialized = new BeanToJsonConverter().convert(o);
        try {
          return (T) new JSONObject(serialized);
        } catch (JSONException e) {
          throw new WebDriverException("cannot cast");
        }
      } else {
        return (T) map;
      }
    }
    if (o instanceof Collection) {
      List<Object> res = new ArrayList<Object>();
      Collection c = (Collection<Object>) o;
      for (Object ob : c) {
        res.add(cast(ob));
      }
      return (T) res;
    }
    return (T) o;
  }
}
