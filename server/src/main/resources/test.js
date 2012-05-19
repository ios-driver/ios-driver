#import "json2.js"
#import "ui-automation.js"
#import "UIAutomation.js"
#import "UIAElement.js"


var target = UIATarget.localTarget();
target.setTimeout(0);
var app = target.frontMostApp();
var win = app.mainWindow();


var back =  win.element(-1,{'name':'Mountain 1'});
log("befor"+back.checkIsValid2());
back.tap();

//target.delay(4);

log("after"+back.checkIsValid2());


