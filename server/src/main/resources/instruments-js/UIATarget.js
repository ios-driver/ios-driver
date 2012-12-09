/**
 * @module ios-driver
 */


"use strict";

/**
 * en , fr ...
 * @return {string}   the 2 letters code for the current language of the device.
 */
UIATarget.prototype.language = function () {
    var app = UIATarget.localTarget().frontMostApp();
    return app.preferencesValueForKey("AppleLanguages")[0];
}

/**
 * en_GB
 * @return {string} the locale of the device.
 */
UIATarget.prototype.locale = function () {
    var target = UIATarget.localTarget();
    var app = target.frontMostApp();

    // en_GB
    return app.preferencesValueForKey("AppleLocale");
}