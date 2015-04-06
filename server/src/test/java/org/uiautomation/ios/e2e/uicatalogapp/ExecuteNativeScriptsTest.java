/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.e2e.uicatalogapp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.SampleApps;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

public class ExecuteNativeScriptsTest extends BaseIOSDriverTest {

  @BeforeClass
  public void startDriver() {
    driver = getDriver(SampleApps.uiCatalogCap());
    driver.findElement(By.xpath("//UIAButton[@name='UICatalog']")).click();
  }

  private static final String buttonsName = "Buttons";

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAString() {
    Object result = executeScript("return  UIATarget.localTarget().frontMostApp().bundleID();");

    assertTrue(result instanceof String);
    assertEquals(result, "com.example.apple-samplecode.UICatalog");
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnALong() {
    Object
        result =
        executeScript("return UIATarget.localTarget().frontMostApp().windows().length;");

    assertTrue(result instanceof Long, result.getClass().getName());
    assertTrue((Long) result > 1);
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAWebElement() {
    Object result = executeScript("return UIATarget.localTarget().frontMostApp()");

    assertNotNull(result);
    assertTrue(result instanceof WebElement);
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnABoolean() {
    Object result = executeScript("return true;");

    assertNotNull(result);
    assertTrue(result instanceof Boolean);
    assertTrue((Boolean) result);
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAStringsArray() {
    List<Object> expectedResult = new ArrayList<Object>();
    expectedResult.add("zero");
    expectedResult.add("one");
    expectedResult.add("two");
    Object result = ((JavascriptExecutor) driver).executeScript("return ['zero', 'one', 'two'];");

    compareLists(expectedResult, (List<Object>) result);
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAnArray() {
    List<Object> expectedResult = new ArrayList<Object>();
    expectedResult.add("zero");
    List<Object> subList = new ArrayList<Object>();
    subList.add(true);
    subList.add(false);
    expectedResult.add(subList);
    Object result = executeScript("return ['zero', [true, false]];");
    assertNotNull(result);
    assertTrue(result instanceof List, "result was: " + result + " (" + result.getClass() + ")");
    List<Object> list = (List<Object>) result;
    assertTrue(compareLists(expectedResult, list));
  }

  @Test
  public void testShouldBeAbleToExecuteJavascriptAndReturnABasicObjectLiteral() {
    Object result = executeScript("return {abc: '123', tired: false};");
    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;

    Map<String, Object> expected = new HashMap<String, Object>();
    expected.put("abc", "123");
    expected.put("tired", false);

    assertTrue(map.size() >= expected.size(), "Expected:<" + expected + ">, but was:<" + map + ">");
    for (Map.Entry<String, Object> entry : expected.entrySet()) {
      assertEquals(entry.getValue(), map.get(entry.getKey()),
                   "Difference at key:<" + entry.getKey() + ">");
    }
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAnObjectLiteral() {

    Map<String, Object> expectedResult = new HashMap<String, Object>() {
      {
        put("foo", "bar");
        put("baz", Arrays.asList("a", "b", "c"));
        put("person", new HashMap<String, String>() {
          {
            put("first", "John");
            put("last", "Doe");
          }
        });
      }
    };

    Object
        result =
        executeScript(
            "return {foo:'bar', baz: ['a', 'b', 'c'], person: {first: 'John',last: 'Doe'}};");
    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");

    Map<String, Object> map = (Map<String, Object>) result;
    assertTrue(map.size() == 3, "Expected:<" + expectedResult + ">, but was:<" + map + ">");
    assertEquals("bar", map.get("foo"));
    assertTrue(compareLists((List<?>) expectedResult.get("baz"), (List<?>) map.get("baz")));

    Map<String, String> person = (Map<String, String>) map.get("person");
    assertTrue(person.size() == (2), "Expected:<{first:John, last:Doe}>, but was:<" + person + ">");
    assertEquals("John", person.get("first"));
    assertEquals("Doe", person.get("last"));
  }

  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAComplexObject() {
    Object
        result =
        executeScript("return UIATarget.localTarget().frontMostApp().windows()[0].rect()");

    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;
    Map<String, Long> origin = (Map<String, Long>) map.get("origin");
    Assert.assertEquals(origin.get("x"), (Long) 0L);
    Assert.assertEquals(origin.get("y"), (Long) 0L);
  }

  private static boolean compareLists(List<?> first, List<?> second) {
    if (first.size() != second.size()) {
      return false;
    }
    for (int i = 0; i < first.size(); ++i) {
      if (first.get(i) instanceof List<?>) {
        if (!compareLists((List<?>) first.get(i), (List<?>) second.get(i))) {
          return false;
        }
      } else {
        if (!first.get(i).equals(second.get(i))) {
          return false;
        }
      }
    }
    return true;
  }

  @Test
  public void testPassingAndReturningALongShouldReturnAWholeNumber() {
    Long expectedResult = 1L;
    Object result = executeScript("return arguments[0];", expectedResult);
    assertTrue((result instanceof Integer || result instanceof Long),
               "Expected result to be an Integer or Long but was a " + result.getClass());
    assertEquals(expectedResult.longValue(), result);
  }

  @Test
  public void testPassingAndReturningADoubleShouldReturnADecimal() {
    Double expectedResult = 1.2;
    Object result = executeScript("return arguments[0];", expectedResult);
    assertTrue(result instanceof Float || result instanceof Double,
               "Expected result to be a Double or Float but was a " + result.getClass());
    assertEquals(expectedResult.doubleValue(), result);
  }

  @Test
  public void testShouldThrowAnExceptionWhenTheJavascriptIsBad() {
    try {
      executeScript("return squiggle();");
      fail("Expected an exception");
    } catch (Exception e) {
      // This is expected
      assertFalse(e.getMessage().startsWith("null "), e.getMessage());
    }
  }

  @Test
  public void testShouldBeAbleToCallFunctionsDefinedOnThePage() {
    Object
        res =
        executeScript(
            "var app =  UIATarget.localTarget().frontMostApp();var reference = UIAutomation.cache.store(app); return reference;");
    assertEquals(res, 1L);
  }

  @Test
  public void testShouldBeAbleToPassAStringAnAsArgument() {
    String
        value =
        (String) executeScript("return arguments[0] == 'fish' ? 'fish' : 'not fish';", "fish");
    assertEquals("fish", value);
  }

  @Test
  public void testShouldBeAbleToPassABooleanAnAsArgument() {
    boolean value = (Boolean) executeScript("return arguments[0] == true;", true);
    assertTrue(value);
  }

  @Test
  public void testShouldBeAbleToPassANumberAnAsArgument() {
    boolean value = (Boolean) executeScript("return arguments[0] == 1 ? true : false;", 1);
    assertTrue(value);
  }

  @Test
  public void testShouldBeAbleToPassAWebElementAsArgument() {
    WebElement cell = driver.findElement(By.xpath("//UIATableCell[@name='" + buttonsName + "']"));
    String res = (String) executeScript("return arguments[0].type();", cell);
    assertEquals(res, "UIATableCell");
  }

  @Test
  public void testPassingArrayAsOnlyArgumentFlattensArray() {
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    String value = (String) executeScript("return arguments[0]", array);
    assertEquals(array[0], value);
  }

  @Test
  public void testShouldBeAbleToPassAnArrayAsAdditionalArgument() {
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    long length = (Long) executeScript("return arguments[1].length", "string", array);
    assertEquals(array.length, length);
  }

  @Test
  public void testShouldBeAbleToPassACollectionAsArgument() {
    Collection<Object> collection = new ArrayList<Object>();
    collection.add("Cheddar");
    collection.add("Brie");
    collection.add(7);
    long length = (Long) executeScript("return arguments[0].length", collection);
    assertEquals(collection.size(), length);

    collection = new HashSet<Object>();
    collection.add("Gouda");
    collection.add("Stilton");
    collection.add("Stilton");
    collection.add(true);
    length = (Long) executeScript("return arguments[0].length", collection);
    assertEquals(collection.size(), length);
  }

  @Test
  public void testShouldThrowAnExceptionIfAnArgumentIsNotValid() {
    try {
      executeScript("return arguments[0];", driver);
      fail("Exception should have been thrown");
    } catch (IllegalArgumentException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldBeAbleToPassInMoreThanOneArgument() {
    String result = (String) executeScript("return arguments[0] + arguments[1];", "one", "two");
    assertEquals("onetwo", result);
  }

  @Test
  public void testShouldBeAbleToReturnAnArrayOfWebElements() {
    // 1 more new window in UICatalog. 2 becomes 3.
    List<WebElement>
        items =
        (List<WebElement>) executeScript("return UIATarget.localTarget().frontMostApp().windows()");
    assertEquals(items.size(), 3);
  }

  @Test
  public void testJavascriptStringHandlingShouldWorkAsExpected() {
    String value = (String) executeScript("return '';");
    assertEquals("", value);

    value = (String) executeScript("return undefined;");
    assertNull(value);

    value = (String) executeScript("return ' '");
    assertEquals(" ", value);
  }

  @Test(enabled = false)
  public void testShouldBeAbleToExecuteABigChunkOfJavascriptCode() throws IOException {
    // File jqueryFile = InProject.locate("common/src/web/jquery-1.3.2.js");
    // String jquery = Files.toString(jqueryFile, Charset.forName("US-ASCII"));
    // assertTrue(jquery.length() > 50000,
    // "The javascript code should be at least 50 KB.");
    // This should not throw an exception ...
    // executeScript(jquery);
  }

  @Test(enabled = false)
  public void testShouldThrowExceptionIfExecutingOnNoPage() {
    try {
      ((JavascriptExecutor) driver).executeScript("return 1;");
    } catch (WebDriverException e) {
      // Expected
      return;
    }
    fail("Expected exception to be thrown");
  }

  @Test
  public void testShouldBeAbleToCreateAPersistentValue() {
    executeScript("UIAutomation.newAttribute = new Array()");
    executeScript("UIAutomation.newAttribute.push('hello world')");
    String text = (String) executeScript("return UIAutomation.newAttribute[0];");

    assertEquals("hello world", text);
  }

  @Test
  public void testCanHandleAnArrayOfElementsAsAnObjectArray() {
    List<WebElement> forms = driver.findElements(By.xpath("//UIATableCell"));
    Object[] args = new Object[]{forms};

    String
        name =
        (String) ((JavascriptExecutor) driver).executeScript("return arguments[0][0].type()", args);

    assertEquals(name, "UIATableCell");
  }

  @Test
  public void testCanPassAMapAsAParameter() {
    List<Integer> nums = ImmutableList.of(1, 2);
    Map<String, Object> args = ImmutableMap.of("bar", "test", "foo", nums);

    Object res = ((JavascriptExecutor) driver).executeScript("return arguments[0]['foo'][1]", args);

    assertEquals(2, ((Number) res).intValue());
  }

  private Object executeScript(String script, Object... args) {
    return ((JavascriptExecutor) driver).executeScript(script, args);
  }

}