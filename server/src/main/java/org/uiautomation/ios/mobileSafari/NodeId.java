package org.uiautomation.ios.mobileSafari;

public class NodeId {

  private final int id;

  public NodeId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "" + id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NodeId other = (NodeId) obj;
    if (id != other.id) return false;
    return true;
  }


}
