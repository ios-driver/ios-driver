/**
 * global variable.
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
    getCapabilities: function () {
        if (this.CAPABILITIES === -1) {
            this.CAPABILITIES = this.loadCapabilities();
        }
        var result = this.CAPABILITIES;
        var target = UIATarget.localTarget();
        result.rect = target.rect();
        return result;
    },
    setTimeout: function (type, timeoutInSeconds) {
        this.TIMEOUT_IN_SEC[type] = timeoutInSeconds;
    },
    getTimeout: function (type) {
        return this.TIMEOUT_IN_SEC[type];
    },
    setAlertHandler: function () {
        UIATarget.onAlert = function onAlert(alert) {
            UIAutomation.cache.setAlert(alert);
            return true;
        }
    },
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
