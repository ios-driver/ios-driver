/**
 * @module ios-driver
 */

UIAAlert.prototype.defaultButton_origin = UIAAlert.prototype.defaultButton;

/**
 * return the default button, but also send an event that the alert disappeared.
 * @return {UIAButton} the default button for the alert.
 */
UIAAlert.prototype.defaultButton = function () {
    var res = this.defaultButton_origin();
    res.tap = function () {
        if (this.isVisible()) {
            var rect = this.rect();
            var x = rect.origin.x + (rect.size.width / 2);
            var y = rect.origin.y + (rect.size.height / 2);
            var point = {
                'x': Math.floor(x),
                'y': Math.floor(y)
            };
            UIATarget.localTarget().tap(point);
            UIAutomation.cache.clearAlert();
        } else {
            throw new UIAutomationException("element is not visible", 11);
        }
    }
    return res;
}
UIAAlert.prototype.cancelButton_origin = UIAAlert.prototype.cancelButton;

/**
 * return the cancel button, but also send an event that the alert disappeared.
 * @return {UIAButton} the cancel button for the alert.
 */
UIAAlert.prototype.cancelButton = function () {
    var res = this.cancelButton_origin();

    res.tap = function () {
        if (this.isVisible()) {
            var rect = this.rect();
            var x = rect.origin.x + (rect.size.width / 2);
            var y = rect.origin.y + (rect.size.height / 2);
            var point = {
                'x': Math.floor(x),
                'y': Math.floor(y)
            };
            UIATarget.localTarget().tap(point);
            UIAutomation.cache.clearAlert();
        } else {
            throw new UIAutomationException("element is not visible", 11);
        }
    }
    return res;
}
