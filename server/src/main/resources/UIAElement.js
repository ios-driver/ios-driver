UIAApplication.prototype.keyboard2 = function() {
	var keyboard = this.keyboard();
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
	} else {
		var ex = new UIAutomationException("element is not visible", 11);
		throw ex;
	}

}

UIAElement.prototype.reference = function() {
	if(!this.id) {
		UIAutomation.cache.store(this);
	}
	return this.id;
}
UIAKey.prototype.reference = UIAElement.prototype.reference;
/**
 * can't find a way to detect stale object. CheckIsValid doesn't do it properly.
 * Trying to scroll to the element seems like a valid approximation.
 */
UIAElement.prototype.isStale = function() {
	if(this.checkIsValid() == false) {
		return true;
	} else {
		try {
			this.scrollToVisible();
			return false;
		} catch (err) {
			if(err.message && err.message.indexOf('scrollToVisible cannot be used') != -1) {
				return true;
			}
		}
	}
	return false;
}
/*UIAElement.prototype.element = function(depth, criteria) {
 var all = this.getChildren(depth);
 var res = new Array();

 for(var i = 0; i < all.length; i++) {
 var element = all[i];
 if(element.matches(criteria)) {
 return element;
 }
 }
 throw new UIAutomationException("cannot find element for criteria :" + JSON.stringify(criteria), 7);
 }*/

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
	var timeout = UIAutomation.TIMEOUT_IN_SEC;
	var limit = new Date().getTime() + (1000 * timeout);
	
	while (new Date().getTime() < limit ){
		try {
			var res = this._element(depth, criteria);
			return res;
		}catch (err){
			// nothing found.
			UIATarget.localTarget().delay(1);
		}
	}
	throw new UIAutomationException("Timeout after "+timeout+" seconds.Cannot find element for criteria :" + JSON.stringify(criteria), 7);

	
}
	
UIAElement.prototype._element = function(depth, criteria) {
	// if a criteria contains a OR, first run the search on the whole tree on each criteria in the OR so that A or B returns A, and B or A returns B.
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

UIAElement.prototype.tree = function(path) {
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
	var res = buildNode(this);
	UIATarget.localTarget().captureScreenWithName('current');
	var result = {
		tree : res,
		path : path
	};
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