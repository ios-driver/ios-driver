package org.uiautomation.ios.server.command.uiautomation;


import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.server.IOSServerManager;
import org.uiautomation.ios.server.command.UIAScriptHandler;

public class SetSliderPosNHandler extends UIAScriptHandler {
    private static final
    String
            template =
            "var element = UIAutomation.cache.get(:reference);" +
                    "element.dragToValue(:value);" +
                    "UIAutomation.createJSONResponse(':sessionId',0,'')";


    public SetSliderPosNHandler(IOSServerManager driver, WebDriverLikeRequest request) throws JSONException {
        super(driver, request);

        String js = template
                .replace(":sessionId", request.getSession())
                .replace(":reference", request.getVariableValue(":reference"))
                .replace(":value", request.getPayload().getString("dragToValue"));

        setJS(js);
    }

    @Override
    public JSONObject configurationDescription() throws JSONException {
        return noConfigDefined();
    }
}
