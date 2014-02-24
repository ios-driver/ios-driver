/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
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

package org.uiautomation.ios.server.simulator;

import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.server.command.UIAScriptRequest;
import org.uiautomation.ios.server.instruments.communication.CommunicationChannel;
import org.uiautomation.ios.server.services.Instruments;
import org.uiautomation.ios.server.services.TakeScreenshotService;

public class InstrumentsLibImobile implements Instruments/*, MessageHandler*/ {


  @Override
  public void start(long timeOut) throws InstrumentsFailedToStartException {

  }

  @Override
  public void stop() {

  }

  @Override
  public Response executeCommand(UIAScriptRequest request) {
    return null;
  }

  @Override
  public CommunicationChannel getChannel() {
    return null;
  }

  @Override
  public TakeScreenshotService getScreenshotService() {
    return null;
  }

//  private static final Logger log = Logger.getLogger(InstrumentsLibImobile.class.getName());
//
//  private final String sessionId;
//  private final String bundleId;
//  private final String aut;
//  private final String uuid;
//  private final int port;
//  private  InstrumentsService instruments;
//  private final MultiInstrumentsBasedCommunicationChannel channel;
//
//  public InstrumentsLibImobile(String uuid, int port, String aut,
//                               String sessionId, String bundleId) {
//    this.port = port;
//    this.aut = aut;
//    this.uuid = uuid;
//    this.sessionId = sessionId;
//    this.bundleId = bundleId;
//    if (uuid != null) {
//      //this.instruments = new InstrumentsService(DeviceService.get(uuid), bundleId, this);
//      channel = new MultiInstrumentsBasedCommunicationChannel(port, aut, sessionId, instruments);
//    } else {
//      this.instruments = null;
//      throw new RuntimeException("NI");
//    }
//
//  }
//
//
//  @Override
//  public void handle(String message) {
//    //System.out.println(message);
//
//    try {
//      NSDictionary o = (NSDictionary) XMLPropertyListParser.parse(message.getBytes("UTF-8"));
//      String msg = ((NSString) o.get("Message")).getContent();
//      channel.handle(msg);
//    } catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    }
//
//  }
//
//
//  @Override
//  public void start() throws InstrumentsFailedToStartException {
//
//    String script = new ScriptHelper().generateScriptContent(port, aut, sessionId, MULTI);
//    //instruments.startApp(bundleId);
//    //System.out.println("started app");
//    instruments.executeScriptNonManaged(script);
//    //System.out.println("started script ");
//    try {
//      channel.waitForUIScriptToBeStarted();
//      //System.out.println("script said hello");
//    } catch (InterruptedException e) {
//      throw new InstrumentsFailedToStartException("Error starting script " + e.getMessage(), e);
//    }
//  }
//
//  @Override
//  public void stop() {
//    channel.stop();
//    instruments.stopApp();
//  }
//
//  @Override
//  public Response executeCommand(UIAScriptRequest request) {
//    UIAScriptResponse res = channel.executeCommand(request);
//    return res.getResponse();
//  }
//
//
//
//  @Override
//  public CommunicationChannel getChannel() {
//    return channel;
//  }
//
//  @Override
//  public TakeScreenshotService getScreenshotService() {
//    return null;  //To change body of implemented methods use File | Settings | File Templates.
//  }
}
