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

$(document).ready(function ($) {

    var version = "0.6.3";
    var downloadFile = "https://github.com/ios-driver/ios-driver/releases/download/0.6.3/ios-server-0.6.3-jar-with-dependencies.jar";
    var github = "https://github.com/ios-driver/ios-driver/";

    $("#download_me").attr('href', downloadFile);
    $("#download_me").html("Download " + version);
    $(".version").html(version);
    $("#forkme_banner").attr('href', github);

    buildFooter();

});

buildFooter = function () {
    $("#footer_wrap").html('<footer class="inner">' +
                           '<p class="copyright">ios-driver maintained by <a ' +
                           'href="https://github.com/ios-driver?tab=members">ios committers</a></p>' +
                           '<p>Published with <a href="http://pages.github.com">GitHub Pages</a></p>'
                               +
                           '</footer>');
};

buildMenu = function (page) {
    $("#menu").html('<ul>' +
                    '<li><a href="index.html" id="home">Home</a></li>' +
                    '<li><a href="setup.html" id="setup">Setup</a></li>' +
                    '<li><a href="inspector.html" id="inspector">Inspector</a></li>' +
                    '<li><a href="native.html" id="native">Native</a></li>' +
                    '<li><a href="safari.html" id="safari">Safari</a></li>' +
                    '<li><a href="hybrid.html" id="hybrid">Hybrid</a></li>' +
                    '<li><a href="scale.html" id="scale">Scale</a></li>' +
                    '<li><a href="faq.html" id="faq">FAQ</a></li>' +
                    '<li><a href="bug.html" id="bug">Report a bug</a></li>' +
                    '</ul>');
    $("#" + page).parent().addClass("active");
};

