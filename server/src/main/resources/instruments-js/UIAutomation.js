/**
 * @module ios-driver
 */

/**
 * @constructor
 * @type {{cache: Cache, CURL: string, COMMAND: string, HOST: String, TIMEOUT_IN_SEC:
 * {implicit: number}, SESSION: string, CAPABILITIES: number, createJSONResponse: Function,
 * postResponseAndGetNextCommand: Function, loadCapabilities: Function, getCapabilities:
 * Function, setTimeout: Function, getTimeout: Function, setAlertHandler:
 * Function, commandLoop: Function}}
 */
var UIAutomation = {
    cache: new Cache(),
    CURL: "/usr/bin/curl",
    COMMAND: "http://localhost:$PORT/wd/hub/uiascriptproxy?sessionId=$SESSION",
    HOST: UIATarget.localTarget().host(),
    TIMEOUT_IN_SEC: {
        "implicit": 0
    },
    SESSION: "$SESSION",
    CAPABILITIES: -1,

    /**
     * Create a webdriver response for ios-driver. The response won't be sent to the client directly,
     * but to the ios-server first, and then potentially forwarded, and modified.
     * @param {string} sessionId the session currently controlling instruments.
     * @param {number} status the reponse status. 0 for ok.
     * @param {Object} value the response value
     * @return {string} the response value, stringified.
     */
    createJSONResponse: function (sessionId, status, value) {
        var result = {};
        result.sessionId = sessionId;
        result.status = status;
        var res = {};
        try {
            if (value && value.type && (value.type() === "UIAElementArray")) {
                var all = new Array();
                value = value.toArray();
                for (var i = 0; i < value.length; i++) {
                    var current = value[i];
                    var item = {};
                    item.ELEMENT = "" + current.reference();
                    item.type = current.type();
                    all.push(item);
                }
                res = all;
            } else if (value instanceof Array) {
                var all = new Array();
                for (var i = 0; i < value.length; i++) {
                    var current = value[i];
                    var item = {};

                    if (current.type) {
                        item.ELEMENT = "" + current.reference();
                        item.type = current.type();
                    } else {
                        item = current;
                    }

                    all.push(item);
                }
                res = all;
            } else if (value && value.type) {
                res.ELEMENT = "" + value.reference();
                res.type = value.type();
            } else {
                res = value;
            }
            result.value = res;
        } catch (err) {
            result.value = err;
        }

        var json = JSON.stringify(result);
        return json;
    },

    /**
     * Post the response from the previous call, and wait for the next command, up to 10 minutes.
     * @param {string} jsonResponse the response to send to ios-server.
     * @return {string} the next command.The command is a javascript snipet, that will be executed
     * using eval().
     */
    postResponseAndGetNextCommand: function (jsonResponse) {
        log("posting response : " + jsonResponse);
        var nextCommand = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND,
                                                                                    "--data-binary",
                                                                                    jsonResponse],
                                                                        600);
        if (nextCommand.exitCode != 0) {
            throw new UIAutomationException("error getting new command. exit code : " + result
                .exitCode);
        }
        log("command : " + nextCommand.stdout);
        return nextCommand.stdout;

    },
    /**
     * Initialises the capabilities for this session. Capabilities should be immutable.
     * @return {Object} the capabilities for the current session. Part of the capabilities a client
     * wants aren't accessible from instruments, and will be added by the ios-server.
     */
    loadCapabilities: function () {
        var result = new Object();
        var target = UIATarget.localTarget();
        var app = target.frontMostApp();

        // en , fr ...
        result.language = app.preferencesValueForKey("AppleLanguages")[0];
        // en_GB
        result.locale = app.preferencesValueForKey("AppleLocale");
        result.version = app.version();
        result.CFBundleIdentifier = app.bundleID();
        result.CFBundleVersion = app.bundleVersion();
        result.device = target.model();
        result.name = target.name();
        result.systemName = target.systemName();
        result.sdkVersion = target.systemVersion();
        result.aut = "$AUT";
        result.rect = target.rect();
        return result;
    },

    /**
     * Return the capabilities of the session. Hack inside to have the dimension of the target.
     * Capabilities should be immutable, and dimensions shoudln't be stored here.
     * @return {Object} the capabilities of the session.
     */
    getCapabilities: function () {
        if (this.CAPABILITIES === -1) {
            this.CAPABILITIES = this.loadCapabilities();
        }
        var result = this.CAPABILITIES;
        var target = UIATarget.localTarget();
        result.rect = target.rect();
        return result;
    },

    /**
     * set one of the timeout for the session
     * @param {string} type of the timout to set.
     * @param {number} timeoutInSeconds  in seconds.
     */
    setTimeout: function (type, timeoutInSeconds) {
        this.TIMEOUT_IN_SEC[type] = timeoutInSeconds;
    },

    /**
     * returns the timeout in seconds for the specified type.
     * @param {string} type
     * @return {number} the timeout for that type.
     */
    getTimeout: function (type) {
        return this.TIMEOUT_IN_SEC[type];
    },

    /**
     * wires the alert handler. If the handler isn't attached, all alerts are ignored, and clicked
     * away.
     */
    setAlertHandler: function () {
        UIATarget.onAlert = function onAlert(alert) {
            UIAutomation.cache.setAlert(alert);
            return true;
        }
    },

    /**
     * keep executing commands and sending the responses untils the stop command is received.
     */
    commandLoop: function () {
        // first command after registration sends the capabilities.
        var init = {};
        init.firstResponse = UIAutomation.getCapabilities();
        var response = this.createJSONResponse(this.SESSION, 0, init);
        var ok = true;
        while (ok) {
            try {
                var request = this.postResponseAndGetNextCommand(response);
                if (request === "stop") {
                    ok = false;
                    log("end of the command loop.");

                    return;
                } else {
                    try {
                        response = eval(request);
                    } catch (err) {
                        if (err.status) {
                            response =
                            this.createJSONResponse(this.SESSION, err.status, err.message);
                        } else {
                            response = this.createJSONResponse(this.SESSION, 17, err.message);
                        }

                    }
                }
            } catch (err) {
                var response = this.createJSONResponse(this.SESSION, 13, err);
                log("err2 : " + JSON.stringify(err));
                return;
            }
        }
    }
};
