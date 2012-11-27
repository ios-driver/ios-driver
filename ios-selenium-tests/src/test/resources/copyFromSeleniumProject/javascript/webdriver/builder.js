// Copyright 2011 Software Freedom Conservancy. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

goog.provide('webdriver.Builder');

goog.require('webdriver.AbstractBuilder');
goog.require('webdriver.FirefoxDomExecutor');
goog.require('webdriver.WebDriver');
goog.require('webdriver.http.CorsClient');
goog.require('webdriver.http.Executor');



/**
 * @constructor
 * @extends {webdriver.AbstractBuilder}
 */
webdriver.Builder = function() {
  goog.base(this);
};
goog.inherits(webdriver.Builder, webdriver.AbstractBuilder);


/**
 * @override
 */
webdriver.Builder.prototype.build = function() {
  var executor;
  if (webdriver.FirefoxDomExecutor.isAvailable()) {
    executor = new webdriver.FirefoxDomExecutor();
    return webdriver.WebDriver.createSession(executor, this.getCapabilities());
  } else {
    var client = new webdriver.http.CorsClient(this.getServerUrl());
    executor = new webdriver.http.Executor(client);

    if (this.getSession()) {
      return webdriver.WebDriver.attachToSession(executor, this.getSession());
    } else {
      throw new Error('Unable to create a new client for this browser. The ' +
          'WebDriver session ID has not been defined.');
    }
  }
};
