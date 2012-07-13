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
	this.lastReference = 0;

	this.store = function(element) {
		this.lastReference++;
		element.id = this.lastReference;
		this.storage[this.lastReference] = element;
		return element.id;
	};

	this.get = function(reference) {
		var res = this.storage[reference];
		if (!res) {
			throw new UIAutomationException("can't find " + reference
					+ " in cache.");
			// on arrays, stale doesn't make sense.
		}else if (res.isStale && res.isStale()){
			throw new UIAutomationException("elements ref:" + reference
					+ " is stale",10);
		}else {
			return res;
		}
		
	};

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
	COMMAND : "http://localhost:$PORT/wd/hub/uiascriptproxy",
	HOST : UIATarget.localTarget().host(),

	register : function() {
		log("registering to " + this.REGISTER);
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [
				this.REGISTER, "-d", "params" ], 90);
		if (result.exitCode != 0) {
			throw new UIAutomationException("error registering. exit code : "
					+ result.exitCode);
		}
	},

	getNextCommand : function() {
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL,
				[ this.COMMAND ], 3600);
		if (result.exitCode != 0) {
			throw new UIAutomationException(
					"error getting new command. exit code : " + result.exitCode);
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
			if (value && value.type && (value.type() === "UIAElementArray")) {
				res.ref = value.reference();
				res.type = value.type();
				res.length = value.length;
			} else if (value && value.type) {
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
		var result = this.HOST.performTaskWithPathArgumentsTimeout(this.CURL, [
				this.COMMAND, "--data-binary", jsonResponse ], 90);
		if (result.exitCode != 0) {
			throw new UIAutomationException(
					"error sending response. exit code : " + result.exitCode);
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
		result.bundleid = app.bundleID();
		result.device = target.model();
		result.name = target.name();
		result.systemName = target.systemName();
		result.sdkVersion = target.systemVersion();
		result.aut = "$AUT";
		result.rect = target.rect();
		return JSON.stringify(result);
	},

	commandLoop : function() {
		while (true) {
			try {
				var request = this.getNextCommand();
				if (request === "stop") {
					var response = this
							.createJSONResponse("sessionId", 0, "ok");
					this.sendResponse(response);
					return;
				} else {
					var response = "unknown state.";
					try {
						response = eval(request);
					} catch (err) {
						log("err : " + JSON.stringify(err));
						response = this.createJSONResponse("sessionId",
								err.status, err);
					}
					this.sendResponse(response);
				}
			} catch (err) {
				var response = this.createJSONResponse("sessionId", 13, err);
				this.sendResponse(response);
			}
		}
	}
};
