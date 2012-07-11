import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.ClientSideCriteriaFactory;


public class ClientSideCriteriaTests {

  ClientSideCriteriaFactory factory;

  @BeforeClass
  public void setup() throws IOException, JSONException {
    InputStream is = ClientSideCriteriaTests.class.getResourceAsStream("/ClientSideL10N.json");

    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    JSONArray json = new JSONArray(writer.toString());
    Map<String, String> content = new HashMap<String, String>();
    for (int i = 0; i < json.length(); i++) {
      JSONObject entry = json.getJSONObject(i);
      String key = (String) entry.keys().next();
      content.put(key, entry.getString(key));
    }

    factory = new ClientSideCriteriaFactory(content);

  }

  @Test
  public void clienSideMapping() throws JSONException {
    NameCriteria c = factory.nameCriteria("abc", L10NStrategy.clientL10N,MatchingStrategy.exact);
    Assert.assertEquals(c.getValue(), "abc localisé.");
  }

}
