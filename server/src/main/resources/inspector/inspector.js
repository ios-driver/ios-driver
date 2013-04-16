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

$(document).ready(function () {

    $(function () {
        $(".resizeable-se").resizable({ handles: "e,s" });
    });

    $(function () {
        $(".resizeable-s").resizable({ handles: "s" });
    });

    var table = new InspectorTable();
    table.setLogHeight(150);

    var onResize = function (event, ui) {
        var height = ui.size.height;
        table.setTopHeight(height);
    }

    $(".resizeable-se").resize(onResize);
    $(".resizeable-s").resize(onResize);

    $(window).resize(function () {
        table.maximize();

    });

    $("#tabs").tabs({ heightStyle: "fill" });

});

InspectorTable = function () {
    this.maximize();
}

InspectorTable.prototype.setLogHeight = function (height) {
    this.setTopHeight(this.height - height);
}

InspectorTable.prototype.setTopHeight = function (height) {
    $("#logs").height(this.height - height);
    $("#logs2").height(this.height - height);
    $("#top").height(height);
    $("#top td").height(height);
    try {
        $("#tabs").tabs("refresh");
    } catch (ignore) {

    }

}

InspectorTable.prototype.maximize = function () {
    var header = $("#header").height();
    var footer = $("#footer").height();
    var total = $(window).height();
    this.setHeight(total - header - footer);

}
InspectorTable.prototype.setHeight = function (height) {
    this.height = height;
    var logsH = $("#logs").height();
    this.setLogHeight(logsH);

}

