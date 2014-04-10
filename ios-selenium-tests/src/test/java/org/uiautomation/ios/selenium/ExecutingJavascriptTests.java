package org.uiautomation.ios.selenium;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.uiautomation.ios.server.command.uiautomation.SetScriptTimeoutNHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@Test
public class ExecutingJavascriptTests extends BaseSeleniumTest {

  ////@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAString() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = executeScript("return document.title;");

    assertTrue(result instanceof String);
    assertEquals("XHTML Test Page", result);
  }

  ////@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAString() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.title);}, 1);");

    assertTrue(result instanceof String);
    assertEquals("XHTML Test Page", result);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnALong() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.nestedPage);

    Object result = executeScript("return document.getElementsByName('checky').length;");

    assertTrue(result instanceof Long, result.getClass().getName());
    assertTrue((Long) result > 1);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnALong() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.nestedPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.getElementsByName('checky').length);}, 1);");

    assertTrue(result instanceof Long, result.getClass().getName());
    assertTrue((Long) result > 1);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAWebElement() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = executeScript("return document.getElementById('id1');");

    assertNotNull(result);
    assertTrue(result instanceof WebElement);
    assertEquals("a", ((WebElement) result).getTagName().toLowerCase());
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAWebElement() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.getElementById('id1'));}, 1);");

    assertNotNull(result);
    assertTrue(result instanceof WebElement);
    assertEquals("a", ((WebElement) result).getTagName().toLowerCase());
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnABoolean() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = executeScript("return true;");

    assertNotNull(result);
    assertTrue(result instanceof Boolean);
    assertTrue((Boolean) result);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnABoolean() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(true);}, 1);");

    assertNotNull(result);
    assertTrue(result instanceof Boolean);
    assertTrue((Boolean) result);
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAStringsArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    List<Object> expectedResult = new ArrayList<Object>();
    expectedResult.add("zero");
    expectedResult.add("one");
    expectedResult.add("two");
    Object result = ((JavascriptExecutor) driver).executeScript("return ['zero', 'one', 'two'];");

    ExecutingJavascriptTests.compareLists(expectedResult, (List<Object>) result);
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAStringsArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    List<Object> expectedResult = new ArrayList<Object>();
    expectedResult.add("zero");
    expectedResult.add("one");
    expectedResult.add("two");
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(['zero', 'one', 'two']);}, 1);");

    ExecutingJavascriptTests.compareLists(expectedResult, (List<Object>) result);
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAnArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
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

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAnArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    List<Object> expectedResult = new ArrayList<Object>();
    expectedResult.add("zero");
    List<Object> subList = new ArrayList<Object>();
    subList.add(true);
    subList.add(false);
    expectedResult.add(subList);
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(['zero', [true, false]]);}, 1);");
    assertNotNull(result);
    assertTrue(result instanceof List, "result was: " + result + " (" + result.getClass() + ")");
    List<Object> list = (List<Object>) result;
    assertTrue(compareLists(expectedResult, list));
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteJavascriptAndReturnABasicObjectLiteral() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

    Object result = executeScript("return {abc: '123', tired: false};");
    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;

    Map<String, Object> expected = new HashMap<String, Object>();
    expected.put("abc", "123");
    expected.put("tired", false);

    // Cannot do an exact match; Firefox 4 inserts a few extra keys in our
    // object; this is OK, as
    // long as the expected keys are there.
    assertTrue(map.size() >= expected.size(), "Expected:<" + expected + ">, but was:<" + map + ">");
    for (Map.Entry<String, Object> entry : expected.entrySet()) {
      assertEquals(entry.getValue(), map.get(entry.getKey()),
                   "Difference at key:<" + entry.getKey() + ">");
    }
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnABasicObjectLiteral() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb({abc: '123', tired: false});}, 1);");
    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;

    Map<String, Object> expected = new HashMap<String, Object>();
    expected.put("abc", "123");
    expected.put("tired", false);

    // Cannot do an exact match; Firefox 4 inserts a few extra keys in our
    // object; this is OK, as
    // long as the expected keys are there.
    assertTrue(map.size() >= expected.size(), "Expected:<" + expected + ">, but was:<" + map + ">");
    for (Map.Entry<String, Object> entry : expected.entrySet()) {
      assertEquals(entry.getValue(), map.get(entry.getKey()),
                   "Difference at key:<" + entry.getKey() + ">");
    }
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAnObjectLiteral() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

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

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAnObjectLiteral() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

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
        ((JavascriptExecutor) driver).executeAsyncScript(
            "var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb({foo:'bar', baz: ['a', 'b', 'c'], person: {first: 'John',last: 'Doe'}});}, 1);");
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

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  //@Ignore({ IE, HTMLUNIT, OPERA, OPERA_MOBILE })
  @Test
  public void testShouldBeAbleToExecuteSimpleJavascriptAndReturnAComplexObject() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

    Object result = executeScript("return window.location;");

    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;
    assertEquals("http:", map.get("protocol"));
    assertEquals(pages.javascriptPage, map.get("href"));
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  //@Ignore({ IE, HTMLUNIT, OPERA, OPERA_MOBILE })
  @Test
  public void testShouldBeAbleToExecuteAsyncJavascriptAndReturnAComplexObject() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);

    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(window.location);}, 1);");

    assertTrue(result instanceof Map, "result was: " + result + " (" + result.getClass() + ")");
    Map<String, Object> map = (Map<String, Object>) result;
    assertEquals("http:", map.get("protocol"));
    assertEquals(pages.javascriptPage, map.get("href"));
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

  //@JavascriptEnabled
  @Test
  public void testPassingAndReturningALongShouldReturnAWholeNumber() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Long expectedResult = 1L;
    Object result = executeScript("return arguments[0];", expectedResult);
    assertTrue((result instanceof Integer || result instanceof Long),
               "Expected result to be an Integer or Long but was a " + result.getClass());
    assertEquals(expectedResult.longValue(), result);
  }

  //@JavascriptEnabled
  @Test
  public void testPassingAndReturningALongAsynchronouslyShouldReturnAWholeNumber() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Long expectedResult = 1L;
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result);}, 1);", expectedResult);
    assertTrue((result instanceof Integer || result instanceof Long),
               "Expected result to be an Integer or Long but was a " + result.getClass());
    assertEquals(expectedResult.longValue(), result);
  }

  //@JavascriptEnabled
  @Test
  public void testPassingAndReturningADoubleShouldReturnADecimal() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Double expectedResult = 1.2;
    Object result = executeScript("return arguments[0];", expectedResult);
    assertTrue(result instanceof Float || result instanceof Double,
               "Expected result to be a Double or Float but was a " + result.getClass());
    assertEquals(expectedResult.doubleValue(), result);
  }

  //@JavascriptEnabled
  @Test
  public void testPassingAndReturningADoubleAsynchronouslyShouldReturnADecimal() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Double expectedResult = 1.2;
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result);}, 1);", expectedResult);
    assertTrue(result instanceof Float || result instanceof Double,
               "Expected result to be a Double or Float but was a " + result.getClass());
    assertEquals(expectedResult.doubleValue(), result);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldThrowAnExceptionWhenTheJavascriptIsBad() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    try {
      executeScript("return squiggle();");
      fail("Expected an exception");
    } catch (Exception e) {
      // This is expected
      assertFalse(e.getMessage().startsWith("null "), e.getMessage());
    }
  }

  //@JavascriptEnabled
  @Test
  public void testShouldThrowAnExceptionWhenTheAsynchronousJavascriptIsBad() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.xhtmlTestPage);

    try {
      ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(squiggle());}, 1);");
      fail("Expected an exception");
    } catch (Exception e) {
      // This is expected
      assertFalse(e.getMessage().startsWith("null "), e.getMessage());
    }
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToCallFunctionsDefinedOnThePage() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    executeScript("displayMessage('I like cheese');");
    String text = driver.findElement(By.id("result")).getText();

    assertEquals("I like cheese", text.trim());
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyCallFunctionsDefinedOnThePage() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(displayMessage('I like cheese'));}, 1);");
    String text = driver.findElement(By.id("result")).getText();

    assertEquals("I like cheese", text.trim());
  }

  private Object executeScript(String script, Object... args) {
    return ((JavascriptExecutor) driver).executeScript(script, args);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassAStringAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    String
        value =
        (String) executeScript("return arguments[0] == 'fish' ? 'fish' : 'not fish';", "fish");

    assertEquals("fish", value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyPassAStringAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    String
        value =
        (String) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result == 'fish' ? 'fish' : 'not fish');}, 1);", "fish");

    assertEquals("fish", value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassABooleanAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    boolean value = (Boolean) executeScript("return arguments[0] == true;", true);

    assertTrue(value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyPassABooleanAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    boolean value = (Boolean) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result == true);}, 1);", true);

    assertTrue(value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassANumberAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    boolean value = (Boolean) executeScript("return arguments[0] == 1 ? true : false;", 1);

    assertTrue(value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyPassANumberAnAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    boolean value = (Boolean) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result == 1 ? true : false);}, 1);", 1);

    assertTrue(value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassAWebElementAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    WebElement button = driver.findElement(By.id("plainButton"));
    String value = (String) executeScript(
        "arguments[0]['flibble'] = arguments[0].getAttribute('id'); return arguments[0]['flibble'];",
        button);

    assertEquals("plainButton", value);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyPassAWebElementAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    WebElement button = driver.findElement(By.id("plainButton"));
    String value = (String) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; result['flibble'] = result.getAttribute('id'); window.setTimeout(function(){cb(result['flibble']);}, 1);", button);

    assertEquals("plainButton", value);
  }

  //@JavascriptEnabled
  @Test
  public void testPassingArrayAsOnlyArgumentFlattensArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    String value = (String) executeScript("return arguments[0]", array);
    assertEquals(array[0], value);
  }

  //@JavascriptEnabled
  @Test
  public void testPassingArrayAsOnlyArgumentAsynchronouslyFlattensArray() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    String value = (String) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result);}, 1);", array);
    assertEquals(array[0], value);
  }

  //@JavascriptEnabled
  //@Ignore({ OPERA, OPERA_MOBILE })
  @Test(enabled = false)
  public void testShouldBeAbleToPassAnArrayAsAdditionalArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    long length = (Long) executeScript("return arguments[1].length", "string", array);
    assertEquals(array.length, length);
  }

  //@JavascriptEnabled
  //@Ignore({ OPERA, OPERA_MOBILE })
  @Test(enabled = false)
  public void testShouldBeAbleToPassAnArrayAsAdditionalArgumentAsynchronously() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Object[] array = new Object[]{"zero", 1, true, 3.14159, false};
    long length = (Long) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[1], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result.length);}, 1);", "string", array);
    assertEquals(array.length, length);
  }

  //@JavascriptEnabled
  @Test(enabled = false)
  // TODO
  public void testShouldBeAbleToPassACollectionAsArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
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

  //@JavascriptEnabled
  @Test(enabled = false)
  // TODO
  public void testShouldBeAbleToPassACollectionAsArgumentAsynchronously() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    Collection<Object> collection = new ArrayList<Object>();
    collection.add("Cheddar");
    collection.add("Brie");
    collection.add(7);
    long length = (Long) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result.length);}, 1);", collection);
    assertEquals(collection.size(), length);

    collection = new HashSet<Object>();
    collection.add("Gouda");
    collection.add("Stilton");
    collection.add("Stilton");
    collection.add(true);
    length = (Long) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result.length);}, 1);", collection);
    assertEquals(collection.size(), length);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldThrowAnExceptionIfAnArgumentIsNotValid() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    try {
      executeScript("return arguments[0];", driver);
      fail("Exception should have been thrown");
    } catch (IllegalArgumentException e) {
      // this is expected
    }
  }

  //@JavascriptEnabled
  @Test
  public void testShouldThrowAnExceptionIfAnAsynchronousArgumentIsNotValid() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    try {
      ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result);}, 1);", driver);
      fail("Exception should have been thrown");
    } catch (IllegalArgumentException e) {
      // this is expected
    }
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassInMoreThanOneArgument() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    String result = (String) executeScript("return arguments[0] + arguments[1];", "one", "two");

    assertEquals("onetwo", result);
  }

  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToPassInMoreThanOneArgumentAsynchronously() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.get(pages.javascriptPage);
    String result = (String) ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], result2 = arguments[1], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result + result2);}, 1);", "one", "two");

    assertEquals("onetwo", result);
  }

  //@JavascriptEnabled
  @Test
  //@Ignore({ OPERA, OPERA_MOBILE })
  public void testShouldBeAbleToGrabTheBodyOfFrameOnceSwitchedTo() {
    driver.get(pages.richTextPage);

    driver.switchTo().frame("editFrame");
    WebElement
        body =
        (WebElement) ((JavascriptExecutor) driver).executeScript("return document.body");
    String text = body.getText();
    driver.switchTo().defaultContent();

    assertEquals("", text);
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToReturnAnArrayOfWebElements() {
    driver.get(pages.formPage);

    List<WebElement> items = (List<WebElement>) ((JavascriptExecutor) driver)
        .executeScript("return document.getElementsByName('snack');");

    assertFalse(items.isEmpty());
  }

  @SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToReturnAnArrayOfWebElementsAsynchronously() {
    driver.get(pages.formPage);

    List<WebElement> items = (List<WebElement>) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.getElementsByName('snack'));}, 1);");

    assertFalse(items.isEmpty());
  }

  //@JavascriptEnabled
  @Test
  public void testJavascriptStringHandlingShouldWorkAsExpected() {
    driver.get(pages.javascriptPage);

    String value = (String) executeScript("return '';");
    assertEquals("", value);

    value = (String) executeScript("return undefined;");
    assertNull(value);

    value = (String) executeScript("return ' '");
    assertEquals(" ", value);
  }

  //@JavascriptEnabled
  @Test
  public void testJavascriptStringHandlingShouldWorkAsExpectedAsynchronously() {
    driver.get(pages.javascriptPage);

    String value = (String) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb('');}, 1);");
    assertEquals("", value);

    value = (String) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(undefined);}, 1);");
    assertNull(value);

    value = (String) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(' ');}, 1);");
    assertEquals(" ", value);
  }

  //@JavascriptEnabled
  //@Ignore(OPERA)
  @Test(enabled = false)
  public void testShouldBeAbleToExecuteABigChunkOfJavascriptCode() throws IOException {
    driver.get(pages.javascriptPage);

    //File jqueryFile = InProject.locate("common/src/web/jquery-1.3.2.js");
    //String jquery = Files.toString(jqueryFile, Charset.forName("US-ASCII"));
    //assertTrue(jquery.length() > 50000, "The javascript code should be at least 50 KB.");
    // This should not throw an exception ...
    //executeScript(jquery);
  }

  //@SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToExecuteScriptAndReturnElementsList() {
    driver.get(pages.formPage);
    String scriptToExec = "return document.getElementsByName('snack');";

    List<WebElement>
        resultsList =
        (List<WebElement>) ((JavascriptExecutor) driver).executeScript(scriptToExec);

    assertFalse(resultsList.isEmpty());
  }

  //@SuppressWarnings("unchecked")
  //@JavascriptEnabled
  @Test
  public void testShouldBeAbleToAsynchronouslyExecuteScriptAndReturnElementsList() {
    driver.get(pages.formPage);

    List<WebElement>
        resultsList =
        (List<WebElement>) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.getElementsByName('snack'));}, 1);");

    assertFalse(resultsList.isEmpty());
  }

  //@JavascriptEnabled
  //@NeedsFreshDriver
  //@NoDriverAfterTest
  //@Ignore(reason = "Failure indicates hang condition, which would break the"
  //    + " test suite. Really needs a timeout set.")
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

  //@JavascriptEnabled
  //@NeedsFreshDriver
  //@NoDriverAfterTest
  //@Ignore(reason = "Failure indicates hang condition, which would break the"
  //    + " test suite. Really needs a timeout set.")
  @Test(enabled = false)
  public void testShouldThrowExceptionIfAsynchronouslyExecutingOnNoPage() {
    try {
      ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(1);}, 1);");
    } catch (WebDriverException e) {
      // Expected
      return;
    }
    fail("Expected exception to be thrown");
  }

  //@JavascriptEnabled
  //@Ignore(OPERA)
  @Test
  public void testShouldBeAbleToCreateAPersistentValue() {
    driver.get(pages.formPage);

    executeScript("document.alerts = []");
    executeScript("document.alerts.push('hello world');");
    String text = (String) executeScript("return document.alerts.shift()");

    assertEquals("hello world", text);
  }

  //@JavascriptEnabled
  //@Ignore(OPERA)
  @Test
  public void testShouldBeAbleToCreateAPersistentValueAsynchronously() {
    driver.get(pages.formPage);

    ((JavascriptExecutor) driver).executeAsyncScript("document.alerts = []; arguments[arguments.length - 1]();");
    ((JavascriptExecutor) driver).executeAsyncScript("document.alerts.push('hello world'); arguments[arguments.length - 1]();");
    String text = (String) ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(document.alerts.shift());}, 1);");

    assertEquals("hello world", text);
  }

  //@JavascriptEnabled
  //@Ignore(OPERA)
  @Test(enabled = false)
  // freynaud TODO
  public void testCanHandleAnArrayOfElementsAsAnObjectArray() {
    driver.get(pages.formPage);

    List<WebElement> forms = driver.findElements(By.tagName("form"));
    Object[] args = new Object[]{forms};

    String
        name =
        (String) ((JavascriptExecutor) driver)
            .executeScript("return arguments[0][0].tagName", args);

    assertEquals("form", name.toLowerCase());
  }

  //@JavascriptEnabled
  //@Ignore(OPERA)
  @Test(enabled = false)
  // freynaud TODO
  public void testCanHandleAnArrayOfElementsAsAnObjectArrayAsynchronously() {
    driver.get(pages.formPage);

    List<WebElement> forms = driver.findElements(By.tagName("form"));
    Object[] args = new Object[]{forms};

    String
        name =
        (String) ((JavascriptExecutor) driver)
            .executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result[0].tagName);}, 1);", args);

    assertEquals("form", name.toLowerCase());
  }

  //@JavascriptEnabled
  //@Ignore(value = { ANDROID, HTMLUNIT, OPERA, OPERA_MOBILE }, reason = "Opera and HtmlUnit obey the method contract. Android not tested")
  @Test(enabled = false)
  // TODO freynaud
  public void testCanPassAMapAsAParameter() {
    driver.get(pages.simpleTestPage);

    List<Integer> nums = ImmutableList.of(1, 2);
    Map<String, Object> args = ImmutableMap.of("bar", "test", "foo", nums);

    Object res = ((JavascriptExecutor) driver).executeScript("return arguments[0]['foo'][1]", args);

    assertEquals(2, ((Number) res).intValue());
  }

  //@JavascriptEnabled
  //@Ignore(value = { ANDROID, HTMLUNIT, OPERA, OPERA_MOBILE }, reason = "Opera and HtmlUnit obey the method contract. Android not tested")
  @Test(enabled = false)
  // TODO freynaud
  public void testCanPassAMapAsAParameterAsynchronously() {
    driver.get(pages.simpleTestPage);

    List<Integer> nums = ImmutableList.of(1, 2);
    Map<String, Object> args = ImmutableMap.of("bar", "test", "foo", nums);

    Object res = ((JavascriptExecutor) driver).executeAsyncScript("var result = arguments[0], cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb(result['foo'][1]);}, 1);", args);

    assertEquals(2, ((Number) res).intValue());
  }

  @Test
  public void testCanAccessDocumentAndWindowObjectForFrames() {
    driver.get(pages.framesetPage);
    driver.switchTo().frame("third");

    String actualFrameTitle = "We Leave From Here";

    assertEquals(actualFrameTitle, executeScript("return document.title;"));
    assertEquals(actualFrameTitle, executeScript("return window.document.title;"));
    // We used to replace " document" in script - ensure we properly inject global scope, rather than performing string
    // replacement.
    assertEquals(actualFrameTitle, executeScript("return (document.title);"));
    assertEquals(actualFrameTitle, executeScript("return (window.document.title);"));
  }

  @Test
  public void testCanSetScriptTimeout() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.manage().timeouts().setScriptTimeout(10, TimeUnit.MILLISECONDS);
    assertEquals((int) SetScriptTimeoutNHandler.TIMEOUT, 10);
  }

  @Test
  public void testAsyncScriptsWillTimeoutAndFail() {
    if (!(driver instanceof JavascriptExecutor)) {
      return;
    }

    driver.manage().timeouts().setScriptTimeout(10, TimeUnit.MILLISECONDS);

    driver.get(pages.xhtmlTestPage);

    try {
      // For some reason, setting the setTimeout to a shorter time of say, 50ms, although still greater then the 10ms specified above, does not trigger a timeout.
      // It probably has to do with how often the timeout checks are all performed but it's worth noting that the timeout is not super strict, but just something to fall back
      // on so the user doesn't get trapped in a non-callback-calling async script execution. I don't know if there is any reason for it to be more strict in this context.
      // - samuel
      ((JavascriptExecutor) driver).executeAsyncScript("var cb = arguments[arguments.length - 1]; window.setTimeout(function(){cb();}, 100);");
      fail("Expected an exception");
    } catch (Exception e) {
      // This is expected
      assertFalse(e.getMessage().startsWith("null "), e.getMessage());
    }
  }
}
