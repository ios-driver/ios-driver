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
UIAApplication.prototype.keyboard = function () {
    var keyboard = this.keyboard_original();
    log("keyboard : " + keyboard);
    if (keyboard.toString() === "[object UIAElementNil]") {
        UIATarget.localTarget().delay(2);
        keyboard = this.keyboard_original();
        log("keyboard after 2 : " + keyboard);
        if (keyboard.toString() == "[object UIAElementNil]") {
            throw new UIAutomationException("cannot find keyboard", 7);
        } else {
            return keyboard;
        }

    } else {
        return keyboard;
    }
}
