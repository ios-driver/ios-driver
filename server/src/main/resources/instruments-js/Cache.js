/**
 * @module ios-driver
 */

/**
 *
 * @constructor
 */
var Cache = function () {
    this.storage = {};
    this.lastReference = 3;

    this.store = function (element) {
        if (element && element.type && element.type() === "UIAApplication") {
            var id = 1;
            element.id = id;
            return id;
        } else {
            this.lastReference++;
            element.id = this.lastReference;
            this.storage[this.lastReference] = element;
            return element.id;
        }

    };

    this.get = function (reference, opt_checkStale) {
        var checkStale = true;
        if (opt_checkStale === false) {
            checkStale = false;
        }
        if (reference == 0) {
            return UIATarget.localTarget().frontMostApp().mainWindow();
        } else if (reference == 1) {
            return UIATarget.localTarget().frontMostApp();
        } else if (reference == 2) {
            return UIATarget.localTarget();
        } else if (reference == 3) {
            if (this.storage[3]) {
                return this.storage[3];
            } else {
                throw new UIAutomationException("No alert opened", 27);
            }

        }

        var res = this.storage[reference];

        // there is an alert.
        if (this.storage[3]) {

            if (res.isInAlert()) {
                return res;
            } else {
                throw new UIAutomationException("cannot interact with object " + res
                                                    + ". There is an alert.", 26);
            }

        } else {
            // target and app aren't stale.
            if (!res) {
                throw new UIAutomationException("can't find " + reference + " in cache.");
                // window an apps aren't stale ?
            } else if (res.type && (res.type() == "UIAWindow" || res.type() == "UIAApplication")) {
                return res;
                // on arrays, stale doesn't make sense.
            } else if (checkStale && res.isStale && res.isStale()) {
                throw new UIAutomationException("elements ref:" + reference + " is stale", 10);
            } else {
                return res;
            }
        }

    };

    this.getAlert = function () {
        return this.storage[3];
    };
    this.setAlert = function (alert) {
        this.storage[3] = alert;
        log("found alert");
    };

    this.clearAlert = function () {
        log("removed alert");
        this.storage[3] = null;
    }

    this.clear = function () {
        this.storage = {};
        this.storage[0] = UIATarget.localTarget().frontMostApp().mainWindow();
    };

    this.clear();

}