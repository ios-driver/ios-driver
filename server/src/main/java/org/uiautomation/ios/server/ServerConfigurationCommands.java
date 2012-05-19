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
package org.uiautomation.ios.server;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class ServerConfigurationCommands implements IParameterValidator {

  @Parameter(names = "-port", description = "Run server on a specific port.", validateWith = ServerConfigurationCommands.class)
  private Integer port = 5555; // set Default port the 5555, if you don't specify the -port at all.

  @Parameter(names = "-host", description = "Run server on a specific host.")
  private String host = "0.0.0.0"; // set Default host the 'localhost', if you don't specify the
                                   // -host at all.

  public int getPort() {
    return this.port;
  }

  public String getHost() {
    return this.host;
  }

  public void validate(String name, String value) throws ParameterException {
    if (!value.equals("")) {
      int validate_val = Integer.parseInt(value);
      if (validate_val < 0) { // port range
        throw new ParameterException("Parameter " + name + " should be positive (found " + value
            + ")");
      }
    }
  }
}
