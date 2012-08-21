package org.uiautomation.ios.e2e.uicatalogapp;

import org.testng.annotations.Test;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIATarget;

public class LocalTargetTest extends UICatalogTestsBase {

	@Test
	public void accessLocalTarget() throws InterruptedException {
		RemoteUIADriver driver = null;
		try {
			driver = getDriver();
			RemoteUIATarget target = driver.getLocalTarget();
			target.tap(50, 100);
			// TODO freynaud add validation...

		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}

	@Test
	public void tapByPosition() throws InterruptedException {
		RemoteUIADriver driver = null;
		try {
			driver = getDriver();
			RemoteUIATarget target = driver.getLocalTarget();
			target.tap(50, 100);
		
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
}
