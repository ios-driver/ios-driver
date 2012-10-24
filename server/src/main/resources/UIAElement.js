UIATarget.prototype.language = function() {
	var target = UIATarget.localTarget();
	var app = target.frontMostApp();

	// en , fr ...
	return app.preferencesValueForKey("AppleLanguages")[0];
}

UIATarget.prototype.locale = function() {
	var target = UIATarget.localTarget();
	var app = target.frontMostApp();

	// en_GB
	return app.preferencesValueForKey("AppleLocale");
}

UIAApplication.prototype.keyboard2 = function() {
	var keyboard = this.keyboard();
	log("keyboard : " + keyboard)
	if(keyboard.toString() == "[object UIAElementNil]") {
		throw new UIAutomationException("cannot find keyboard", 7);
	} else {
		return keyboard;
	}
}

UIAElementArray.prototype.type = function() {
	return "UIAElementArray";
}

UIAElementArray.prototype.isStale = function() {
	return false;
}

UIAElementArray.prototype.reference = function() {
	if(!this.id) {
		UIAutomation.cache.store(this);
	}
	return this.id;
}

UIAElementArray.prototype.elements2 = function(depth, criteria) {
	var all = this.toArray();
	var res = new Array();
	for(var i = 0; i < all.length; i++) {
		var element = all[i];
		if(element.matches(criteria)) {
			res.push(element);
		}
	}
	return new MyUIAElementArray(res);
}
// TODO freynaud convert to the new OR strategy.
UIAElementArray.prototype.element = function(depth, criteria) {
	var all = this.toArray();
	for(var i = 0; i < all.length; i++) {
		var element = all[i];
		if(element.matches(criteria)) {
			return element;
		}
	}
	throw new UIAutomationException("cannot find element for criteria :" + JSON.stringify(criteria), 7);
}
/**
 * returns the class for the element, as per
 * http://developer.apple.com/library/ios/#documentation/DeveloperTools/Reference/UIAutomationRef
 */
UIAElement.prototype.type = function() {
	return this.toString().replace('[object ', '').replace(']', '');
}

UIAElement.prototype.isInAlert = function() {
	var parent;
	if(this.parent) {
		parent = this.parent();
	}

	while(parent.type() != "UIAElementNil" && parent.type() != "UIATarget") {
		if(parent.type() == "UIAAlert") {
			return true;
		}
		parent = parent.parent();
	}

	return false;
}

UIAElement.prototype.isScrollable = function() {
	var parent;
	if(this.parent) {
		parent = this.parent();
	}

	while(parent.type() != "UIAElementNil" && parent.type() != "UIATarget") {
		if(parent.type() == "UIATableView" || parent.type() == "UIAWebView") {
			return true;
		}
		parent = parent.parent();
	}

	return false;
}

UIAElementNil.prototype.type = function() {
	return "UIAElementNil";
}
// TODO freynaud check why this is necessary. key extends elements.
UIAKey.prototype.type = UIAElement.prototype.type;

UIAElement.prototype.tap2 = function() {
	if(this.isVisible()) {
		var rect = this.rect();
		var x = rect.origin.x + (rect.size.width / 2);
		var y = rect.origin.y + (rect.size.height / 2);
		var point = {
			'x' : Math.floor(x),
			'y' : Math.floor(y)
		};
		UIATarget.localTarget().tap(point);
		if(this.isInAlert() && this.type() == "UIAButton") {
			log("tapping in something in an alert.Alert is gone.");
			UIAutomation.cache.clearAlert();
		}

	} else {
		var ex = new UIAutomationException("element is not visible", 11);
		throw ex;
	}
}

UIAAlert.prototype.defaultButton2 = function() {
	var res = this.defaultButton();
	res.tap2 = function() {
		if(this.isVisible()) {
			var rect = this.rect();
			var x = rect.origin.x + (rect.size.width / 2);
			var y = rect.origin.y + (rect.size.height / 2);
			var point = {
				'x' : Math.floor(x),
				'y' : Math.floor(y)
			};
			UIATarget.localTarget().tap(point);
			UIAutomation.cache.clearAlert();
		} else {
			var ex = new UIAutomationException("element is not visible", 11);
			throw ex;
		}
	}
	return res;
}

UIAElement.prototype.reference = function() {
	if(!this.id) {
		UIAutomation.cache.store(this);
	}
	return this.id;
}
UIAElementNil.prototype.reference = UIAElement.prototype.reference;

