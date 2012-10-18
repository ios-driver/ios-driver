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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.ExceptionStatus;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class FailedWebDriverLikeResponse extends WebDriverLikeResponse {

  public FailedWebDriverLikeResponse(JSONObject content) throws JSONException {
    super(content);
  }

  public FailedWebDriverLikeResponse(String sessionId, Exception e) {
    super(sessionId, 13, null);
    try {
      setValue(serializeException(e));
    } catch (JSONException e2) {
      throw new IOSAutomationException(e2);
    }
  }



  private JSONObject serializeException(Throwable e) throws JSONException {
    JSONObject res = new JSONObject();
    res.put("message", e.getMessage());
    res.put("class", e.getClass().getCanonicalName());
    res.put("screen", "TODO");
    res.put("stacktrace", serializeStackTrace(e.getStackTrace()));
    if (e.getCause() != null) {
      res.put("cause", serializeException(e.getCause()));
    }
    return res;

  }

  private JSONArray serializeStackTrace(StackTraceElement[] els) throws JSONException {
    JSONArray stacktace = new JSONArray();
    for (StackTraceElement el : els) {
      JSONObject stckEl = new JSONObject();
      stckEl.put("fileName", el.getFileName());
      stckEl.put("className", el.getClassName());
      stckEl.put("methodName", el.getMethodName());
      stckEl.put("lineNumber", el.getLineNumber());
      stacktace.put(stckEl);
    }
    return stacktace;
  }

  public static Exception createThrowable(JSONObject serialized) {
    try {
      Class<? extends Exception> c = getException(serialized);

      String msg = serialized.getString("message");

      JSONObject cause = serialized.optJSONObject("cause");
      Throwable causeThrowable = null;
      if (cause != null) {
        causeThrowable = createThrowable(cause);
      }

      Object[] args;
      Constructor<?> constructor;
      Throwable exception;
      try {
        Class<?>[] argsClass = new Class[] {String.class, Throwable.class};
        constructor = c.getConstructor(argsClass);
        args = new Object[] {msg, (Exception) causeThrowable};
        exception = (Throwable) constructor.newInstance(args);
        
      }catch (NoSuchMethodException e){
        Class<?>[] argsClass = new Class[] {String.class};
        constructor = c.getConstructor(argsClass);
        args = new Object[] {msg};
        exception = (Throwable) constructor.newInstance(args);
      }

      JSONArray stack = serialized.optJSONArray("stacktrace");
      if (stack != null) {
        List<StackTraceElement> stacks = new ArrayList<StackTraceElement>();
        for (int i = 0; i < stack.length(); i++) {
          JSONObject o = stack.getJSONObject(i);
          String fileName = o.getString("fileName");
          String className = o.getString("className");
          String methodName = o.getString("methodName");
          int lineNumber = o.getInt("lineNumber");
          StackTraceElement el = new StackTraceElement(className, methodName, fileName, lineNumber);
          stacks.add(el);
        }
        exception.setStackTrace(stacks.toArray(new StackTraceElement[0]));
      }
      return (Exception) exception;
    } catch (Exception e) {
      throw new IOSAutomationException("error deserializing the exception from the server : "
          + serialized.toString(), e);
    }

  }

 
  private static Class<? extends Exception> getException(JSONObject serialized)
      throws ClassNotFoundException {
    String clazz = serialized.optString("class");
    if (clazz == null || clazz.isEmpty()) {
      Integer statusCode = serialized.optInt("status");
      if (statusCode != null) {
        return ExceptionStatus.getExceptionForStatus(statusCode);
      }
      clazz = IOSAutomationException.class.getCanonicalName();
    }
    return (Class<? extends Exception>) Class.forName(clazz);
  }

}
