package org.uiautomation.ios.mobileSafari.message;

import com.dd.plist.NSDictionary;
import com.dd.plist.XMLPropertyListParser;


public class BaseIOSWebKitMessage implements IOSMessage {

  protected final String rawMessage;
  protected final NSDictionary rootDict;
  protected final String selector;
  protected final NSDictionary arguments;

  public BaseIOSWebKitMessage(String rawMessage) throws Exception {
    this.rawMessage = rawMessage;

    rootDict = (NSDictionary) XMLPropertyListParser.parse(rawMessage.getBytes());
    selector = rootDict.objectForKey("__selector").toString();
    arguments = (NSDictionary) rootDict.objectForKey("__argument");

  }

  @Override
  public String getSelector() {
    return selector;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "\n\tArguments:\n\t" + toString(arguments);
  }

  protected String toString(NSDictionary args) {
    if (args == null) {
      return null;
    }
    StringBuilder b = new StringBuilder();
    for (String key : args.allKeys()) {
      b.append(key + "=");
      b.append(args.objectForKey(key));
    }
    return b.toString();
  }
}
