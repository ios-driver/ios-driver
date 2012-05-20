package org.uiautomation.ios.server.application;

import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public class ServerSideL10NFactory {

  private final ServerSideL10NDecorator decorator;

  public ServerSideL10NFactory(IOSApplication aut) {
    this.decorator = new ServerSideL10NDecorator(aut);
  }

  public NameCriteria nameCriteria(String serverKey) {
    NameCriteria criteria = new NameCriteria(serverKey, MatchingStrategy.serverL10N);
    criteria.addDecorator(decorator);
    criteria.decorate();
    return criteria;
  }



  class ServerSideL10NDecorator implements CriteriaDecorator {

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
      // TODO freynaud make it regex
      criteria.setValue(newValue);
      criteria.setStrategy(MatchingStrategy.regex);
    }


    private boolean isElligible(Criteria c) {
      if (c instanceof PropertyEqualCriteria) {
        PropertyEqualCriteria crit = (PropertyEqualCriteria) c;
        return crit.getMatchingStrategy() == MatchingStrategy.serverL10N;
      }
      return false;
    }

  }

}
