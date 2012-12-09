/**
 * @module ios-driver
 */

/**
 * Create an ios exception following the webdriver JSON protocol
 * @param message
 * @param status
 * @constructor
 */
function UIAutomationException(message, status) {
    this.message = message;
    this.status = status || 13;

    this.toString = function () {
        return this.message;
    }
}

/**
 * basic logging feature using instruments UIALogger.
 * Logging is asynchronous for now ( bug ? )
 * @param msg the message to log.
 */
function log(msg) {
    UIALogger.logMessage("log:" + msg);
}