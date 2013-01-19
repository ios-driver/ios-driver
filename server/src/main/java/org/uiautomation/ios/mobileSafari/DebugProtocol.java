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
package org.uiautomation.ios.mobileSafari;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.mobileSafari.events.Event;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.RemoteExceptionException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class DebugProtocol {

  private static final Logger log = Logger.getLogger(DebugProtocol.class.getName());

  private static Socket socket;
  private ByteArrayOutputStream buf = new ByteArrayOutputStream();
  private final PlistManager plist = new PlistManager();
  private final MessageHandler handler;

  private final String LOCALHOST_IPV6 = "::1";
  private final int port = 27753;

  private int commandId = 0;

  private final boolean displayPerformance = false;
  private Thread listen;
  private volatile boolean keepGoing = true;
  private final String bundleId;

  /**
   * connect to the webview
   */
  public DebugProtocol(EventListener listener, String bundleId, ResponseFinder... finders)
      throws Exception,
             InterruptedException {
    this.handler = new DefaultMessageHandler(listener, finders);
    this.bundleId = bundleId;

    init();

    listen = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          while (keepGoing) {
            Thread.sleep(50);
            listenOnce();
          }
        } catch (IOException socketClosed) {
          //ignore.
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    listen.start();

    Thread monitor = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(5000);
            System.out.println("Monitor : " + sendCommand(DOM.getDocument()).optJSONObject("root")
                .optString("documentURL"));
          } catch (Exception e) {
            System.out.println("Monitor issue " + e.getMessage());
          }
        }
      }
    });
    monitor.start();
  }

  public void init() throws Exception {
    System.out.println("\nINIT\n");
    if (socket != null && (socket.isConnected() || !socket.isClosed())) {
      socket.close();
    }
    socket = new Socket(LOCALHOST_IPV6, port);
    sendCommand(PlistManager.SET_CONNECTION_KEY);
    sendCommand(PlistManager.CONNECT_TO_APP);
    sendCommand(PlistManager.SET_SENDER_KEY);
  }

  public void switchTo() throws Exception {
    sendCommand(PlistManager.SET_SENDER_KEY);
  }

  /**
   * sends the json formated command.
   *
   * @param command . For command format, read https://www.webkit.org/blog/?p=1875&preview=true.
   */
  public JSONObject sendCommand(JSONObject command) {
    try {
      commandId++;
      command.put("id", commandId);

      long start = System.currentTimeMillis();

      String xml = plist.JSONCommand(command);
      // perf("got xml \t" + (System.currentTimeMillis() - start) + "ms.");
      xml = xml.replace("$bundleId", this.bundleId);

      byte[] bytes = plist.plistXmlToBinary(xml);
      // perf("prepared request \t" + (System.currentTimeMillis() - start) +
      // "ms.");
      sendBinaryMessage(bytes);
      // perf("sent request \t\t" + (System.currentTimeMillis() - start) + "ms.");
      JSONObject response = handler.getResponse(command.getInt("id"));
      // perf("got response\t\t" + (System.currentTimeMillis() - start) + "ms.");
      JSONObject error = response.optJSONObject("error");
      if (error != null) {
        throw new RemoteExceptionException(error, command);
      } else if (response.optBoolean("wasThrown", false)) {
        throw new WebDriverException("remote JS exception " + response.toString(2));
      } else {
        log.fine(System.currentTimeMillis() + "\t\t" + (System.currentTimeMillis() - start) + "ms\t"
                 + command.getString("method") + " " + command);
        return response.getJSONObject("result");
      }
    } catch (JSONException e) {
      throw new WebDriverException(e);
    }

  }


  /**
   * Some commands do not follow the Remote Debugging protocol. For instance the ones that
   * initialize the connection between the webview and the remote debugger do not have json content,
   * they're just an exchange of keys.
   */
  private void sendCommand(String command) throws Exception {
    String xml = plist.loadFromTemplate(command);
    xml = xml.replace("$bundleId", bundleId);
    byte[] bytes = plist.plistXmlToBinary(xml);
    sendBinaryMessage(bytes);
  }

  /**
   * sends the message to the AUT.
   */
  private void sendBinaryMessage(byte[] bytes) {

    try {
      OutputStream os = socket.getOutputStream();
      os.write((byte) ((bytes.length >> 24) & 0xFF));
      os.write((byte) ((bytes.length >> 16) & 0xFF));
      os.write((byte) ((bytes.length >> 8) & 0xFF));
      os.write((byte) (bytes.length & 0xFF));
      os.write(bytes);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }

  /**
   * reads the messages from the AUT.
   */
  private void pushInput(byte[] inputBytes) throws Exception {
    buf.write(inputBytes);
    while (buf.size() >= 4) {
      byte[] bytes = buf.toByteArray();
      int size = 0;
      size = (size << 8) + byteToInt(bytes[0]);
      size = (size << 8) + byteToInt(bytes[1]);
      size = (size << 8) + byteToInt(bytes[2]);
      size = (size << 8) + byteToInt(bytes[3]);
      if (bytes.length >= 4 + size) {
        String message = plist.plistBinaryToXml(Arrays.copyOfRange(bytes, 4, size + 4));
        handler.handle(message);
        buf = new ByteArrayOutputStream();
        buf.write(bytes, 4 + size, bytes.length - size - 4);
      } else {
        // System.err.println("Expecting " + size + " + 4 bytes. Buffered " +
        // bytes.length + ".");
        break;
      }
    }
  }

  /**
   * listen for a complete message.
   */
  private void listenOnce() throws Exception {
    InputStream is = socket.getInputStream();
    while (is.available() > 0) {
      byte[] bytes = new byte[is.available()];
      is.read(bytes);
      // System.err.println("Received " + bytes.length + " bytes.");
      pushInput(bytes);
    }
  }

  private int byteToInt(byte b) {
    int i = (int) b;
    return i >= 0 ? i : i + 256;
  }

  public void stop() {
    if (handler != null) {
      handler.stop();
    }

    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    keepGoing = false;
    if (listen != null) {
      listen.interrupt();
    }
    keepGoing = true;

  }

  public static void main(String[] args) throws Exception {
    test();

  }

  public static void test() throws Exception {
    final DebugProtocol protocol = new DebugProtocol(new EventListener() {
      @Override
      public void onPageLoad() {

      }

      @Override
      public void domHasChanged(Event event) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      @Override
      public void frameDied(JSONObject message) {
        //To change body of implemented methods use File | Settings | File Templates.
      }
      //}, "com.apple.mobilesafari");
    }, "com.yourcompany.UICatalog");

    while (true) {
      Thread.sleep(5000);
      try {
        System.out.println(
            "Monitor : " + protocol.sendCommand(DOM.getDocument()).optJSONObject("root")
                .optString("documentURL"));
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }

    }
  }
}
