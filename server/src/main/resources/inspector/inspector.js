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

    var layout = $('#content').layout({
                                          center__size: .50,
                                          center__childOptions: {}
                                      });

    layout.sizePane("south", 200);
    var topLayout = layout.center.children.layout1;
    topLayout.sizePane("west", 450);

    // #content to resize to size - header - footer

    //$("#tabs").tabs();

    $(window).resize(function () {

        var h = $(window).height() - $("#header").height() - $("#footer").height();
        console.log("resized " + h);
        $("#content").height(h);
    });

});


