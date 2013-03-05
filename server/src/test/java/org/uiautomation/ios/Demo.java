/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package org.uiautomation.ios;

import org.uiautomation.ios.server.IOSServer;
import org.uiautomation.ios.server.IOSServerConfiguration;

/**
 * example for the documentation
 */
public class Demo {


  public static void main(String[] args) throws Exception {
    String[] a = {"-port", "4444", "-host", "localhost",
                  "-aut", SampleApps.getUICatalogFile(),
                  "-aut", SampleApps.getUICatalogIpad(),
                  "-beta"};
    IOSServerConfiguration config = IOSServerConfiguration.create(a);

    IOSServer server = new IOSServer(config);
    server.start();


  }

}
