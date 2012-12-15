/**
 * @module ios-driver
 */

/**
 * returns the class for the element, as per http://developer.apple.com/library/ios/#documentation/DeveloperTools/Reference/UIAutomationRef
 * for instance : UIAElement , UIAKeyboard etc.
 * @return {string} element.
 */
UIAElement.prototype.type = function () {
    return this.toString().replace('[object ', '').replace(']', '');
}

UIAElement.prototype.rect_original = UIAElement.prototype.rect;
/**
 * Similar to rect(), but all dimensions rounded to integers.
 * @return {UIARect} return the position and dimension of the current element.
 */
UIAElement.prototype.rect = function () {
    var rect = this.rect_original();

    rect.origin.x = Math.floor(rect.origin.x);
    rect.origin.y = Math.floor(rect.origin.y);
    rect.size.height = Math.floor(rect.size.height);
    rect.size.width = Math.floor(rect.size.width);
    return rect;
}

/**
 * checks is the element is part of an alert.
 * @return {boolean} true is the element is a descendant of an alert.
 */
UIAElement.prototype.isInAlert = function () {
    var parent;
    if (this.parent) {
        parent = this.parent();
    }

    while (parent.type() != "UIAElementNil" && parent.type() != "UIATarget") {
        if (parent.type() == "UIAAlert") {
            return true;
        }
        parent = parent.parent();
    }

    return false;
}

/**
 * with IOS6, scrollToVisible crashes for elements that are not in a tableView or a webView.
 * As scrollToVisible is used internally by ios-driver to find if an element is stale, it is necessary
 * to workaround this bug.
 * @return {boolean} true if it is safe to call scrollToVisible on the element.
 */
UIAElement.prototype.isScrollable = function () {
    var parent;
    if (this.parent) {
        parent = this.parent();
    }

    while (parent.type() != "UIAElementNil" && parent.type() != "UIATarget") {
        if (parent.type() == "UIATableView" || parent.type() == "UIAWebView") {
            // log(this.type() + "isScollable : " + false);
            return true;
        }
        parent = parent.parent();
    }
    return false;
}

UIAElement.prototype.tap_original = UIAElement.prototype.tap;

/**
 * Tap an an element. By default tap the center of the element, unless opt_XFactor and opt_YFactor are
 * specified.
 * @param {number} opt_XFactor where in the element. 0 is left border, 1 right border
 * @param {number} opt_YFactor where in the element. 0 is top border, 1 is bottom one.
 */
UIAElement.prototype.tap = function (opt_XFactor, opt_YFactor) {
    var xFactor = 0.5;
    var yFactor = 0.5;

    if (opt_XFactor) {
        xFactor = opt_XFactor;
    }
    if (opt_YFactor) {
        yFactor = opt_YFactor;
    }
    if (this.isVisible()) {
        var rect = this.rect();
        var x = rect.origin.x + (rect.size.width * xFactor);
        var y = rect.origin.y + (rect.size.height * yFactor);
        var point = {
            'x': Math.floor(x),
            'y': Math.floor(y)
        };
        UIATarget.localTarget().tap(point);
        if (this.isInAlert() && this.type() == "UIAButton") {
            log("tapping in something in an alert.Alert is gone.");
            UIAutomation.cache.clearAlert();
        }
    } else {
        var ex = new UIAutomationException("element is not visible", 11);
        throw ex;
    }
}

/**
 * ios-driver made id. Used to store and retrieve elements from the cache, and represent the element
 * when passed around the network.
 * @return {number} the unique reference of that element. Used by the cache as id.
 */
UIAElement.prototype.reference = function () {
    if (!this.id) {
        UIAutomation.cache.store(this);
    }
    return this.id;
}

UIAElement.prototype.scrollToVisible_original = UIAElement.prototype.scrollToVisible;
/**
 * scrollToVisible only makes sense if the element if in a webview or a
 * tableView. It was working, and doing nothing for other elements up to ios5.1.
 * Starting from ios6, it now throws : Unexpected error in
 * -[UIAStaticText_0xdc363d0 scrollToVisible],
 * /SourceCache/UIAutomation_Sim/UIAutomation-271/Framework/UIAElement.m line
 * 1545, kAXErrorFailure so need to check first if scrolling will do anything to
 * avoid this exception.
 */
