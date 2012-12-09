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

    /**
     * Stores a UIElement in the cache. Some special elements have hardcoded values.
     *
     * The mainWindow = 0
     * UIAApplication  frontMostApp() = 1
     * UIATarget = 2
     * If there is an alert, its id = 3. id=3 is reserved for alerts.
     *
     * @param {UIAElement} element the element to cache.
     * @return {number} the id of the element in the cache.
     */
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

    /**
     * return the UIAElement with the given reference.
     *
     * @param {number} reference The reference of the element. ( the id from webdriver )
     * @param {boolean} opt_checkStale optional. Defaults to true. If true, checks if an element is stale, and
     * if not, scrolls it into view before returning it.
     * @return {UIAElement}
     */
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

    /**
     * Return the currently opened alert, and throws if no alert present.
     * @throws  UIAutomationException if there is no alert opened.
     * @return {UIAAlert} the currently opened alert.
     */
    this.getAlert = function () {
        return this.storage[3];
    };

    /**
     * set the current alert.
     * @param {UIAAlert} alert the alert to set.
     */
    this.setAlert = function (alert) {
        this.storage[3] = alert;
        log("found alert");
    };

    /**
     * remove the current alert.
     */
    this.clearAlert = function () {
        log("removed alert");
        this.storage[3] = null;
    }

    /**
     * empty the cache.
     */
    this.clear = function () {
        this.storage = {};
        this.storage[0] = UIATarget.localTarget().frontMostApp().mainWindow();
    };

    this.clear();

}