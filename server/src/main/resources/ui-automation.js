

var cache = new Array();
var index = 0;
nextKey = function() {
	index++;
	return index.toString();
}
store = function(object) {
	var key = getKeyFromCache(object);
	if (key === -1) {
		key = nextKey();
	}
	cache[key] = object;
	// logCache();
	return key;
}
function logCache() {
	var str = "";
	for ( var key in cache) {
		str += ", cache[" + key + "]=" + cache[key];
	}
	log("cache : " + str);
}

/**
 * checks if an object is cached already. if it is, returns its chache
 * reference, otherwise returns -1;
 */
function getKeyFromCache(object) {
	/*
	 * var wrapped = new UIAElementWrapper(object);
	 * 
	 * for(var key in cache) { if(wrapped.equals(cache[key])) { return key; } }
	 */
	return -1;
}

function Logs() {
	this.logs = new Array();

	this.add = function(logEntry) {
		this.logs.push(logEntry);
		// UIALogger.logMessage("added : "+JSON.stringify(logEntry));
	}
}

var logs = new Logs();

function LogEntry(duration) {

	this.duration = duration;
	this.start = new Date().getTime();

	this.log = function(lvl, msg) {
		this.lvl = lvl;
		this.msg = msg;
		this.time = new Date().getTime();
		if (this.duration) {
			this.total = this.time - this.start;
		}

		logs.add(this);
	}
}



function UIAElementWrapper(element) {
	this.element = element;
	this.reference = -1;

	this.isUIAutomationClass = function() {
		var regex = /\[object UIA.*\]/;
		var found = (this.element.toString().search(regex) === 0);
		return found;
	}

	this.getUIClass = function() {
		if (this.isUIAutomationClass()) {
			return this.element.toString().replace('[object ', '').replace(']',
					'');
		} else {
			throw new UIAException("element " + this.element
					+ " isn't a UIA automation element");
		}
	}

	this.toString = function() {
		try {
			var name = this.element.name();
			var value = this.element.value();
			// label() undefined in xcode-instrument 4.2 but working in 4.3 ?
			var label = this.element.label();
			return 'type:' + this.getUIClass() + ",name:" + name + ",value:"
					+ value + ",label," + label;
		} catch (err) {
			return "error : " + err + this.element;
		}

	}

	this.getReferenceInCache = function() {
		if (this.reference == -1) {
			this.reference = store(this.element);
		}
		return this.reference;
	}

	this.simpleObject = function() {
		try {
			var name = this.element.name();
			var value = this.element.value();
			// label() undefined in xcode-instrument 4.2 but working in 4.3 ?
			var label = this.element.label();
			return {
				"type" : this.getUIClass(),
				"name" : name,
				"value" : value,
				"label" : label,
				"rect" : this.element.rect(),
				"reference" : this.getReferenceInCache()
			};
		} catch (err) {
			return "error : " + err + this.element;
		}

	}
	/**
	 * the native elements returned by UIAutomation cannot be compared.
	 * app.mainWindow() === app.windows()[0] returns false, even when
	 * windows().length = 1. Comparing all their attibute to get equality.
	 * Should work, 2 visible elements that are at the same place should be the
	 * same elements.
	 */
	this.equals = function(object) {
		var toCompare = [/* "isVisible", "label", */"name", /*
															 * "value",
															 * "isValid",
															 * "isEnabled"
															 */];
		if (object) {
			try {
				var other = new UIAElementWrapper(object);
				// special case : UIATarget. singleton ?.Doesn't have the
				// properties everything else has.
				if (other.getUIClass() === "UIATarget") {
					var eq = this.getUIClass() === "UIATarget"
					return eq;
				}

				if (other.getUIClass() !== this.getUIClass()) {

				}

				// compare all properties
				for ( var i in toCompare) {
					var methodName = toCompare[i];
					if (object[methodName]() !== this.element[methodName]()) {
						return false;
					}
				}

				// compare the coordinates
				var or = object.rect();
				var x = or.origin.x;
				var y = or.origin.y;
				var h = or.size.height;
				var w = or.size.width;

				var tr = this.element.rect();
				var x2 = tr.origin.x;
				var y2 = tr.origin.y;
				var h2 = tr.size.height;
				var w2 = tr.size.width;

				if ((x !== x2) || (y !== y2) || (h !== h2) || (w !== w2)) {
					return false;
				}

				return true;
			} catch (err) {
				return false;
			}

		} else {
			return false;
		}
	}
}