UIAElement.prototype.scrollToVisible = function () {
    if (this.isScrollable()) {
        this.scrollToVisible_original();
    }
}
/**
 * can't find a way to detect stale object. CheckIsValid doesn't do it properly.
 * Trying to scroll to the element seems like a valid approximation.
 */
UIAElement.prototype.isStale = function () {
    if (this.checkIsValid() == false) {
        return true;
    } else {
        try {
            this.scrollToVisible();
            // tmp fix for safari and big web views.
            if (this.type() === "UIAWebView") {
                return false;
            }
            if (this.isVisible() == 1) {
                return false;
            } else {
                return true;
            }
        } catch (err) {
            if (err.message && err.message.indexOf('scrollToVisible cannot be used') != -1) {
                return true;
            }
        }
    }
    return false;
}

UIAElement.prototype.element_or = function (depth, criteria) {
    var all = this.getChildren(depth);
    var res = new Array();

    var keys = getKeys(criteria);

    var possibleResult;
    var resultArea = 10000 * 10000;
    for (var i = 0; i < all.length; i++) {
        var element = all[i];
        if (element.matches(criteria)) {
            if (keys.length === 2) {// if location criteria, need to find the
                // smallest element containing the location
                var elementArea = element.rect().size.width * element.rect().size.height;
                // the = is critical. the 'all' collection has the elements
                // listed in order. For the given tree:
                // parent
                // - child1
                // - child2
                // - child3
                // all will be parent , child1, child2 , child3.
                // if child1 and child2 have the same area, the assumption is
                // that child1 is a container, while child2 is the element
                // the user will interact with.
                // <= instead of < garantees to return child2 and not child1
                if (elementArea <= resultArea) {
                    possibleResult = element;
                    resultArea = elementArea;
                }
            } else {
                return element;
            }

        }
    }
    if (possibleResult) {
        return possibleResult;
    }
    throw new UIAutomationException("cannot find element for criteria :" + JSON.stringify(criteria),
                                    7);
}

UIAElement.prototype.element = function (depth, criteria) {

    if (UIAutomation.getTimeout('implicit') == 0) {
        return this._element(depth, criteria);
    } else {
        var timeout = UIAutomation.getTimeout('implicit');
        var limit = new Date().getTime() + (1000 * timeout);

        while (new Date().getTime() < limit) {
            try {
                var res = this._element(depth, criteria);
                return res;
            } catch (err) {
                // nothing found.
                UIATarget.localTarget().delay(1);
            }
        }
        throw new UIAutomationException("Timeout after " + timeout
                                            + " seconds.Cannot find element for criteria :" + JSON
                                            .stringify(criteria), 7);

    }

}

UIAElement.prototype._element = function (depth, criteria) {
    // if a criteria contains a OR, first run the search on the whole tree on
    // each criteria in the OR so that A or B returns A, and B or A returns B.
    var keys = getKeys(criteria);

    if (keys.length == 1 && keys[0] === "OR") {
        var array = criteria[keys[0]];
        for (var c in array) {
            var orCriteria = array[c];
            try {
                return this.element(depth, orCriteria);
            } catch (err) {
                // nothing found
            }
        }
        throw new UIAutomationException("cannot find element for criteria :" + JSON
            .stringify(criteria), 7);
    } else {
        return this.element_or(depth, criteria);
    }
}

UIAElement.prototype.elements2 = function (depth, criteria) {
    var all = this.getChildren(depth);
    var res = new Array();
    if (criteria) {
        for (var i = 0; i < all.length; i++) {
            var element = all[i];
            if (element.matches(criteria)) {
                res.push(element);
            }
        }
        return res;
    } else {
        return all;
    }
}

/**
 * Return the ios-driver object associated with the element. Contains all the info used to debug, and
 * accessible by calling the logElementTree command.
 * @return {Object} an ios-driver node representing the UIAElement.
 */
UIAElement.prototype.asNode = function () {
    try {
        return {
            "type": this.type(),
            "name": this.name(),
            "value": this.value(),
            "label": this.label(),
            "rect": this.rect(),
            "ref": this.reference()
        };
    } catch (err) {
        return "error : " + err;
    }
}

/**
 * returns a dump of the application object tree, optionally taking a screenshot.
 * @param {boolean} attachScreenshot  if true, a screenshot will be taken.
 * @return {object}
 */
