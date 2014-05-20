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

import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSTemplate implements stylized substitution for JavaScript code templates. For example, from SetTimeoutNHandler:
 *
 *   private static final JSTemplate template = new JSTemplate(
 *       "UIAutomation.setTimeout('%:type$s',%:ms$d);" +
 *       "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
 *       "type", "ms", "sessionId");
 *   ...
 *   protected String generateScript(String type, int timeout, String sessionId) {
 *     return template.generate(type, timeout, sessionId);
 *   }
 *
 * The JSTemplate constructor substitutes the named parameters with indices such that they can later be used for
 * formatted printing. Note that for e.g. "type", the pattern matched during substitution is "%:type$"; the leading and
 * trailing character are critical to proper interaction with Formatter.Internally, the template for the above example
 * becomes:
 *
 *   "UIAutomation.template('%1$s',%2$d);" +
 *   "UIAutomation.createJSONResponse('%3$s',0,'')"
 *
 * The arguments passed to the generate() method are passed directly on to Formatter.format().
 */
public class JSTemplate {

  private static final Logger log = Logger.getLogger(JSTemplate.class.getName());

  private final String format;
  private final int numArgs;

  public JSTemplate(String template, String... args) {
    String s = template;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      // Convert e.g. "%:arg$s' to "%1$s".
      String sPrev = s;
      s = s.replaceAll(String.format("%%:%s[$]", arg), String.format("%%%d\\$", i+1));
      if (s.equals(sPrev)) {
        throw new IllegalArgumentException(String.format("No substitution occurred for \"%s\"", arg));
      }
    }
    if (s.matches("%:[A-Za-z_][A-Za-z_0-9]+[$]")) {
      throw new IllegalArgumentException("Not all template parameters were substituted");
    }
    this.format = s;
    this.numArgs = args.length;
  }

  public String generate(Object... args) {
    // Formatter silently ignores excess arguments. The following check requires strict arity equality to catch all
    // mismatches.
    if (args.length != numArgs) {
      throw new IllegalArgumentException(String.format("Argument count mismatch (%d should be %d)",
          args.length, numArgs));
    }
    String js = new Formatter().format(format, args).toString();
    if (log.isLoggable(Level.FINE))
      log.fine(String.format("JS: %s", js));
    return js;
  }
}