package org.uiautomation.ios.server.application;

import java.util.List;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.SampleApps;

public class IOSL10NTest {

  private IOSApplication app;

  @BeforeClass
  public void setup() throws Exception {

    app = new IOSApplication(SampleApps.getIntlMountainsFile());
    app.setLanguage("en");
  }

  @Test
  public void noVariable() {
    String content = "Mountain Detail";

    List<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);

    Assert.assertEquals(results.size(), 1);

    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 0);

    String french = app.translate(res, app.getAppleLocaleFromLanguageCode("fr"));
    Assert.assertEquals(french, "Détail de Montagne");

  }

  @Test
  public void matchesPatternStart() {
    // %@ items found in %@
    String content = "42 feet";
    List<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 1);
    Assert.assertEquals(res.getArgs().get(0), "42");

    String french = app.getDictionary("fr").translate(res);
    Assert.assertEquals(french, "42 pieds");

  }

  @Test
  public void notFound() {
    String content = "ferrets are great pets";
    List<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 0);
  }

  @Test(expectedExceptions = { WebDriverException.class })
  public void languageNotFound() {
    String content = "42 feet";
    List<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);

    app.translate(results.get(0), app.getAppleLocaleFromLanguageCode("es"));
  }

  @Test
  public void complex() {
    // %@ items found in %@
    String content = "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters";
    List<ContentResult> results = app.getDictionary("en").getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    String french = app.getDictionary("fr").translate(res);
    // returns
    // "Bien que Mountain 1 de haut, 29 May 1953 aient été montés la première fois 8,848 meters."
  }

}
