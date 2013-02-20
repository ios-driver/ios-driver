/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */
package org.uiautomation.ios.mobileSafari.remoteWebkitProtocol;

import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.mobileSafari.PlistManager;
import org.uiautomation.ios.mobileSafari.ResponseFinder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class SimulatorProtocolImpl extends WebInspector2 {

  private static final Logger log = Logger.getLogger(SimulatorProtocolImpl.class.getName());

  private static Socket socket;
  private ByteArrayOutputStream buf = new ByteArrayOutputStream();

  private final String LOCALHOST_IPV6 = "::1";
  private final int port = 27753;


  private final boolean displayPerformance = false;


  public SimulatorProtocolImpl(MessageListener listener,
                               ResponseFinder... finders) {
    super(listener, finders);
    start();
  }


  public void start() {
    try {
      if (socket != null && (socket.isConnected() || !socket.isClosed())) {
        socket.close();
      }
      socket = new Socket(LOCALHOST_IPV6, port);
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
    startListenerThread();
    //sendCommand(PlistManager.SET_CONNECTION_KEY);
    //sendCommand(PlistManager.CONNECT_TO_APP);
    //sendCommand(PlistManager.SET_SENDER_KEY);
  }


  /**
   * sends the message to the AUT.
   */
  protected void sendMessage(String xml) {
    //System.out.println("sending " + xml);
    try {
      byte[] bytes = xml.getBytes("UTF-8");
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
        String
            message =
            new PlistManager().plistBinaryToXml(Arrays.copyOfRange(bytes, 4, size + 4));
        handler.handle(message);
        //System.out.println(message);
        buf = new ByteArrayOutputStream();
        buf.write(bytes, 4 + size, bytes.length - size - 4);
        //return message;
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
  protected void read() throws Exception {
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
    stopListenerThread();
    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
