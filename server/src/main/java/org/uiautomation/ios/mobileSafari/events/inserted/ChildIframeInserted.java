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
package org.uiautomation.ios.mobileSafari.events.inserted;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.NodeId;

public class ChildIframeInserted extends ChildNodeInserted {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((contentDocument == null) ? 0 : contentDocument.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChildIframeInserted other = (ChildIframeInserted) obj;
    if (contentDocument == null) {
      if (other.contentDocument != null)
        return false;
    } else if (!contentDocument.equals(other.contentDocument))
      return false;
    return true;
  }

  private final NodeId contentDocument;

  public ChildIframeInserted(JSONObject message) throws JSONException {
    super(message);

    JSONObject params = message.optJSONObject("params").getJSONObject("node");
    
    JSONObject json = params.getJSONObject("contentDocument");
    contentDocument = new NodeId(json.getInt("nodeId"));
  }

  public NodeId getContentDocument() {
    return contentDocument;
  }

}
