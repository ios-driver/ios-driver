package org.uiautomation.ios.client.uiamodels.impl;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class AttachRemoteUIADriver extends RemoteUIADriver {

  @Override
  protected void startClient() {
  }

  @Override
  protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
  }

  public AttachRemoteUIADriver(URL url, SessionId session) {
    super(url, null);
    setCommandExecutor(new HttpCommandExecutor(url));
    setSessionId(session.toString());
  }

  IOSCapabilities caps = null;

  @Override
  public IOSCapabilities getCapabilities() {
    if (caps == null) {
      caps = loadCapabilities();
    }
    return caps;
  }

  private IOSCapabilities loadCapabilities() {
    WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.GET_SESSION);
    Map<String, Object> c = execute(request);
    return new IOSCapabilities(c);
  }

}
