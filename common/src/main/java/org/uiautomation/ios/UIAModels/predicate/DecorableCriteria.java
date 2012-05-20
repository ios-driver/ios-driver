package org.uiautomation.ios.UIAModels.predicate;

import java.util.ArrayList;
import java.util.List;

public abstract class DecorableCriteria extends AbstractCriteria {

  private List<CriteriaDecorator> decorators = new ArrayList<CriteriaDecorator>();

  @Override
  public void addDecorator(CriteriaDecorator decorator) {
    decorators.add(decorator);
  }

  @Override
  public void decorate() {
    for (CriteriaDecorator d : decorators) {
      d.decorate(this);
    }
  }
}
