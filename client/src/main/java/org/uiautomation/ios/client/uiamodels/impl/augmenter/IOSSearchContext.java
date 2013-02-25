package org.uiautomation.ios.client.uiamodels.impl.augmenter;

import org.openqa.selenium.NoSuchElementException;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.Criteria;

import java.util.List;

public interface IOSSearchContext {

  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException;

  public List<UIAElement> findElements(Criteria c);
}
