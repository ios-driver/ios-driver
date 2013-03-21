/*
 * Copyright 2012 ios-driver committers.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the Licence at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

/**
 * Navigate between the different webviews of safari.
 * @constructor
 */
function SafariPageNavigator() {
    this.target = UIATarget.localTarget();
    this.app = this.target.frontMostApp();
    this.implicit_wait_sec = 2;
}

SafariPageNavigator.prototype.initSelectWebViewMode = function () {
    this.scrollView = this.app.element(-1, getTypeCriteria("UIAScrollView"))
}
SafariPageNavigator.prototype.getCurrentToolBar = function () {
    return  this.app.element(-1, getTypeCriteria("UIAToolbar"));
}
SafariPageNavigator.prototype.enter = function () {
    UIATarget.localTarget().delay(1);
    var toolBar = this.getCurrentToolBar();
    var buttons = toolBar.elements2(-1, getTypeCriteria("UIAButton"));

    // normal mode. need to switch to the "select web view" mode.
    if (buttons.length === 5) {
        var pages = buttons[4];
        log("there are " + pages.value() + " pages.");
        pages.tap();
        this.target.delay(0.3);
        this.initSelectWebViewMode();
        return this;
        // select web view mode already.
    } else if (buttons.length === 2) {
        this.initSelectWebViewMode();
        log("already in select web view mode.");
        return this;
    } else {
        throw new UIAutomationException("Cannot find the state we're in."
                                            + "Was expecting 5 or 2 buttons but found "
                                            + buttons.length);
    }
}
getTypeCriteria = function (type) {
    return {
        "expected": type,
        "matching": "exact",
        "method": "type",
        "l10n": "none"
    };
}

getTypeAndLocationCriteria = function (type, x, y) {
    var criteria = {
        "AND": [
            {"l10n": "none", "expected": type, "matching": "exact", "method": "type"},
            {"y": y, "x": x}
        ]}
    return criteria;
}

getTypeAndNameCriteria = function (type, name) {
    return {"AND": [
        { "expected": type, "matching": "exact", "method": "type", "l10n": "none"},
        { "expected": name, "matching": "exact", "method": "name", "l10n": "none"}
    ]}
}

SafariPageNavigator.prototype.done = function () {
    var done = this.getCurrentToolBar().element(-1,
                                                getTypeAndNameCriteria("UIAButton", "Done"),
                                                this.implicit_wait_sec);
    done.tap();
}
SafariPageNavigator.prototype.newPage = function () {
    var newPage = this.getCurrentToolBar().element(-1,
                                                   getTypeAndNameCriteria("UIAButton", "New Page"),
                                                   this.implicit_wait_sec);
    newPage.tap();
}
/**
 * Default to previous
 * @param {number} direction if <0 , go to the previous page. If >0, go to the next one.
 */
SafariPageNavigator.prototype.select = function (direction) {
    if (typeof  direction === "undefined") {
        throw new UIAutomationException("need to specify the direction.");
    }

    log("select " + direction);

    // get either one. Enough to the the y position.
    var randomElement = this.app.element(getTypeCriteria("UIAElement"));
    var rect = randomElement.rect();

    // same for both.
    var y = (rect.origin.y + rect.size.height / 2);

    var x;
    if (direction > 0) {
        // next : click to the right of the screen.
        x = this.getCurrentToolBar().rect().size.width - 5;
    } else if (direction < 0) {
        // previous , click on the left of the screen.
        var x = 1;
    }
    log("point  " + x + "," + y);
    // check the there is a UIAElement positioned properly.
    var element;
    try {
        element = this.app.element(-1, getTypeAndLocationCriteria("UIAElement", x, y));
    } catch (err) {
        var msg = "Last webview.Cannot go further.";
        if (direction < 0) {
            msg = "First webview.Cannot go to previous one..";
        }
        throw new UIAutomationException(msg, 23);
    }

    var point = {
        'x': x,
        'y': y
    };
    this.target.tap(point);
    this.target.delay(0.4);

}

SafariPageNavigator.prototype.goToWebView = function (opt_times) {
    if (typeof  opt_times === "undefined") {
        opt_times = 1;
    }
    var max = opt_times;
    if (opt_times < 0) {
        max = -max;
    }
    for (var i = 0; i < max; i++) {
        this.select(opt_times);
    }
    this.done();
}







