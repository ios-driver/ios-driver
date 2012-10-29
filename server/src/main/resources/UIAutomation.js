function UIAutomationException(message, status) {
	this.message = message;
	this.status = status || 13;

	this.toString = function() {
		return this.message;
	}
}

function log(msg) {
	UIALogger.logMessage("log:" + msg);
}

var Cache = function() {
	this.storage = {};
	this.lastReference = 3;

	this.store = function(element) {
		this.lastReference++;
		element.id = this.lastReference;
		this.storage[this.lastReference] = element;
		return element.id;
	};

	this.get = function(reference) {
		if(reference == 0) {
			return UIATarget.localTarget().frontMostApp().mainWindow();
		} else if(reference == 1) {
			return UIATarget.localTarget().frontMostApp();
		} else if(reference == 2) {
			return UIATarget.localTarget();
		} else if(reference == 3) {
			if(this.storage[3]) {
				return this.storage[3];
			} else {
				throw new UIAutomationException("No alert opened", 27);
			}

		}

		var res = this.storage[reference];

		// there is an alert.
		if(this.storage[3]) {

			if(res.isInAlert()) {
				return res;
			} else {
				throw new UIAutomationException("cannot interact with object " + res + ". There is an alert.", 26);
			}

		} else {
			// target and app aren't stale.
			if(!res) {
				throw new UIAutomationException("can't find " + reference + " in cache.");
				// window an apps aren't stale ?
			} else if(res.type && (res.type() == "UIAWindow" || res.type() == "UIAApplication")) {
				return res;
				// on arrays, stale doesn't make sense.
			} else if(res.isStale && res.isStale()) {
				throw new UIAutomationException("elements ref:" + reference + " is stale", 10);
			} else {
				return res;
			}
		}

	};

	this.getAlert = function() {
		return this.storage[3];
	};
	this.setAlert = function(alert) {
		this.storage[3] = alert;
		log("found alert");
	};

	this.clearAlert = function() {
		log("removed alert");
		this.storage[3] = null;
	}

	this.clear = function() {
		this.storage = {};
		this.storage[0] = UIATarget.localTarget().frontMostApp().mainWindow();
	};

	this.clear();

}
/**
 * global variable.
 */
var UIAutomation = {
	cache : new Cache(),
	CURL : "/usr/bin/curl",
	REGISTER : "http://localhost:$PORT/wd/hub/uiascriptproxy/register",
	COMMAND : "http://localhost:$PORT/wd/hub/uiascriptproxy?sessionId=$SESSION",
	HOST : UIATarget.localTarget().host(),
	TIMEOUT_IN_SEC : {
		"implicit" : 0
	},
	SESSION : "$SESSION",

	register : function() {
		log("registering to " + this.REGISTER);
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.REGISTER, "-d", "sessionId=" + this.SESSION], 90);
		if(result.exitCode != 0) {
			throw new UIAutomationException("error registering. exit code : " + result.exitCode);
		}
	},
	getNextCommand : function() {
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND], 3600);
		if(result.exitCode != 0) {
			throw new UIAutomationException("error getting new command. exit code : " + result.exitCode);
		}
		log("command : " + result.stdout);
		return result.stdout;
	},
	createJSONResponse : function(sessionId, status, value) {
		var result = {};
		result.sessionId = sessionId;
		result.status = status;
		var res = {};
		try {
			if(value && value.type && (value.type() === "UIAElementArray")) {
				res.ref = value.reference();
				res.type = value.type();
				res.length = value.length;
			} else if(value && value.type) {
				res.ref = value.reference();
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
	sendResponse : function(jsonResponse) {
		log("response : " + jsonResponse);
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [this.COMMAND, "--data-binary", jsonResponse], 90);
		if(result.exitCode != 0) {
			throw new UIAutomationException("error sending response. exit code : " + result.exitCode);
		}
		return result;
	},
	getCapabilities : function() {
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
		return JSON.stringify(result);
	},
	setTimeout : function(type, timeoutInSeconds) {
		this.TIMEOUT_IN_SEC[type] = timeoutInSeconds;
	},
	getTimeout : function(type) {
		return this.TIMEOUT_IN_SEC[type];
	},
	setAlertHandler : function() {
		UIATarget.onAlert = function onAlert(alert) {
			UIAutomation.cache.setAlert(alert);
			return true;
		}
	},
	commandLoop : function() {
		while(true) {
			try {
				var request = this.getNextCommand();
				if(request === "stop") {
					var response = this.createJSONResponse(this.SESSION, 0, "ok");
					this.sendResponse(response);
					return;
				} else {
					var response = "unknown state.";
					try {
						response = eval(request);
					} catch (err) {
						log("err : " + JSON.stringify(err));
						response = this.createJSONResponse(this.SESSION, err.status, err);
					}
					this.sendResponse(response);
				}
			} catch (err) {
				var response = this.createJSONResponse(this.SESSION, 13, err);
				this.sendResponse(response);
			}
		}
	}
};
