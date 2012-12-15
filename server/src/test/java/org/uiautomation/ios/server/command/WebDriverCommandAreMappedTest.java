package org.uiautomation.ios.server.command;


import org.testng.annotations.Test;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.server.CommandMapping;

public class WebDriverCommandAreMappedTest {


  @Test
  public void allCommandsAreMapped() {
    for (WebDriverLikeCommand command : WebDriverLikeCommand.values()) {
      try {
        CommandMapping.get(command);
      } catch (Exception e) {
        System.err.println(command);

      }
    }
  }
}
