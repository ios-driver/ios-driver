package org.uiautomation.ios;


import java.io.IOException;
import java.util.logging.FileHandler;

public class LogstashFileHandler extends FileHandler {
    public LogstashFileHandler() throws IOException, SecurityException {
        super();
    }

    public LogstashFileHandler(String pattern) throws IOException, SecurityException {
        super(pattern);
    }

    public LogstashFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        super(pattern, append);
    }

    public LogstashFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
        super(pattern, limit, count);
    }

    public LogstashFileHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
    }
}
