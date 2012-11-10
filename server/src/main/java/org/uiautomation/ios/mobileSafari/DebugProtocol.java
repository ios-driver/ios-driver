package org.uiautomation.ios.mobileSafari;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;
import org.uiautomation.ios.webInspector.DOM.RemoteObjectArray;

public class DebugProtocol {
  private final Socket socket;
  private ByteArrayOutputStream buf = new ByteArrayOutputStream();
  private final PlistManager plist = new PlistManager();
  private final MessageHandler handler;

  private final String LOCALHOST_IPV6 = "::1";
  private final int port = 27753;

  private int commandId = 0;

  private final boolean displayPerformance = false;

  /**
   * connect to the webview
   * 
   * @param handler for server initiated notifications
   * @throws UnknownHostException
   * @throws IOException
   * @throws InterruptedException
   */
  public DebugProtocol(MessageHandler handler) throws UnknownHostException, IOException,
      InterruptedException {
    this.handler = handler;

    socket = new Socket(LOCALHOST_IPV6, port);
    sendCommand(PlistManager.SET_CONNECTION_KEY);
    sendCommand(PlistManager.CONNECT_TO_APP);
    sendCommand(PlistManager.SET_SENDER_KEY);


    Thread listen = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          while (true) {
            Thread.sleep(50);
            listenOnce();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    listen.start();
  }


  /**
   * sends the json formated command.
   * 
   * @param command. For command format, read https://www.webkit.org/blog/?p=1875&preview=true.
   * @return
   * @throws Exception
   */
  public JSONObject sendCommand(JSONObject command) throws Exception {
    perf("\n--------------" + command.getString("method") + "-------------------");
    long start = System.currentTimeMillis();
    commandId++;
    command.put("id", commandId);
    String xml = plist.JSONCommand(command);
    byte[] bytes = plist.plistXmlToBinary(xml);
    perf("prepared request \t" + (System.currentTimeMillis() - start) + "ms.");
    sendBinaryMessage(bytes);
    perf("sent request \t\t" + (System.currentTimeMillis() - start) + "ms.");
    JSONObject response = handler.getResponse(command.getInt("id"));
    perf("got response\t\t" + (System.currentTimeMillis() - start) + "ms.");
    JSONObject error = response.optJSONObject("error");
    if (error != null) {
      throw new Exception(error.toString(2));
    } else if (response.optBoolean("wasThrown", false)) {
      throw new Exception("remote JS exception " + response.toString(2));
    } else {

      perf("total\t\t\t" + (System.currentTimeMillis() - start) + "ms.");
      return response.getJSONObject("result");
    }
  }

  private void perf(String msg) {
    if (displayPerformance) {
      System.out.println(msg);
    }

  }


  /**
   * Some commands do not follow the Remote Debugging protocol. For instance the ones that
   * initialize the connection between the webview and the remote debugger do not have json content,
   * they're just an exchange of keys.
   * 
   * @param command
   * @throws IOException
   * @throws InterruptedException
   */
  private void sendCommand(String command) throws IOException, InterruptedException {
    String xml = plist.loadFromTemplate(command);
    byte[] bytes = plist.plistXmlToBinary(xml);
    sendBinaryMessage(bytes);
  }


  /**
   * sends the message to the AUT.
   * 
   * @param bytes
   * @throws IOException
   */
  private void sendBinaryMessage(byte[] bytes) throws IOException {
    OutputStream os = socket.getOutputStream();
    os.write((byte) ((bytes.length >> 24) & 0xFF));
    os.write((byte) ((bytes.length >> 16) & 0xFF));
    os.write((byte) ((bytes.length >> 8) & 0xFF));
    os.write((byte) (bytes.length & 0xFF));
    os.write(bytes);
    // System.err.println("Sending " + bytes.length + " bytes.");
  }


  /**
   * reads the messages from the AUT.
   * 
   * @param inputBytes
   * @throws IOException
   * @throws InterruptedException
   */
  private void pushInput(byte[] inputBytes) throws IOException, InterruptedException {
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
        System.err.println("Expecting " + size + " + 4 bytes. Buffered " + bytes.length + ".");
        break;
      }
    }
  }


  /**
   * listen for a complete message.
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  private void listenOnce() throws IOException, InterruptedException {
    InputStream is = socket.getInputStream();
    while (is.available() > 0) {
      byte[] bytes = new byte[is.available()];
      is.read(bytes);
      // System.err.println("Received " + bytes.length + " bytes.");
      pushInput(bytes);
    }
  }

  public <T> T cast(JSONObject response) throws JSONException {
    List<String> primitives = new ArrayList<String>();
    primitives.add("boolean");
    primitives.add("number");
    primitives.add("string");

    JSONObject result = response.optJSONObject("result");
    if (result != null) {
      String type = result.getString("type");


      if (primitives.contains(type)) { // primitive type.
        Object value = result.get("value");
        return (T) value;
      } else if ("object".equals(type)) { // object
        if ("array".equals(result.optString("subtype"))) {
          RemoteObject array = new RemoteObject(result, this);
          return (T) new RemoteObjectArray(array);
        } else {
          return (T) new RemoteObject(result, this);
        }

      } else {
        throw new RuntimeException("NI " + response.toString(2));
      }
    } else {
      throw new RuntimeException("bug, null result ");
    }
  }


  private int byteToInt(byte b) {
    int i = (int) b;
    return i >= 0 ? i : i + 256;
  }
}
