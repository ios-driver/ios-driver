package org.uiautomation.ios.webInspector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.webInspector.DOM.DOM;
import org.uiautomation.ios.webInspector.DOM.Node;
import org.uiautomation.ios.webInspector.DOM.RemoteObject;

public class WebInspector {

  private final Socket socket;
  private ByteArrayOutputStream buf = new ByteArrayOutputStream();
  private final PlistManager plist = new PlistManager();
  private MessageHandler handler;

  private final String LOCALHOST_IPV6 = "::1";
  private final int port = 27753;

  private int commandId = 0;

  private final DOMCache cache;

  public WebInspector() throws UnknownHostException, IOException, InterruptedException {

    cache = new DOMCache();

    socket = new Socket(LOCALHOST_IPV6, port);
    sendCommand(PlistManager.SET_CONNECTION_KEY);
    sendCommand(PlistManager.CONNECT_TO_APP);
    sendCommand(PlistManager.SET_SENDER_KEY);

    setMessageHandler(new MyMessageHandler(cache));



    Thread listen = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          while (true) {
            Thread.sleep(250);
            listenOnce();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    listen.start();
  }



  public static void main(String[] args) throws Exception {
    WebInspector inspector = new WebInspector();

    // String ref = inspector.findSearchButton();
    // inspector.callFunctionOn(ref);
    // inspector.getDocument();

    JSONObject res = inspector.sendCommand(DOM.getDocument());

    DOM dom = new DOM(inspector);

    Node n = new Node(res.getJSONObject("result").getJSONObject("root"));
    Node body = n.getBody();

    RemoteObject remoteBody = dom.resolveNode(body.getId());


    JSONObject r = inspector.sendCommand(DOM.requestNode(remoteBody));
    System.err.println("node for body : " + r.toString(2));

    RemoteObject frame = inspector.findIframe();
    JSONObject r2 = inspector.sendCommand(DOM.requestNode(frame));

    System.err.println(r2.toString(2));


    for (Node iframe : inspector.cache.getIFrame()) {
      System.out.println("iframe =" + iframe.getId());
      RemoteObject document = dom.resolveNode(iframe.getContentDocument().getId());
      inspector.findElementById(document,"srchFrm");
      inspector.findElementById(document,"gh-btn");
    }



  }


  private void findElementById(RemoteObject docRef,String id) throws Exception {
    JSONObject cmd = new JSONObject();

    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", id));

    cmd.put(
        "params",
        new JSONObject()
            .put("objectId", docRef.getId())
            .put("functionDeclaration",
                "(function(arg) { var el = this.getElementById(arg);alert(el); return el;})")
            .put("arguments", args).put("returnByValue", false));



    JSONObject response = sendCommand(cmd);
    System.out.println(cmd.toString(2));
    System.out.println(response.toString(2));
  }

  private void callFunctionOn(String ref) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("id", 3);
    cmd.put("method", "Runtime.callFunctionOn");

    JSONArray args = new JSONArray();
    args.put(new JSONObject().put("value", "type"));

    cmd.put(
        "params",
        new JSONObject().put("objectId", ref)
            .put("functionDeclaration", "(function(arg) {return this.getAttribute(arg);})")
            .put("arguments", args).put("returnByValue", true));

    System.out.println(cmd.toString(2));


    JSONObject response = sendCommand(cmd);
    System.out.println(response.toString(2));

  }

  public RemoteObject findIframe() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("id", 1);
    cmd.put("method", "Runtime.evaluate");
    cmd.put(
        "params",
        new JSONObject().put("expression", "document.getElementsByTagName('iframe')[0];").put(
            "returnByValue", false));
    JSONObject response = sendCommand(cmd);
    System.out.println(response.toString(2));
    JSONObject res = response.getJSONObject("result").getJSONObject("result");

    return new RemoteObject(res);
  }

  public String findSearchButton() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("id", 1);
    cmd.put("method", "Runtime.evaluate");
    cmd.put(
        "params",
        new JSONObject().put("expression", "document.getElementById('gh-btn');").put(
            "returnByValue", false));
    JSONObject response = sendCommand(cmd);
    System.out.println(response.toString(2));
    String res = response.getJSONObject("result").getJSONObject("result").getString("objectId");

    return res;
  }

  public void getDocument() throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("id", 3);
    cmd.put("method", "DOM.getDocument");
    JSONObject response = sendCommand(cmd);
    System.out.println(response.toString(2));
  }


  public void getText(String ref) throws Exception {
    JSONObject cmd = new JSONObject();
    cmd.put("id", 2);
    cmd.put("method", "Runtime.getProperties");
    cmd.put("params", new JSONObject().put("objectId", ref).put("ownProperties", true));


    JSONObject response = sendCommand(cmd);
    System.out.println(response.toString(2));
  }

  private void setMessageHandler(MessageHandler handler) {
    this.handler = handler;
  }


  public JSONObject sendCommand(JSONObject json) throws Exception {
    commandId++;
    json.put("id", commandId);
    String xml = plist.JSONCommand(json);
    byte[] bytes = plist.plistXmlToBinary(xml);
    sendBinaryMessage(bytes);
    return handler.getResponse(json.getInt("id"));
  }

  public void sendCommand(String command) throws IOException, InterruptedException {
    String xml = plist.loadFromTemplate(command);
    byte[] bytes = plist.plistXmlToBinary(xml);
    sendBinaryMessage(bytes);
  }


  public String getMessage() {
    return null;
  }


  private void sendBinaryMessage(byte[] bytes) throws IOException {
    OutputStream os = socket.getOutputStream();
    os.write((byte) ((bytes.length >> 24) & 0xFF));
    os.write((byte) ((bytes.length >> 16) & 0xFF));
    os.write((byte) ((bytes.length >> 8) & 0xFF));
    os.write((byte) (bytes.length & 0xFF));
    os.write(bytes);
    // System.err.println("Sending " + bytes.length + " bytes.");
  }


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
        // System.err.println("Reading " + size + " + 4 bytes.");
        String message = plist.plistBinaryToXml(Arrays.copyOfRange(bytes, 4, size + 4));
        handler.handle(message);
        // System.out.println(plist.plistBinaryToXml(Arrays.copyOfRange(bytes, 4, size + 4)));
        // System.out.flush();
        buf = new ByteArrayOutputStream();
        buf.write(bytes, 4 + size, bytes.length - size - 4);
      } else {
        System.err.println("Expecting " + size + " + 4 bytes. Buffered " + bytes.length + ".");
        break;
      }
    }
  }


  private void listenOnce() throws IOException, InterruptedException {
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


}
