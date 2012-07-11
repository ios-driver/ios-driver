package org.uiautomation.ios.server.application;

import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public class ServerSideL10NDecorator implements CriteriaDecorator {

  private final IOSApplication app;

  public ServerSideL10NDecorator(IOSApplication app) {
    this.app = app;
  }

  @Override
  public void decorate(Criteria c) {
    decorateContent(c);
    decorateMatching(c);
  }



  private void decorateContent(Criteria c) {
    if (!isElligibleForContent(c)) {
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

  private void decorateMatching(Criteria c) {
    if (!isElligibleForMatching(c)) {
      return;
    }
    PropertyEqualCriteria criteria = (PropertyEqualCriteria) c;
    String oldValue = criteria.getValue();

    String newValue = oldValue;
    switch (criteria.getMatchingStrategy()) {
      case starts:
        newValue = oldValue + "(.*)";
        break;
      case contains:
        newValue = "(.*)" + oldValue + "(.*)";
        break;
      case ends:
        newValue = "(.*)" + oldValue;
        break;
      default:
        throw new IOSAutomationException("Can't find strategy");
    }
    criteria.setValue(newValue);
    criteria.setMatchingStrategy(MatchingStrategy.regex);

  }

  private boolean isElligibleForContent(Criteria c) {
    if (c instanceof PropertyEqualCriteria) {
      PropertyEqualCriteria crit = (PropertyEqualCriteria) c;
      return crit.getL10nstrategy() == L10NStrategy.serverL10N;
    }
    return false;
  }

  private boolean isElligibleForMatching(Criteria c) {
    if (c instanceof PropertyEqualCriteria) {
      PropertyEqualCriteria crit = (PropertyEqualCriteria) c;
      if (crit.getMatchingStrategy() == MatchingStrategy.exact) {
        return false;
      }
      if (crit.getMatchingStrategy() == MatchingStrategy.regex) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }


}
