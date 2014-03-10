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

function Recorder(inspector) {
    this.log = new Logger(this);
    this.inspector = inspector;
    this.log.lvl = 4;
    this.index = 0;
    this.urlBase = document.URL.split("/session/")[0].replace("inspector", "wd/hub");
    var secondPart = document.URL.split("/session/")[1];
    this.session = secondPart.split("/")[0];
    this.log.info(this.session);
    this.on = document.URL.indexOf("mode=record") !== -1;
    this.javaCode = "";
}

Recorder.prototype.active = function (state) {
    this.on = state;
}
Recorder.prototype.recordClick = function (locator) {
    this.index++;
    var el = "WebElement element" + this.index + " = driver.findElement(By.xpath(\"" + locator
        + "\");"

    this.javaCode += el + "\n";
    this.javaCode += "element" + this.index + ".click();\n";

    this.log.debug(this.javaCode);
    $("#java_container").html("<pre  class='prettyprint  lang-java'> " + this.javaCode + "</pre>");
    if (prettyPrint) {
        console.log("pretty!")
        prettyPrint();
    }
    console.log("click : " + el);
}

Recorder.prototype.forwardClick = function (x, y) {
    var url = this.urlBase + "/session/" + this.session + "/tap/2";
    var payload = {"x": x, "y": y};
    this.POST(url, payload);
}

Recorder.prototype.POST = function (url, payload) {
    var me = this;
    this.log.debug(url + ", payload=" + JSON.stringify(payload));
    $.ajax({
               url: url,
               async: false,
               type: "POST",
               contentType: 'application/json;charset=UTF-8',
               data: JSON.stringify(payload)

           })
        .done(function () {
                  me.log.info("success");
              })
        .fail(function () {
                  me.log.info("error");
              });

    this.log.debug("Reloading now.");
    this.inspector.reloadData();
}