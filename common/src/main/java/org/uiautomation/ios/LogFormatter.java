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
package org.uiautomation.ios;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

  private final SimpleDateFormat datef = new SimpleDateFormat("mm:ss:SSS");

  @Override
  public String format(LogRecord record) {
    // Create a StringBuffer to contain the formatted record
    // start with the date.
    StringBuffer sb = new StringBuffer();

    // Get the date from the LogRecord and add it to the buffer
    Date date = new Date(record.getMillis());
    sb.append(datef.format(date));
    sb.append(" ");

    // Get the level name and add it to the buffer
    sb.append(record.getLevel().getName());
    sb.append(" ");

    String[] pieces = record.getSourceClassName().split("\\.");
    String name = pieces[pieces.length - 1];
    sb.append(name);
    sb.append(".");
    sb.append(record.getSourceMethodName());
    sb.append("  ");

    // Get the formatted message (includes localization
    // and substitution of paramters) and add it to the buffer
    sb.append(formatMessage(record));
    sb.append("\n");

    return sb.toString();
  }
}
