package org.uiautomation.ios.server;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIAStaticText;
import org.uiautomation.ios.UIAModels.UIATableCell;
import org.uiautomation.ios.UIAModels.UIAWindow;
import org.uiautomation.ios.server.utils.JSONToXMLConvertor;
import org.uiautomation.ios.server.utils.XPath2Engine;
import org.uiautomation.ios.server.utils.XPathWithL10N;

import com.google.common.collect.ImmutableMap;

public class XpathTreeFinderTest {

  private final String uiCatalog1 = "{\"tree\":{\"ref\":4,\"name\":\"UICatalog\",\"value\":null,\"children\":[{\"ref\":5,\"name\":null,\"value\":null,\"children\":[{\"ref\":6,\"name\":\"UICatalog\",\"value\":null,\"children\":[{\"ref\":7,\"name\":null,\"value\":null,\"children\":[{\"ref\":8,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":9,\"name\":\"UICatalog\",\"value\":\"UICatalog\",\"label\":\"UICatalog\",\"rect\":{\"origin\":{\"y\":29,\"x\":112},\"size\":{\"height\":27,\"width\":95}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIANavigationBar\"},{\"ref\":10,\"name\":\"Empty list\",\"value\":\"rows 1 to 10 of 12\",\"children\":[{\"ref\":11,\"name\":\"Buttons, Various uses of UIButton\",\"value\":null,\"children\":[{\"ref\":12,\"name\":\"Buttons, Various uses of UIButton\",\"value\":\"Buttons, Various uses of UIButton\",\"label\":\"Buttons, Various uses of UIButton\",\"rect\":{\"origin\":{\"y\":64,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":64,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":13,\"name\":\"Controls, Various uses of UIControl\",\"value\":null,\"children\":[{\"ref\":14,\"name\":\"Controls, Various uses of UIControl\",\"value\":\"Controls, Various uses of UIControl\",\"label\":\"Controls, Various uses of UIControl\",\"rect\":{\"origin\":{\"y\":108,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":108,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":15,\"name\":\"TextFields, Uses of UITextField\",\"value\":null,\"children\":[{\"ref\":16,\"name\":\"TextFields, Uses of UITextField\",\"value\":\"TextFields, Uses of UITextField\",\"label\":\"TextFields, Uses of UITextField\",\"rect\":{\"origin\":{\"y\":152,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":152,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":17,\"name\":\"SearchBar, Use of UISearchBar\",\"value\":null,\"children\":[{\"ref\":18,\"name\":\"SearchBar, Use of UISearchBar\",\"value\":\"SearchBar, Use of UISearchBar\",\"label\":\"SearchBar, Use of UISearchBar\",\"rect\":{\"origin\":{\"y\":196,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":196,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":19,\"name\":\"TextView, Use of UITextField\",\"value\":null,\"children\":[{\"ref\":20,\"name\":\"TextView, Use of UITextField\",\"value\":\"TextView, Use of UITextField\",\"label\":\"TextView, Use of UITextField\",\"rect\":{\"origin\":{\"y\":240,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":240,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":21,\"name\":\"Pickers, Uses of UIDatePicker, UIPickerView\",\"value\":null,\"children\":[{\"ref\":22,\"name\":\"Pickers, Uses of UIDatePicker, UIPickerView\",\"value\":\"Pickers, Uses of UIDatePicker, UIPickerView\",\"label\":\"Pickers, Uses of UIDatePicker, UIPickerView\",\"rect\":{\"origin\":{\"y\":284,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":284,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":23,\"name\":\"Images, Use of UIImageView\",\"value\":null,\"children\":[{\"ref\":24,\"name\":\"Images, Use of UIImageView\",\"value\":\"Images, Use of UIImageView\",\"label\":\"Images, Use of UIImageView\",\"rect\":{\"origin\":{\"y\":328,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":328,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":25,\"name\":\"Web, Use of UIWebView\",\"value\":null,\"children\":[{\"ref\":26,\"name\":\"Web, Use of UIWebView\",\"value\":\"Web, Use of UIWebView\",\"label\":\"Web, Use of UIWebView\",\"rect\":{\"origin\":{\"y\":372,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":372,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":27,\"name\":\"Segment, Various uses of UISegmentedControl\",\"value\":null,\"children\":[{\"ref\":28,\"name\":\"Segment, Various uses of UISegmentedControl\",\"value\":\"Segment, Various uses of UISegmentedControl\",\"label\":\"Segment, Various uses of UISegmentedControl\",\"rect\":{\"origin\":{\"y\":416,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":416,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":29,\"name\":\"Toolbar, Uses of UIToolbar\",\"value\":null,\"children\":[{\"ref\":30,\"name\":\"Toolbar, Uses of UIToolbar\",\"value\":\"Toolbar, Uses of UIToolbar\",\"label\":\"Toolbar, Uses of UIToolbar\",\"rect\":{\"origin\":{\"y\":460,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":460,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":31,\"name\":\"Alerts, Various uses of UIAlertView, UIActionSheet\",\"value\":null,\"children\":[{\"ref\":32,\"name\":\"Alerts, Various uses of UIAlertView, UIActionSheet\",\"value\":\"Alerts, Various uses of UIAlertView, UIActionSheet\",\"label\":\"Alerts, Various uses of UIAlertView, UIActionSheet\",\"rect\":{\"origin\":{\"y\":504,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":504,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"},{\"ref\":33,\"name\":\"Transitions, Shows UIViewAnimationTransitions\",\"value\":null,\"children\":[{\"ref\":34,\"name\":\"Transitions, Shows UIViewAnimationTransitions\",\"value\":\"Transitions, Shows UIViewAnimationTransitions\",\"label\":\"Transitions, Shows UIViewAnimationTransitions\",\"rect\":{\"origin\":{\"y\":548,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":548,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIATableCell\"}],\"label\":\"Empty list\",\"rect\":{\"origin\":{\"y\":64,\"x\":0},\"size\":{\"height\":416,\"width\":320}},\"type\":\"UIATableView\"},{\"ref\":35,\"name\":null,\"value\":null,\"children\":[{\"ref\":36,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":37,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAToolbar\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"},{\"ref\":38,\"name\":null,\"value\":null,\"children\":[{\"ref\":39,\"name\":null,\"value\":null,\"children\":[{\"ref\":40,\"name\":\"Swipe down with three fingers to reveal the notification center., Double-tap to scroll to top\",\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":4},\"size\":{\"height\":20,\"width\":43}},\"type\":\"UIAElement\"},{\"ref\":41,\"name\":\"3 of 3 bars, Wi-Fi signal strength\",\"value\":null,\"label\":\"3 of 3 bars, Wi-Fi signal strength\",\"rect\":{\"origin\":{\"y\":0,\"x\":52},\"size\":{\"height\":20,\"width\":20}},\"type\":\"UIAElement\"},{\"ref\":42,\"name\":\"06:39\",\"value\":null,\"label\":\"06:39\",\"rect\":{\"origin\":{\"y\":0,\"x\":142},\"size\":{\"height\":20,\"width\":36}},\"type\":\"UIAElement\"},{\"ref\":43,\"name\":\"100% battery power\",\"value\":null,\"label\":\"100% battery power\",\"rect\":{\"origin\":{\"y\":0,\"x\":296},\"size\":{\"height\":20,\"width\":21}},\"type\":\"UIAElement\"},{\"ref\":44,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":45,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":20,\"width\":320}},\"type\":\"UIAStatusBar\"},{\"ref\":46,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":47,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"}],\"label\":\"UICatalog\",\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":460,\"width\":320}},\"type\":\"UIAApplication\"},\"deviceOrientation\":1}";
  private final String intlMountainZH = "{\"ref\":1,\"name\":\"山\",\"value\":null,\"children\":[{\"ref\":7,\"name\":null,\"value\":null,\"children\":[{\"ref\":8,\"name\":\"山 Detail\",\"value\":null,\"children\":[{\"ref\":9,\"name\":null,\"value\":null,\"children\":[{\"ref\":10,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":11,\"name\":\"山\",\"value\":null,\"label\":\"山\",\"rect\":{\"origin\":{\"y\":27,\"x\":5},\"size\":{\"height\":30,\"width\":48}},\"type\":\"UIAButton\"},{\"ref\":12,\"name\":\"山 Detail\",\"value\":\"山 Detail\",\"label\":\"山 Detail\",\"rect\":{\"origin\":{\"y\":29,\"x\":119},\"size\":{\"height\":27,\"width\":82}},\"type\":\"UIAStaticText\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIANavigationBar\"},{\"ref\":13,\"name\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"value\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"label\":\"山 1 是8,848米高。它第一次攀登了在29 May 1953。\",\"rect\":{\"origin\":{\"y\":168,\"x\":34},\"size\":{\"height\":164,\"width\":251}},\"type\":\"UIAStaticText\"},{\"ref\":14,\"name\":null,\"value\":null,\"children\":[{\"ref\":15,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":320}},\"type\":\"UIAImage\"},{\"ref\":16,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":480,\"x\":0},\"size\":{\"height\":44,\"width\":320}},\"type\":\"UIAToolbar\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"},{\"ref\":17,\"name\":null,\"value\":null,\"children\":[{\"ref\":18,\"name\":null,\"value\":null,\"children\":[{\"ref\":19,\"name\":\"使用三指向下滑過螢幕來顯示通知中心。, 點兩下來捲動到頁首\",\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":4},\"size\":{\"height\":20,\"width\":52}},\"type\":\"UIAElement\"},{\"ref\":20,\"name\":\"Wi-Fi 訊號強度 3 格(共 3 格)\",\"value\":null,\"label\":\"Wi-Fi 訊號強度 3 格(共 3 格)\",\"rect\":{\"origin\":{\"y\":0,\"x\":61},\"size\":{\"height\":20,\"width\":20}},\"type\":\"UIAElement\"},{\"ref\":21,\"name\":\"14:17\",\"value\":null,\"label\":\"14:17\",\"rect\":{\"origin\":{\"y\":0,\"x\":142},\"size\":{\"height\":20,\"width\":36}},\"type\":\"UIAElement\"},{\"ref\":22,\"name\":\"電池電力 100%\",\"value\":null,\"label\":\"電池電力 100%\",\"rect\":{\"origin\":{\"y\":0,\"x\":296},\"size\":{\"height\":20,\"width\":21}},\"type\":\"UIAElement\"},{\"ref\":23,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":24,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":20,\"width\":320}},\"type\":\"UIAStatusBar\"},{\"ref\":25,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":0},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"},{\"ref\":26,\"name\":null,\"value\":null,\"label\":null,\"rect\":{\"origin\":{\"y\":477,\"x\":317},\"size\":{\"height\":3,\"width\":3}},\"type\":\"UIAImage\"}],\"label\":null,\"rect\":{\"origin\":{\"y\":0,\"x\":0},\"size\":{\"height\":480,\"width\":320}},\"type\":\"UIAWindow\"}],\"label\":\"山\",\"rect\":{\"origin\":{\"y\":20,\"x\":0},\"size\":{\"height\":460,\"width\":320}},\"type\":\"UIAApplication\"}";

  private JSONObject tree;
  private XPath2Engine parser;

  private JSONObject treeIntl;
  private XPath2Engine parserIntl;

  @BeforeClass
  public void setup() throws Exception {
    tree = new JSONObject(uiCatalog1).getJSONObject("tree");
    treeIntl = new JSONObject(intlMountainZH);

    parserIntl = new XPath2Engine(new JSONToXMLConvertor(treeIntl).asXML());
    parser = new XPath2Engine(new JSONToXMLConvertor(tree).asXML());

  }

  private Map<String, String> expected(int reference, Class<? extends UIAElement> type) {
    return ImmutableMap.of("ELEMENT", "" + reference, "type", type.getSimpleName());
  }

  private final String mainWindowRef = "5";
  private final String otherWindow = "38";

  @DataProvider(name = "xpaths")
  private Object[][] xpathAndResult() {
    return new Object[][] {
        { "//UIATableCell/UIAStaticText[@name='SearchBar, Use of UISearchBar']", expected(18, UIAStaticText.class) },
        { "//*[contains(@name,'Use of UISearchBar') and @value='null']", expected(17, UIATableCell.class) },
        { "//*[contains(@name,'Use of UISearchBar') and contains(@value,'Use of UISearchBar')]",
            expected(18, UIAStaticText.class) } };
  }

  @Test(dataProvider = "xpaths")
  public void findSingle(String xpath, Map<String, String> expected) {
    Map<String, String> found = parser.findElementByXpath(xpath);
    Assert.assertEquals(found, expected);
  }

  @Test
  public void findSingleReturnsFirstMAtch() {
    Map<String, String> found = parser.findElementByXpath("//*[contains(@name,'UISearchBar')]");
    Assert.assertEquals(found, expected(17, UIATableCell.class));
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void noMatch() {
    parser.findElementByXpath("//*[contains(@name,'ferret')]");
  }

  @Test(expectedExceptions = InvalidSelectorException.class)
  public void wrongXpath() {
    parser.findElementByXpath("//*[contains('UISearchBar')]");
  }

  @Test
  public void findElementElement() {
    Map<String, String> found = parser.findElementByXpath("//*[contains(@name,'UISearchBar')]", mainWindowRef);
    Assert.assertEquals(found, expected(17, UIATableCell.class));
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void dotSlashDoesntFindTopElement() {
    Map<String, String> found = parser.findElementByXpath("./*[contains(@name,'UISearchBar')]", otherWindow);
    Assert.assertEquals(found, expected(17, UIATableCell.class));
  }

  @Test
  public void findMultiple() {
    List<Map<String, String>> founds = parser.findElementsByXpath("//UIAWindow");
    Assert.assertEquals(founds.size(), 2);
    Assert.assertTrue(founds.contains(expected(5, UIAWindow.class)));
    Assert.assertTrue(founds.contains(expected(38, UIAWindow.class)));
    Assert.assertFalse(founds.contains(expected(5, UIAStaticText.class)));
  }

  @Test
  public void findMultipleNoResult() {
    List<Map<String, String>> founds = parser.findElementsByXpath("//html");
    Assert.assertEquals(founds.size(), 0);
  }

  @Test
  public void findElementsElement() {
    List<Map<String, String>> founds = parser.findElementsByXpath("//*[contains(@name,'UISearchBar')]", mainWindowRef);
    Assert.assertEquals(founds.size(), 2);
    Assert.assertTrue(founds.contains(expected(17, UIATableCell.class)));
    Assert.assertTrue(founds.contains(expected(18, UIAStaticText.class)));
  }

  @Test
  public void dotSlashDoesntFindTopElements() {
    List<Map<String, String>> founds = parser.findElementsByXpath("./*[contains(@name,'UISearchBar')]", otherWindow);
    Assert.assertEquals(founds.size(), 0);
  }

  @Test(expectedExceptions = InvalidSelectorException.class)
  public void wrongXpathElements() {
    parser.findElementsByXpath("./*[contains(@name,'UISearchBar')", otherWindow);
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void elementsDoesntExist() {
    parser.findElementsByXpath("./*[contains(@name,'UISearchBar')", "50000");
  }

  @Test
  public void regex() {
    List<Map<String, String>> elements = parser.findElementsByXpath("//*[matches(@name,'(.*)UISearchBar(.*)')]");
    Assert.assertEquals(elements.size(), 2);
  }

  @Test(expectedExceptions = NoSuchElementException.class)
  public void elementsDoesntExistIntl() {
    parserIntl.findElementsByXpath("./*[contains(@name,'UISearchBar')", "50000");
  }

  @Test
  public void withL10N() throws Exception {
    XPath2Engine parserIntl = new XPath2Engine(new JSONToXMLConvertor(treeIntl).asXML());
    XPathWithL10N xpath = new XPathWithL10N("//*[matches(@name,l10n('meterFormat'))]");
    xpath.setTranslation("meterFormat", "(.*)米(.*)");
    Map<String, String> el = parserIntl.findElementByXpath(xpath.getXPath());
    Assert.assertEquals(el, expected(13, UIAStaticText.class));
  }

  @Test
  public void withL10N2() throws Exception {
    XPath2Engine parserIntl = new XPath2Engine(new JSONToXMLConvertor(treeIntl).asXML());
    XPathWithL10N xpath = new XPathWithL10N("//*[matches(@name,l10n('meterFormat'))]");
    xpath.setTranslation("meterFormat", "(.*)米(.*)");
    Map<String, String> el = parserIntl.findElementByXpath(xpath.getXPath());
    Assert.assertEquals(el, expected(13, UIAStaticText.class));
  }

  @Test
  public void withL10N3() throws Exception {
    XPath2Engine parserIntl = new XPath2Engine(new JSONToXMLConvertor(treeIntl).asXML());
    XPathWithL10N xpath = new XPathWithL10N("//*[matches(@name,l10n('meterFormat'))]");
    xpath.setTranslation("meterFormat", "(.*)米(.*)");
    Map<String, String> el = parserIntl.findElementByXpath(xpath.getXPath());
    Assert.assertEquals(el, expected(13, UIAStaticText.class));
  }

  @Test
  public void contains() throws Exception {
    XPathWithL10N xpath = new XPathWithL10N("//*[contains(@name,'米')]");
    Map<String, String> el = parserIntl.findElementByXpath(xpath.getXPath());
    Assert.assertEquals(el, expected(13, UIAStaticText.class));
  }

}
