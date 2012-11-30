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
package org.uiautomation.ios.client.uiamodels.impl;

import java.util.Map;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.CriteriaDecorator;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.PropertyEqualCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;

public class ClientSideCriteriaFactory {

  private CriteriaDecorator decorator;

  public ClientSideCriteriaFactory(Map<String, String> contentByKey) {
    decorator = new ClientSideL10NDecorator(contentByKey);
  }

  public NameCriteria nameCriteria(String expected, L10NStrategy l10nStrategy,
      MatchingStrategy matchingStrategy) {
    NameCriteria c = new NameCriteria(expected, l10nStrategy, matchingStrategy);
    c.addDecorator(decorator);
    c.decorate();
    return c;

  }

  public LabelCriteria labelCriteria(String expected, L10NStrategy l10nStrategy,
      MatchingStrategy matchingStrategy) {
    LabelCriteria c = new LabelCriteria(expected, l10nStrategy, matchingStrategy);
    c.addDecorator(decorator);
    c.decorate();
    return c;
  }

  public ValueCriteria valueCriteria(String expected, L10NStrategy l10nStrategy,
      MatchingStrategy matchingStrategy) {
    ValueCriteria c = new ValueCriteria(expected, l10nStrategy, matchingStrategy);
    c.addDecorator(decorator);
    c.decorate();
    return c;
  }


  class ClientSideL10NDecorator implements CriteriaDecorator {

    private Map<String, String> clientSideL10n;

    public ClientSideL10NDecorator(Map<String, String> clientSideL10n) {
      this.clientSideL10n = clientSideL10n;
    }

    @Override
    public void decorate(Criteria c) {
      if (requiresDecoration(c)) {
        PropertyEqualCriteria criteria = (PropertyEqualCriteria) c;

        String newValue = localizeString(criteria.getValue());
        criteria.setValue(newValue);
        criteria.setL10nstrategy(L10NStrategy.none);
      }
    }

    private String localizeString(String value) {
      if (clientSideL10n == null) {
        throw new WebDriverException(
            "you need to provide client side content to use the client side l10n");
      }
      String res = clientSideL10n.get(value);
      if (res == null) {
        throw new WebDriverException("no client side content provided for " + value);
      } else {
        return res;
      }
    }
  }

  private boolean requiresDecoration(Criteria c) {
    if (c instanceof PropertyEqualCriteria) {
      PropertyEqualCriteria crit = (PropertyEqualCriteria) c;
      L10NStrategy strategy = crit.getL10nstrategy();
      if (strategy == L10NStrategy.clientL10N) {
        return true;
      }
    }
    return false;
  }



}