UIAKey.prototype.reference = UIAElement.prototype.reference;

/**
 * scrollToVisible only makes sense if the element if in a webview or a tableView.
 * It was working, and doing nothing for other elements up to ios5.1.
 * Starting from ios6, it now throws :
 * Unexpected error in -[UIAStaticText_0xdc363d0 scrollToVisible], /SourceCache/UIAutomation_Sim/UIAutomation-271/Framework/UIAElement.m line 1545, kAXErrorFailure
 * so need to check first if scrolling will do anything to avoid this exception.
 */
UIAElement.prototype.scrollToVisibleSafe = function() {
	if(this.isScrollable()) {
		this.scrollToVisible();
	}
}
/**
 * can't find a way to detect stale object. CheckIsValid doesn't do it properly.
 * Trying to scroll to the element seems like a valid approximation.
 */
UIAElement.prototype.isStale = function() {
	log(this + "checkISValid :" + this.checkIsValid());
	if(this.checkIsValid() == false) {
		log("checkISValid returns false");
		return true;
	} else {
		try {
			this.scrollToVisibleSafe();
			if(this.isVisible() == 1) {
				return false;
			} else {
				return true;
			}
		} catch (err) {
			if(err.message && err.message.indexOf('scrollToVisible cannot be used') != -1) {
				return true;
			}
		}
	}
	return false;
}

UIAKey.prototype.scrollToVisible = function() {
};

UIAKeyboard.prototype.scrollToVisible = function() {
};

UIAElement.prototype.element_or = function(depth, criteria) {
	var all = this.getChildren(depth);
	var res = new Array();

	for(var i = 0; i < all.length; i++) {
		var element = all[i];
		if(element.matches(criteria)) {
			return element;
		}
	}
	throw new UIAutomationException("cannot find element for criteria :" + JSON.stringify(criteria), 7);
}

UIAElement.prototype.element = function(depth, criteria) {

	if( UIAutomation.getTimeout('implicit') == 0) {
		return this._element(depth, criteria);
	} else {
		var timeout = UIAutomation.getTimeout('implicit');
		var limit = new Date().getTime() + (1000 * timeout);

		while(new Date().getTime() < limit) {
			try {
				var res = this._element(depth, criteria);
				return res;
			} catch (err) {
				// nothing found.
				UIATarget.localTarget().delay(1);
			}
		}
		throw new UIAutomationException("Timeout after " + timeout + " seconds.Cannot find element for criteria :" + JSON.stringify(criteria), 7);

	}

}

UIAElement.prototype._element = function(depth, criteria) {
	// if a criteria contains a OR, first run the search on the whole tree on
	// each criteria in the OR so that A or B returns A, and B or A returns B.
	var keys = getKeys(criteria);

	if(keys.length == 1 && keys[0] === "OR") {
		var array = criteria[keys[0]];
		for(var c in array) {
			var orCriteria = array[c];
			try {
				return this.element(depth, orCriteria);
			} catch (err) {
				// nothing found
			}
		}
		throw new UIAutomationException("cannot find element for criteria :" + JSON.stringify(criteria), 7);
	} else {
		return this.element_or(depth, criteria);
	}
}

UIAElement.prototype.elements2 = function(depth, criteria) {
	var all = this.getChildren(depth);
	var res = new Array();
	if(criteria) {
		for(var i = 0; i < all.length; i++) {
			var element = all[i];
			if(element.matches(criteria)) {
				res.push(element);
			}
		}
		return new MyUIAElementArray(res);
	} else {
		return new MyUIAElementArray(all);
	}
}

UIAElement.prototype.asNode = function() {
	try {
		return {
			"type" : this.type(),
			"name" : this.name(),
			"value" : this.value(),
			"label" : this.label(),
			"rect" : this.rect(),
			"ref" : this.reference()
		};
	} catch (err) {
		return "error : " + err;
	}
}

