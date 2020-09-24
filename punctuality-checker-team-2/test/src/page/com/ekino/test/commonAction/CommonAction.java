package com.ekino.test.commonAction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class CommonAction {

	private String url = "https://web.t2.pc.ekinoffy.com/";

	public ChromeOptions setParam() {
		ChromeOptions options = new ChromeOptions();
		options.setCapability("chrome.switches", "--ignore-ssl-errors=yes");
		options.addArguments("--disable-extensions");
		options.addArguments("--start-maximized");
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--allow-insecure-localhost=yes");
		options.addArguments("--ignore-urlfetcher-cert-requests=yes");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-blink-features");
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments(
				"--user-agent=Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 640 XL LTE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Mobile Safari/537.36 Edge/12.10166");
		options.setExperimentalOption("useAutomationExtension", false);
		options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_setting_values.notifications", 2);
		prefs.put("credentials_enable_service", false);
		prefs.put("download.default_directory", System.getProperty("user.dir") + "\\download");
		options.setExperimentalOption("prefs", prefs);
		options.addArguments("--test-type");
		options.setCapability("goog:chromeOptions", options);
		options.setCapability("acceptSslCerts", true);
		options.setCapability("unexpectedAlertBehaviour", "ignore");
		options.setCapability("browserstack.debug", true);
		options.setCapability("unhandledPromptBehavior", "ignore");

		return options;
	}

	public CommonAction openPage(WebDriver driver) {
		driver.get(url);
		return this;
	}

	public void hover(WebElement element, WebDriver driver) {
		Actions action = new Actions(driver);
		action.moveToElement(element).perform();
	}
	
	public void scrollIntoViewIfNeeded(WebElement element, WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", element);
	}

	public void assertTrue(boolean conditional, String messagePass, String messageFail) {
		Assert.assertTrue(conditional, messageFail);
		if (conditional) {
			System.out.println(messagePass);
		}
	}

	public void assertEqual(String actual, String expected, String messagePass, String messageFail) {
		Assert.assertEquals(actual, expected, messageFail);
		if (actual.contentEquals(expected)) {
			System.out.println(messagePass);
		}
	}
}
