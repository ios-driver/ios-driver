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
package org.uiautomation.ios.mobileSafari.events.inserted;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.mobileSafari.events.NodeEvent;

public class ChildNodeInserted extends NodeEvent {
  
  
 
  // {"node":{
  // "childNodeCount":1
  // ,"localName":"iframe"
  // ,"nodeId":52
  // ,"nodeValue":""
  // ,"nodeName":"IFRAME"
  // ,"children":[{"localName":"","nodeId":53,"nodeValue":"\n<\/body>\n<\/html>\n","nodeName":"","nodeType":3}]
  // ,"attributes":["src","formPage.html","marginheight","0","marginwidth","0","topmargin","0","leftmargin","0","allowtransparency","true","frameborder","0","height","600","scrolling","no","width","120","id","iframe1","name","iframe1-name"]
  // ,"nodeType":1
  // ,"contentDocument":{"childNodeCount":1,"localName":"","nodeId":54,"documentURL":"http://localhost:5721/common/resultPage.html?","nodeValue":"","nodeName":"#document","xmlVersion":"","nodeType":9}}
  // ,"parentNodeId":21
  // ,"previousNodeId":22}}

  private static final Logger log = Logger.getLogger(ChildNodeInserted.class.getName());

  public ChildNodeInserted(JSONObject message) throws JSONException {
    super(message);
  }

}
