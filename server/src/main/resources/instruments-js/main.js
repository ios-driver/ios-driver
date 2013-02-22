UIATarget.localTarget().setTimeout(0);

try {
    UIAutomation.setAlertHandler();
    UIAutomation.commandLoop();

} catch (err) {
    log("err" + JSON.stringify(err));
}
