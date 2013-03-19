/**
 * @module ios-driver
 */


UIAApplication.prototype.keyboard_original = UIAApplication.prototype.keyboard;

/**
 * Returns the keyboard the  user will use to interact with the UIElement currenty having the focus.
 * If the keyboard isn't there yet, wait for it to appear up to implicit timeout.
 *
 * @return {UIAKeyboard} the keyboard used to sendkeys.
 *
 */
UIAApplication.prototype.keyboard = function (opt_implicit_wait) {
    var wait = 2;
    if (!(typeof opt_implicit_wait === "undefined")) {
        wait = opt_implicit_wait;
    }
    var keyboard = this.keyboard_original();
    if (keyboard.toString() === "[object UIAElementNil]") {
        UIATarget.localTarget().delay(wait);
        keyboard = this.keyboard_original();
        if (keyboard.toString() == "[object UIAElementNil]") {
            throw new UIAutomationException("cannot find keyboard", 7);
        } else {
            return keyboard;
        }

    } else {
        return keyboard;
    }
}
