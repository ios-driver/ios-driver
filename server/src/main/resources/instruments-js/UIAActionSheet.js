/**
 * @module ios-driver
 */


UIAActionSheet.prototype.cancelButton_origin = UIAActionSheet.prototype.cancelButton;
/**
 * dismiss the UIAActionSheet.
 */
UIAActionSheet.prototype.dismiss = function () {
    for (var name in this) {
        log("- " + this.dom());
    }
    var button = this.cancelButton();
    if (button.type() === "UIAElementNil") {
        log("cancel is not there,trying default first button.");
        var criteria = {"l10n": "none", "expected": "UIAButton", "matching": "exact", "method": "type"};
        try {
            button = this.element(-1, criteria);
        } catch
            (err) {
            throw new UIAutomationException("this UIAActionSheet doesn't "
                                                + "have any button.", 7);
        }
    }
    button.tap();
}

/**
 * accept the UIAActionSheet.
 */
UIAActionSheet.prototype.accept = function () {
    var button = this.defaultButton();
    if (button.type() === "UIAElementNil") {
        throw new UIAutomationException("this UIAActionSheet doesn't have a default normal buttons.",
                                        7);
    }
    button.tap();
}

/**
 * send the value to the alert, assuming it has a text field.
 * @param value
 */
UIAActionSheet.prototype.sendKeys = function (value) {
    throw new UIAutomationException("UIAActionSheet doesn't have sendKeys", 9);
}

UIAActionSheet.prototype.getText = function () {
    log("getText");
    var criteria = {"l10n": "none", "expected": "UIAStaticText", "matching": "exact", "method": "type"};
    var texts = this.elements2(-1, criteria);
    if (texts.length > 0) {
        return texts[0].name();
    } else {
        throw new UIAutomationException("this UIAActionSheet doesn't have any text");
    }
}

/**
 * return the default button, but also send an event that the alert disappeared.
 * @return {UIAButton} the default button for the alert.
 */
UIAActionSheet.prototype.defaultButton = function () {
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

/**
 * return the cancel button, but also send an event that the alert disappeared.
 * @return {UIAButton} the cancel button for the alert.
 */
UIAActionSheet.prototype.cancelButton = function () {
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
