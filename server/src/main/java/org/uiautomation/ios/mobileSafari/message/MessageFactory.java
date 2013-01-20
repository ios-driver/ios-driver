package org.uiautomation.ios.mobileSafari.message;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSDriver;
import org.uiautomation.ios.server.command.Handler;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: freynaud Date: 17/01/2013 Time: 15:08 To change this template
 * use File | Settings | File Templates.
 */
public class MessageFactory {

  private final
  Map<String, Class<? extends BaseIOSWebKitMessage>>
      types =
      new HashMap<String, Class<? extends BaseIOSWebKitMessage>>();

  public MessageFactory() {
    types.put("_rpc_reportSetup:", ReportSetupMessage.class);
    types.put("_rpc_reportConnectedApplicationList:", ReportConnectedApplicationsMessage.class);
    types.put("_rpc_applicationSentListing:", ApplicationSentListingMessage.class);
    types.put("_rpc_applicationSentData:", ApplicationDataMessage.class);
    types.put("_rpc_applicationConnected:", ApplicationConnectedMessage.class);
    // _rpc_applicationDisconnected: simulator stopped..

  }

  public IOSMessage create(String rawMessage) {

    try {
      BaseIOSWebKitMessage m = new BaseIOSWebKitMessage(rawMessage);
      Class<? extends BaseIOSWebKitMessage> impl = types.get(m.getSelector());

      if (impl == null) {
        throw new RuntimeException("NI " + m.getSelector());
      }
      Object[] args = new Object[]{rawMessage};
      Class<?>[] argsClass = new Class[]{String.class};

      Constructor<?> c = impl.getConstructor(argsClass);
      IOSMessage message = (IOSMessage) c.newInstance(args);
      return message;
    } catch (Exception e1) {
      e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return null;
  }

  private Document getDocument(String rawMessage) throws DocumentException {
    String message = rawMessage.replace(
        "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">",
        "");
    SAXReader reader = new SAXReader();
    Document document = reader.read(IOUtils.toInputStream(message));
    return document;
  }

  private Node getWebKitDebugMessage(Document d) {
    Node n = d.selectSingleNode("/plist/dict/dict/data");
    return n;
  }
}
