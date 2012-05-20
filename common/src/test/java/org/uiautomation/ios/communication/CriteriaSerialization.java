package org.uiautomation.ios.communication;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;

public class CriteriaSerialization {



  @Test
  public void name() throws Exception {
    NameCriteria name = new NameCriteria("the name");
    JSONObject o = name.getJSONRepresentation();

    NameCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }

  @Test
  public void value() throws Exception {
    ValueCriteria name = new ValueCriteria("the value");
    JSONObject o = name.getJSONRepresentation();

    ValueCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }

  @Test
  public void label() throws Exception {
    LabelCriteria name = new LabelCriteria("the label");
    JSONObject o = name.getJSONRepresentation();

    LabelCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }
}
