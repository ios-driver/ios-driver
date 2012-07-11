package org.uiautomation.ios.server.application;

import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public class ServerSideL10NDecorator implements CriteriaDecorator {

  private final IOSApplication app;

  public ServerSideL10NDecorator(IOSApplication app) {
    this.app = app;
  }

  @Override
  public void decorate(Criteria c) {
    if (!isElligible(c)) {
      return;
    }
    PropertyEqualCriteria criteria = (PropertyEqualCriteria) c;
    String oldValue = criteria.getValue();
    LanguageDictionary dict = app.getDictionary(app.getCurrentLanguage());
    String newValue = dict.getContentForKey(oldValue);
    if (newValue == null) {
      throw new InvalidCriteriaException("No entry for key " + oldValue + " in dictionary for "
          + app.getCurrentLanguage());
    }

    criteria.setValue(LanguageDictionary.getRegexPattern(newValue));
    criteria.setL10nstrategy(L10NStrategy.none);
  }


  private boolean isElligible(Criteria c) {
    if (c instanceof PropertyEqualCriteria) {
      PropertyEqualCriteria crit = (PropertyEqualCriteria) c;
      return crit.getL10nstrategy() == L10NStrategy.serverL10N;
    }
    return false;
  }


}
