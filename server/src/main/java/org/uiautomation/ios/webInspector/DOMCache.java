package org.uiautomation.ios.webInspector;

import java.util.ArrayList;
import java.util.List;

import org.uiautomation.ios.webInspector.DOM.Node;

public class DOMCache {

  private final List<Node> iframes = new ArrayList<Node>();


  public void addIframe(Node iframe) {
    iframes.add(iframe);
  }

  public void addIframe(List<Node> iframes) {
    this.iframes.addAll(iframes);
  }


  public List<Node> getIFrame() {
    return iframes;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(iframes.size() + " iframes, [");
    for (Node iframe : iframes) {
      b.append("id=" + iframe.getContentDocument().getId());
      b.append(",src=" + iframe.getContentDocument().getDocumentURL());
    }
    b.append("]");
    return b.toString();
  }


}
