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
deviceResize = function (width_in_px) {
    $('#resize').css('left', width_in_px);
    $('#device').css('width', width_in_px);
    $('#internals').css('margin-left', width_in_px);
}

$(document).ready(function () {
    $("#resize").draggable({
                               axis: "x",
                               drag: function (event, ui) {

                                   var width = event.pageX;
                                   console.log("pageX" + width);
                                   deviceResize(width);
                               }});

    $('#device').resize(function () {
        var width = $(this).width();
        $('#internals').css('margin-left', width);
    });

    deviceResize(500);
});