UIAElement.prototype.tree = function (attachScreenshot) {
    var buildNode = function (element) {
        var res = element.asNode();
        var children = element.elements();
        if (children.length > 0) {
            var array = new Array();
            for (var i = 0; i < children.length; i++) {
                array.push(buildNode(children[i]));
            }
            res.children = array;
        }
        return res;
    }
    var result = {};

    if (attachScreenshot) {
        UIATarget.localTarget().captureScreenWithName('tmpScreenshot');
        result.screenshot = true;
    } else {
        result.screenshot = false;
    }

    var res = buildNode(this);
    result.tree = res;
    // TODO freynaud why is that broken ?
    // result.deviceOrientation = UIATarget.localTarget().deviceOrientation();
    result.deviceOrientation = UIATarget.localTarget().frontMostApp().interfaceOrientation();

    return result;
}
/**
 * return an array with all the children of the element. Goes depth deep in the
 * tree. depth = -1 or undefined gets all elements
 * @param {nulber} depth -1 to return everything.
 * @return {Array} of all the children of that element, and their own children.
 */
UIAElement.prototype.getChildren = function (depth) {
    var res = new Array();

    if (!depth) {
        depth = -1;
    }
    var getChildren = function (depth, element) {
        res.push(element);
        var d = depth - 1;
        if (d > 0 || d !== -1) {
            var children = element.elements();
            var size = children.length;
            for (var i = 0; i < size; i++) {
                var child = children[i];
                getChildren(d, child);
            }
        }
    }
    getChildren(depth, this)
    return res;
}
var getKeys = function (obj) {
    var keys = [];
    for (var key in obj) {
        keys.push(key);
    }
    return keys;
}
/**
 * returns true if the element contains the x,y point specified.
 * @param {number} x
 * @param {number} y
 * @return {boolean} true if (x,y) is in the element.
 */
UIAElement.prototype.contains = function (x, y) {
    var rect = this.rect();

    if (x < Math.floor(rect.origin.x)) {
        return false;
    }
    if (y < Math.floor(rect.origin.y)) {
        return false;
    }
    if (x > (Math.floor(rect.origin.x) + Math.floor(rect.size.width))) {
        return false;
    }
    if (y > (Math.floor(rect.origin.y) + Math.floor(rect.size.height))) {
        return false;
    }
    log("match " + x + "," + y + " is in " + JSON.stringify(this.rect()) + "," + this.name());
    return true;
}

/**
 * returns true if the element matches the given criteria.
 * @param {criteria} criteria the criteria object.
 * @return {boolean} true is the element matches the given criteria.
 */
UIAElement.prototype.matches = function (criteria) {
    if (!criteria) {
        return true;
    }
    // criteria only have one field.It's either :
    // AND + array of criterias
    // OR + array of criterias
    // NOT + criteria
    // criteria
    var keys = getKeys(criteria);

    // empty filder matches all.
    if (keys == 0) {
        return true;
    }

    // AND, OR , NOT + array of criteria to apply
    if (keys.length == 1) {
        var key = keys[0];
        if (key === "AND") {
            var array = criteria[key];
            for (var c in array) {
                if (!this.matches(array[c])) {
                    return false;
                }
            }
            return true;
        } else if (key === "OR") {
            var array = criteria[key];
            for (var c in array) {
                if (this.matches(array[c])) {
                    return true;
                }
            }
            return false;
        } else if (key === "NOT") {
            return !this.matches(criteria[key][0]);
        } else {
            throw new UIAutomationException("not a valid criteria, -> " + JSON.stringify(criteria),
                                            32);
        }

    } else if (keys.length == 2) {// location match

        var x = criteria['x'];
        var y = criteria['y'];
        if (this.contains(x, y)) {
            return true;
            // !this.isStale();
        }
    } else if (keys.length == 4) {// property match
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
                throw new UIAutomationException("not a valid strategy, -> " + JSON
                    .stringify(criteria), 32);
        }

    } else {
        throw new UIAutomationException("not a valid criteria, -> " + JSON.stringify(criteria), 32);
    }
}

UIAElementNil.prototype.type = function () {
    return "UIAElementNil";
}

// TODO freynaud check why this is necessary. key extends elements.
UIAKey.prototype.type = UIAElement.prototype.type;
UIAElementArray.prototype.type = UIAElement.prototype.type;

UIAElementNil.prototype.reference = UIAElement.prototype.reference;

UIAKey.prototype.reference = UIAElement.prototype.reference;

UIAKey.prototype.scrollToVisible = function () {
};

UIAKeyboard.prototype.scrollToVisible = function () {
};
