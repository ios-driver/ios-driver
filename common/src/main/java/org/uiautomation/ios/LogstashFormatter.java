package org.uiautomation.ios;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogstashFormatter extends Formatter {


  private static final String SEPARATOR = "LOG_NEXT";
  private static final String EX_START = "LOG_EX_START";
  private static final String EX_STOP = "LOG_EX_STOP";
  private final SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

    sb.append(record.getSourceClassName());
    sb.append(".");
    sb.append(record.getSourceMethodName());
    sb.append("  ");

    // add the exception if there is one
    Throwable t = record.getThrown();
    if (t != null) {
      sb.append(EX_START + t.getMessage());
      sb.append(getStackAsString(t, null));
      sb.append(EX_STOP);
    }

    // Get the formatted message (includes localization
    // and substitution of paramters) and add it to the buffer
    sb.append(formatMessage(record));
    sb.append("\n");

    return sb.toString();
  }

  private String getStackAsString(Throwable t, StringBuffer buff) {
    if (buff == null) {
      buff = new StringBuffer();
    }
    for (StackTraceElement el : t.getStackTrace()) {
      buff.append(el.toString()+SEPARATOR);
    }
    Throwable cause = t.getCause();
    if (cause != null) {
      buff.append("Caused by :");
      getStackAsString(cause, buff);
    }
    return buff.toString();
  }
}
