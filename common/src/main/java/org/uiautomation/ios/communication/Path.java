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
package org.uiautomation.ios.communication;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.remote.SessionId;

public class Path {

  // for methods that are called without a reference, use 0.
  public static final String ROOT = "0";
  private String path;
  private Set<String> variables = new HashSet<String>();

  public static final String SESSION_ID = ":sessionId";
  public static final String REFERENCE = ":reference";



  public Path(WebDriverLikeCommand command) {
    this.path = command.path();

    String[] pieces = path.split("/");
    for (String piece : pieces) {
      if (piece.startsWith(":")) {
        variables.add(piece);
      }
    }
  }

  public Path withSession(SessionId sessionId) {
    validateAndReplace(SESSION_ID, sessionId.toString());
    return this;
  }

  public Path withReference(String reference) {
    validateAndReplace(REFERENCE, reference);
    return this;
  }

  /**
   * removes the :reference for the path, for those commands with optional object. (for instance log
   * object tree can be invoked without a reference, and logs from the root element in that case.
   * 
   * @return
   */
  public Path withoutReference() {
    withReference(ROOT);
    return this;
  }



  public void validateAndReplace(String variable, String value) {
    validateContains(variable);
    replace(variable, value);
  }

  private void replace(String variable, String value) {
    path = path.replace(variable, value);
  }

  private void validateContains(String variable) {
    if (!path.contains(variable)) {
      throw new InvalidParameterException(path + " doesn't have " + variable + " as a variable.");
    }
  }

  public String getPath() {
    return path;
  }



}