function getCapabilities() {
	var result = new Object();
	var target = UIATarget.localTarget();
	var app = target.frontMostApp();

	// en , fr ...
	result.language = app.preferencesValueForKey("AppleLanguages")[0];
	// en_GB
	result.locale = app.preferencesValueForKey("AppleLocale");
	result.version = app.version();
	result.bundleid = app.bundleID();
	result.model = target.model();
	result.name = target.name();
	result.systemName = target.systemName();
	result.systemVersion = target.systemVersion();
	result.rect = target.rect();
	return JSON.stringify(result);
}

function UIAException(msg) {
	this.msg = msg;

	this.toString = function() {
		return this.msg;
	}
}



function log(msg) {
	UIALogger.logMessage("log:" + msg);
}

function TreeLogger(from) {
	var target = UIATarget.localTarget();
	target.setTimeout(0);

	if (from) {
		this.origin = from;
	} else {
		this.origin = UIATarget.localTarget().frontMostApp().mainWindow();
	}

	this.root = [];

	this._loadTree = function(uiaElement) {
		this.root = this._buildNode(uiaElement);
	}

	this._buildNode = function(uiaElement) {

		var node = new Node(uiaElement);
		var uiaChildren = uiaElement.elements();

		var size = uiaChildren.length;
		for ( var i = 0; i < size; i++) {
			var child = this._buildNode(uiaChildren[i]);
			node.addChild(child);
		}
		return node;
	}

	this._loadTree(this.origin);

	this.log = function(path) {
		var entry = new LogEntry(true);
		target.captureScreenWithName('current');
		var result = {
			"path" : path,
			"tree" : this.root.tree(this.root)
		}
		var app = target.frontMostApp();
		// en , fr ...
		result.language = app.preferencesValueForKey("AppleLanguages")[0];
		// en_GB
		result.locale = app.preferencesValueForKey("AppleLocale");

		// var json = JSON.stringify(result);
		var json = result;
		entry.log('DEBUG', 'loaded tree new logger');
		return json;
	}

	this.stringify = function() {
		return this.root.stringify(this.root);
	}
}

function Node(element) {
	this.element = element;
	this.children = new Array();

	this.addChild = function(node) {
		if (node instanceof Node) {
			this.children.push(node);
		} else {
			throw new UIAException(
					"can only add elements of type Node in the tree. Invalid :"
							+ (node.constructor));
		}
	}

	this.toString = function() {
		return "node:" + this.element + " , children:" + this.children.length;
	}

	this.tree = function(from) {
		var json = new UIAElementWrapper(from.element).simpleObject();

		var length = from.children.length;
		if (length != 0) {
			json.children = new Array();
			for ( var i = 0; i < from.children.length; i++) {
				var child = from.children[i];
				json.children.push(this.tree(child));
			}
		}
		return json;
	}
	this.stringify = function(from) {
		return JSON.stringify(this.tree(from));
	}
}

/**
 * load the complete element hierarchy srarting from the element passed as
 * param.
 * 
 * @param from
 *            the UIAelement from which to load the tree.
 * @returns {TreeParser}
 */
function TreeParser(from) {
	this.origin = from;
	this.all = new Array();

	/**
	 * returns an array of all the native UIAElements.
	 */
	this.elements = function() {
		return this.all;
	}

	this._loadAll = function(from) {

		this.all.push(from);
		var children = from.elements();
		var size = children.length;
		for ( var i = 0; i < size; i++) {
			var child = children[i];
			this._loadAll(child);
		}
	}
	this._loadAll(this.origin);

}