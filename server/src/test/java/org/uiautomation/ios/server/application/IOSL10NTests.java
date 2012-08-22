package org.uiautomation.ios.server.application;

import static org.uiautomation.ios.server.application.Localizable.de;
import static org.uiautomation.ios.server.application.Localizable.en;
import static org.uiautomation.ios.server.application.Localizable.es;
import static org.uiautomation.ios.server.application.Localizable.fr;
import static org.uiautomation.ios.server.application.Localizable.zh;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.exceptions.IOSAutomationException;
import org.uiautomation.ios.server.application.ContentResult;
import org.uiautomation.ios.server.application.IOSApplication;
import org.uiautomation.ios.server.application.Localizable;
import org.uiautomation.ios.server.tests.SampleApps;

public class IOSL10NTests {

  private IOSApplication app;

  @BeforeClass
  public void setup() throws Exception {

    app = new IOSApplication(en, SampleApps.getIntlMountainsApp());
    app.loadAllContent();
  }



  @Test
  public void noVariable() {
    String content = "Mountain Detail";

    List<ContentResult> results = app.getDictionary(en).getPotentialMatches(content);

    Assert.assertEquals(results.size(), 1);

    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 0);

    String french = app.translate(res, fr);
    Assert.assertEquals(french, "Détail de Montagne");

  }

  @Test
  public void matchesPatternStart() throws IOSAutomationException {
    // %@ items found in %@
    String content = "42 feet";
    List<ContentResult> results = app.getDictionary(en).getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    Assert.assertEquals(res.getArgs().size(), 1);
    Assert.assertEquals(res.getArgs().get(0), "42");

    String french = app.getDictionary(fr).translate(res);
    Assert.assertEquals(french, "42 pieds");

  }

  @Test
  public void notFound() throws IOSAutomationException {
    String content = "ferrets are great pets";
    List<ContentResult> results = app.getDictionary(en).getPotentialMatches(content);
    Assert.assertEquals(results.size(), 0);
  }

  @Test(expectedExceptions = {IOSAutomationException.class})
  public void languageNotFound() throws IOSAutomationException {
    String content = "42 feet";
    List<ContentResult> results = app.getDictionary(en).getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);

    app.translate(results.get(0), Localizable.es);
  }

  @Test
  public void complex() throws IOSAutomationException {
    // %@ items found in %@
    String content = "Mountain 1 was first climbed on 29 May 1953 and has a height of 8,848 meters";
    List<ContentResult> results = app.getDictionary(en).getPotentialMatches(content);
    Assert.assertEquals(results.size(), 1);
    ContentResult res = results.get(0);
    System.out.println(res);
    String french = app.getDictionary(fr).translate(res);
   // returns "Bien que Mountain 1 de haut, 29 May 1953 aient été montés la première fois 8,848 meters."
  }
  
 


}