UIAElement.prototype.tree = function(attachScreenshot) {
	var buildNode = function(element) {
		var res = element.asNode();
		var children = element.elements();
		if(children.length > 0) {
			var array = new Array();
			for(var i = 0; i < children.length; i++) {
				array.push(buildNode(children[i]));
			}
			res.children = array;
		}
		return res;
	}
	var result = {};

	if(attachScreenshot) {
		UIATarget.localTarget().captureScreenWithName('tmpScreenshot');
		result.screenshot = true;
	} else {
		result.screenshot = false;
	}

	var res = buildNode(this);
	result.tree = res;
	// TODO freynaud why is that broken ?
	// result.deviceOrientation = UIATarget.localTarget().deviceOrientation();
	result.deviceOrientation = UIATarget.localTarget().frontMostApp().interfaceOrientation()

	return result;
}
/**
 * return an array with all the children of the element. Goes depth deep in the
 * tree. depth = -1 or undefined gets all elements.
 */
UIAElement.prototype.getChildren = function(depth) {
	var res = new Array();

	if(!depth) {
		depth = -1;
	}
	var getChildren = function(depth, element) {
		res.push(element);
		var d = depth - 1;
		if(d > 0 || d !== -1) {
			var children = element.elements();
			var size = children.length;
			for(var i = 0; i < size; i++) {
				var child = children[i];
				getChildren(d, child);
			}
		}
	}
	getChildren(depth, this)
	return res;
}
var getKeys = function(obj) {
	var keys = [];
	for(var key in obj) {
		keys.push(key);
	}
	return keys;
}

UIAElement.prototype.matches = function(criteria) {
	if(!criteria) {
		return true;
	}
	// criteria only have one field.It's either :
	// AND + array of criterias
	// OR + array of criterias
	// NOT + criteria
	// criteria
	var keys = getKeys(criteria);

	// empty filder matches all.
	if(keys == 0) {
		return true;
	}

	// AND, OR , NOT + array of criteria to apply
	if(keys.length == 1) {
		var key = keys[0];
		if(key === "AND") {
			var array = criteria[key];
			for(var c in array) {
				if(!this.matches(array[c])) {
					return false;
				}
			}
			return true;
		} else if(key === "OR") {
			var array = criteria[key];
			for(var c in array) {
				if(this.matches(array[c])) {
					return true;
				}
			}
			return false;
		} else if(key === "NOT") {
			return !this.matches(criteria[key][0]);
		} else {
			throw new UIAutomationException("not a valid criteria, -> " + JSON.stringify(criteria), 32);
		}
	}
	// property match
	else if(keys.length == 4) {
		var method = criteria['method'];
		var expected = criteria['expected'];
		var strategy = criteria['matching'];
		var current = this[method]();

		switch (strategy) {
			case 'exact':
				return current == expected;
			case 'regex':
				var regex = new RegExp(expected, 'g');
				return regex.test(current);
				break;
			default:
				throw new UIAutomationException("not a valid strategy, -> " + JSON.stringify(criteria), 32);
		}

	} else {
		throw new UIAutomationException("not a valid criteria, -> " + JSON.stringify(criteria), 32);
	}
}
/**
 * create something similar to UIAelement array from a list of elements
 *
 * @param elements
 *            an array of native UIAElements.
 */
function MyUIAElementArray(elements) {
	this.elements = elements;
	this.length = elements.length;

	this.toArray = function() {
		return this.elements;
	}

	this.type = function() {
		return "UIAElementArray";
	}
	this.toString = function() {
		return "[object MyUIAElementArray]";
	}

	this.firstWithName = function(name) {
		for( i = 0; i < this.elements.length; i++) {
			var current = elements[i];
			log("element with name : " + current.name());
			if(current.name() === name) {
				return current;
			}
		}
		throw new UIAutomationException("cannot find an element with name=" + name);
	}

	this.buttons = function() {
		return this._getElementsOfType("UIAButton");
	}
	this.links = function() {
		return this._getElementsOfType("UIALink");
	}

	this.images = function() {
		return this._getElementsOfType("UIAImage");

	}

	this._getElementsOfType = function(type) {
		if(type) {
			var res = new Array();
			for( i = 0; i < this.all.length; i++) {
				var current = this.all[i];
				if(current.type() === type) {
					res.push(current);
				}
			}
			return res;
		} else {
			throw new UIAutomationException("param type missing");
		}

	}

	this.reference = function() {
		if(!this.id) {
			UIAutomation.cache.store(this);
		}
		return this.id;
	}

	this.isStale = UIAElementArray.prototype.isStale;
	this.element = UIAElementArray.prototype.element;
	this.elements2 = UIAElementArray.prototype.elements2;

}