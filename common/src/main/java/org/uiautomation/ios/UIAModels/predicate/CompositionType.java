package org.uiautomation.ios.UIAModels.predicate;

import org.uiautomation.ios.exceptions.InvalidCriteriaException;

public enum CompositionType {
  AND, OR, NOT;


  @SuppressWarnings("unchecked")
  public Class<? extends ComposedCriteria> getAssociatedClass() {
    String pack = this.getDeclaringClass().getPackage().getName();
    String camel =
        this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
    String clazz = pack + "." + camel + "Criteria";
    try {
      return (Class<? extends ComposedCriteria>) Class.forName(clazz);
    } catch (ClassNotFoundException e) {
      throw new InvalidCriteriaException(clazz + " isn't a valid class.", e);
    }
  }
}
