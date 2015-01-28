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
    COMMAND: "http://localhost:" + CONFIG_PORT + "/wd/hub/uiascriptproxy?sessionId="
        + CONFIG_SESSION,
    LOG: "http://localhost:" + CONFIG_PORT + "/wd/hub/log?sessionId=" + CONFIG_SESSION,
    HOST: UIATarget.localTarget().host(),
    TIMEOUT_IN_SEC: {
        "implicit": 0
    },
    SESSION: CONFIG_SESSION,
    AUT: CONFIG_AUT,
    COMMUNICATION_MODE: CONFIG_MODE,
    ACCEPT_SSL_CERTS: ACCEPT_SSL_CERTS,
    CAPABILITIES: -1,

    /**
     * Create a webdriver response for ios-driver. The response won't be sent to the client directly,
     * but to the ios-server first, and then potentially forwarded, and modified.
     * @param {string} sessionId the session currently controlling instruments.
     * @param {number} status the response status. 0 for ok.
     * @param {Object} value the response value
     * @param {bool} opt_skipStale An optional flag indicating whether the cache should skip its check for stale
     *               elements when it's consulted in preparing the response.
     * @return {string} the response value, stringified.
     */
    createJSONResponse: function (sessionId, status, value, opt_skipStale) {
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
                // check if the element is in an alert to throw the unexpected alert exception if needed
                try {
                    var skipStale = false;
                    if (opt_skipStale === true) {
                        skipStale = true;
                    }
                    this.cache.get(value.reference(), !skipStale);
                    res.ELEMENT = "" + value.reference();
                    res.type = value.type();
                } catch (err) {
                    res = err;
                    result.status = err.status || 13;
                }

            } else {
                res = value;
            }
            result.value = res;
        } catch (err) {
            result.status = 13;
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
    postResponseWithCURLAndGetNextCommand: function (jsonResponse) {
        var nextCommand;
        var maxPartLength = 200000;
        if (jsonResponse.length > maxPartLength){
            var sessionId = "sessionId";
            var partialIdentifier = "b94b2b80-4330-11e4-916c-0800200c9a66-PARTIAL";
            var endIdentifier = "b94b2b80-4330-11e4-916c-0800200c9a66-FINAL"
            var uuidLength = 36;
            var jsonPart;
            var index = 0;
            var jsonRemainder = jsonResponse;
            var startPos = jsonResponse.indexOf(sessionId) + sessionId.length + 3;
            var sessionUUID = jsonResponse.substring(startPos, startPos + uuidLength);

            while (jsonRemainder.length > maxPartLength){
                jsonPart =  sessionUUID + jsonRemainder.substring(0,maxPartLength) + partialIdentifier;
                jsonRemainder = jsonRemainder.substring(maxPartLength, Number.MAX_VALUE);
                log("posting response : " + jsonPart);
                nextCommand = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND,
                                                                                        "--data-binary",
                                                                                        jsonPart],
                                                                                        600);
            }
            jsonRemainder = sessionUUID + jsonRemainder + endIdentifier
            log("posting response : " + jsonRemainder);
            nextCommand = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND,
                                                                                    "--data-binary",
                                                                                    jsonRemainder],
                                                                                    600);
        }else{
            log("posting response : " + jsonResponse);
            nextCommand = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND,
                                                                                    "--data-binary",
                                                                                    jsonResponse],
                                                                                    600);
        }

        if (nextCommand.exitCode != 0) {
            throw new UIAutomationException("error getting new command. exit code : " + result
                .exitCode);
        }
        log("command : " + nextCommand.stdout);
        return nextCommand.stdout;

    },

    /**
     * Post the response from the previous call, and wait for the next command, up to 10 minutes.
     * @param {string} jsonResponse the response to send to ios-server.
     * @return {string} the next command.The command is a javascript snipet, that will be executed
     * using eval().
     */
    pushLogToServer: function (log) {
        try {
            this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.LOG, "--data-binary",
                                                                      log], 10);
        } catch (err) {
            // TODO freynaud : put failed logs in a queue ?
            // a call to perform can fail if the log is from the alert handling method for instance.
            // in that case, the postResponseWithCURLAndGetNextCommand is still hanging, and perform
            // won't be called twice.
        }
    },

    postResponseMultiAndGetNextCommand: function (jsonResponse) {
        response(jsonResponse);
        while (true) {
            UIATarget.localTarget().delay(0.05);
            var cmd = UIATarget.localTarget().frontMostApp().preferencesValueForKey('cmd');
            if (cmd) {
                UIATarget.localTarget().frontMostApp().setPreferencesValueForKey(null, 'cmd');
                return cmd;
            }
        }
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
        result.aut = this.AUT;
        result.rect = target.rect();
        return result;
    },

    /**
     * Return the capabilities of the session. Hack inside to have the dimension of the target.
     * Capabilities should be immutable, and dimensions shouldn't be stored here.
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
     * return false = discard the alert automatically
     * return true = keep the alert opened
     */
    setAlertHandler: function () {
        var ignoreAllAlerts = false;
        var acceptAllSSL = this.ACCEPT_SSL_CERTS;
        UIATarget.onAlert = function onAlert(alert) {
            if (alert.isSSLWarning()) {
                return !acceptAllSSL;
            }
            if (alert.isAlternateKeyboard()){
                return false;
            }
            return !ignoreAllAlerts;
        }
    },

    /**
     * Keep executing commands and sending the responses until the stop command is received.
     */
    commandLoop: function () {
        // first command after registration sends the capabilities.
        var init = {};
        if (this.COMMUNICATION_MODE === "MULTI") {
            UIATarget.localTarget().frontMostApp().setPreferencesValueForKey(null, 'cmd');
        }
        init.firstResponse = UIAutomation.getCapabilities();
        var response = this.createJSONResponse(this.SESSION, 0, init);
        var ok = true;
        while (ok) {
            try {
                var request;
                if (this.COMMUNICATION_MODE === "CURL") {
                    request = this.postResponseWithCURLAndGetNextCommand(response);
                } else {
                    request = this.postResponseMultiAndGetNextCommand(response);
                }
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
