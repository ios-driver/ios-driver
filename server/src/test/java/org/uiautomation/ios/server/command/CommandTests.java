package org.uiautomation.ios.server.command;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.exceptions.IOSAutomationSetupException;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class CommandTests {


  @Test
  public void finderIsRunning() throws IOSAutomationSetupException {
    Assert.assertTrue(ClassicCommands.isRunning("Finder"));
  }

  @Test
  public void pipoNotRunning() throws IOSAutomationSetupException {
    Assert.assertFalse(ClassicCommands.isRunning("pipo"));
  }


}
