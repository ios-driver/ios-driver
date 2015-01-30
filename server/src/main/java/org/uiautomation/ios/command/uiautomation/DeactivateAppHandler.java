package org.uiautomation.ios.command.uiautomation;

import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.UIAScriptHandler;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.utils.JSTemplate;

public class DeactivateAppHandler extends UIAScriptHandler {

    private static final JSTemplate template = new JSTemplate(
            "UIATarget.localTarget().deactivateAppForDuration(%:duration$f);" +
                    "UIAutomation.createJSONResponse('%:sessionId$s',0,'')",
            "sessionId", "duration");

    public DeactivateAppHandler(IOSServerManager driver, WebDriverLikeRequest request) {
        super(driver, request);

        JSONObject payload = request.getPayload();

        String js = template.generate(
                request.getSession(),
                payload.optDouble("duration"));
        setJS(js);
    }

    @Override
    public JSONObject configurationDescription() throws JSONException {
        return noConfigDefined();
    }

}
