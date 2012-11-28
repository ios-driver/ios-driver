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
    
    String[] pieces =record.getSourceClassName().split("\\.");
    String name = pieces[pieces.length-1];
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
