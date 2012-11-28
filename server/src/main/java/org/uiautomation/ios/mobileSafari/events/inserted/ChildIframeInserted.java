package org.uiautomation.ios.mobileSafari.events.inserted;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
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
    if (!"IFRAME".equals(params.opt("nodeName"))) {
      throw new WebDriverException("event parsing error");
    }

    JSONObject json = params.getJSONObject("contentDocument");
    contentDocument = new NodeId(json.getInt("nodeId"));
  }

  public NodeId getContentDocument() {
    return contentDocument;
  }

}
