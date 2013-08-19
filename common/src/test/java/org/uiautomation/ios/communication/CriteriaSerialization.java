/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.communication;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.predicate.AbstractCriteria;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.LabelCriteria;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.NotCriteria;
import org.uiautomation.ios.UIAModels.predicate.OrCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.UIAModels.predicate.ValueCriteria;

public class CriteriaSerialization {


  @Test
  public void uiclass() throws Exception {
    TypeCriteria name = new TypeCriteria(UIAElement.class);
    JSONObject o = name.stringify();

    TypeCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }
  
  @Test
  public void name() throws Exception {
    NameCriteria name = new NameCriteria("the name");
    JSONObject o = name.stringify();

    NameCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }

  @Test
  public void value() throws Exception {
    ValueCriteria name = new ValueCriteria("the value");
    JSONObject o = name.stringify();

    ValueCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }

  @Test
  public void label() throws Exception {
    LabelCriteria name = new LabelCriteria("the label");
    JSONObject o = name.stringify();

    LabelCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(name.getClass(), c.getClass());
    Assert.assertEquals(name.getValue(), c.getValue());
    Assert.assertEquals(name.getMatchingStrategy(), c.getMatchingStrategy());
  }


  @Test
  public void and() throws Exception {
    LabelCriteria l = new LabelCriteria("the label");
    ValueCriteria v = new ValueCriteria("the value");
    AndCriteria and = new AndCriteria(l, v);

    JSONObject o = and.stringify();

    AndCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(and.getClass(), c.getClass());
  }

  @Test
  public void or() throws Exception {
    LabelCriteria l = new LabelCriteria("the label");
    ValueCriteria v = new ValueCriteria("the value");
    OrCriteria or = new OrCriteria(l, v);

    JSONObject o = or.stringify();

    OrCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(or.getClass(), c.getClass());

  }

  @Test
  public void not() throws Exception {
    LabelCriteria l = new LabelCriteria("the label");

    NotCriteria not = new NotCriteria(l);

    JSONObject o = not.stringify();

    NotCriteria c = AbstractCriteria.parse(o);

    Assert.assertEquals(not.getClass(), c.getClass());

  }
}
