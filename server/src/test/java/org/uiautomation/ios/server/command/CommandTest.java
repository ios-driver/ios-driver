package org.uiautomation.ios.server.command;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class CommandTest {

  @Test
  public void finderIsRunning() {
    Assert.assertTrue(ClassicCommands.isRunning("Finder"));
  }

  @Test
  public void pipoNotRunning() {
    Assert.assertFalse(ClassicCommands.isRunning("pipo"));
  }

}
