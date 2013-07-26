package org.uiautomation.ios.server.utils;

public class IOSVersion {

  private final String version;

  public IOSVersion(String version) {
    this.version = version;
  }

  private int compare(String other) {
    String[] piecesMe = version.split("\\.");
    String[] piecesOther = other.split("\\.");
    int i = 0; // left has more weigth= starting at 0
    // check for the first int no equal.
    while (i < piecesMe.length && i < piecesOther.length && piecesMe[i].equals(piecesOther[i])) {
      i++;
    }

    if (i < piecesMe.length && i < piecesOther.length) {
      int diff = Integer.valueOf(piecesMe[i]).compareTo(Integer.valueOf(piecesOther[i]));
      return diff < 0 ? -1 : diff == 0 ? 0 : 1;
    }

    return piecesMe.length < piecesOther.length ? -1
                                                : piecesMe.length == piecesOther.length ? 0 : 1;
  }


  public boolean isGreaterThan(String other) {
    return compare(other) == 1;
  }

  public boolean equals(String other) {
    return compare(other) == 0;
  }

  public boolean isGreaterOrEqualTo(String other) {
    return compare(other) != -1;
  }
}
