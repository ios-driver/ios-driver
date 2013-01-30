/**
 * @module ios-driver
 */

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

UIATarget.prototype.setDeviceOrientation_original = UIATarget.prototype.setDeviceOrientation;

/**
 * Sets the orientation of the device, and wait for the animation to finish.
 * @param orientation
 */
UIATarget.prototype.setDeviceOrientation = function (orientation) {
    this.setDeviceOrientation_original(orientation);
    var timeNeededForTheRotationAnimationToComplete = 0.8; // seconds.
    this.delay(timeNeededForTheRotationAnimationToComplete);
    var newOrientation = UIATarget.localTarget().frontMostApp().interfaceOrientation();
    if (newOrientation !== orientation) {
        throw new UIAutomationException("The orientation specified is not supported by the application."
                                            + newOrientation + " !== " + orientation);
    }
}

/**
 *
 * @return {string} the orientation, with the naming convention following the webdriver ones.
 */
UIATarget.prototype.getDeviceOrientation = function () {
    var orientation = UIATarget.localTarget().frontMostApp().interfaceOrientation();
    var map = {};
    map[1] = "PORTRAIT";
    map[2] = "UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN";
    map[3] = "LANDSCAPE";
    map[4] = "UIA_DEVICE_ORIENTATION_LANDSCAPERIGHT";
    //UIA_DEVICE_ORIENTATION_FACEUP
    //UIA_DEVICE_ORIENTATION_FACEDOWN
    return map[orientation];
}
