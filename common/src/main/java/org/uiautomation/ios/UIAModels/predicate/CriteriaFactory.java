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
package org.uiautomation.ios.UIAModels.predicate;

import java.util.Map;

public class CriteriaFactory {

  private Map<String, String> clientSideL10n;

  public void addClientSideContent(Map<String, String> contentByKey) {
    this.clientSideL10n = contentByKey;
  }

  public NameCriteria nameCriteria(String expected, MatchingStrategy startegy) {
    return new NameCriteria(expected, startegy, clientSideL10n);
  }

  public LabelCriteria labelCriteria(String expected, MatchingStrategy startegy) {
    return new LabelCriteria(expected, startegy, clientSideL10n);
  }

  public ValueCriteria valueCriteria(String expected, MatchingStrategy startegy) {
    return new ValueCriteria(expected, startegy, clientSideL10n);
  }

}
