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

package org.uiautomation.ios.instruments;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.ServerSideSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class InstrumentsFactory {


  private final static List<Class> implementations = new ArrayList();

  static {
    implementations.add(InstrumentsCommandLine.class);
    implementations.add(NoInstrumentsImplementationAvailable.class);
  }


  public static void registerNewImplementation(Class clazz) {
    implementations.add(0, clazz);
  }

  private static Instruments getImplementation(Class clazz, ServerSideSession serverSideSession) throws Exception{
    try {
      Constructor c = clazz.getConstructor(ServerSideSession.class);
      return (Instruments)c.newInstance(serverSideSession);
    } catch (NoSuchMethodException e) {
      throw new WebDriverException(
          "The instruments implementation must have a constructor taking a ServerSideSession as a parameter.");
    } catch (InvocationTargetException e){
      throw (Exception)e.getTargetException();
    }
  }


  public static Instruments getInstruments(ServerSideSession session) throws Exception {
    for (Class implClazz : implementations) {

        Instruments instruments = getImplementation(implClazz, session);
        if (instruments.isCompatible(session)) {
          return instruments;
        }
    }
    throw new WebDriverException("Cannot find a valid implementation for the session " + session);
  }


}
