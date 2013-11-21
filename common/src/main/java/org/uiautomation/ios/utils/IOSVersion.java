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

package org.uiautomation.ios.utils;

public class IOSVersion {

  private final String version;

  public IOSVersion(String version) {
    this.version = version;
  }

  private int compare(String other) {
    String[] piecesMe = version.split("\\.");
    String[] piecesOther = other.split("\\.");
    int i = 0; // left has more weight= starting at 0
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
