package org.uiautomation.ios.client.uiamodels.impl;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.UIAModels.UIACollectionView;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

public class RemoteUIACollectionView extends RemoteUIAElement implements UIACollectionView {

    public RemoteUIACollectionView(RemoteWebDriver driver, String reference) {
        super(driver, reference);
    }

    public void scrollUp() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_UP);
        commandExecutor.execute(request);

    }

    @Override
    public void scrollDown() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_DOWN);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollLeft() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_LEFT);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollRight() {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_RIGHT);
        commandExecutor.execute(request);
    }

    @Override
    public void scrollToElementWithName(String name) {
        WebDriverLikeRequest request = buildRequest(WebDriverLikeCommand.SCROLL_TO_ELEMENT_WITH_NAME,
                ImmutableMap.of("name", name));
        commandExecutor.execute(request);
    }

  @Override
  public void scrollToElementWithPredicate(String predicate) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
