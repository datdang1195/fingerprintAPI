package com.ekino.page.loginPage;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ekino.test.commonAction.CommonAction;

public class LoginPage {
	
	private WebDriver driverRunning;
	
	private WebElement img_ekinoLogo ;
	
	private WebElement btn_loginChrome;
	
	private WebElement lbl_welcome;

	public LoginPage(WebDriver driver) {
		
		driverRunning = driver;
		
		img_ekinoLogo = driver.findElement(By.xpath("//*[contains(@class,'login100-form-title')]"));
		
		btn_loginChrome = driver.findElement(By.xpath("//*[@class='g-signin2']"));
		
		lbl_welcome = driver.findElement(By.xpath("//*[contains(@class,'text-center')]"));
		
	}
	
	public LoginPage verifyEkinoLogoIsPresent() {
		new CommonAction().assertTrue(img_ekinoLogo.isDisplayed(), "Ekino Logo is present", "Ekino Logo is not present");
		return this;
	}
	
	public LoginPage verifyButtonLoginChromeIsPresent() {
		new CommonAction().assertTrue(btn_loginChrome.isDisplayed(), "Button Login is present", "Button Login is not present");
		return this;
	}
	
	public LoginPage verifyWelcomeMessIsPresent() {
		new CommonAction().assertTrue(lbl_welcome.isDisplayed(), "Welcome Mess is present", "Welcome Mess is not present");
		return this;
	}
	
	public LoginPage verifyWelcomeMessIsCorect() {
		new CommonAction().assertTrue(lbl_welcome.getText().contains("WELCOME"), "Welcome Mess is corect", "Welcome Mess is incorect");
		return this;
	}

	public LoginPage verifyLoginButtonColor() {
		WebElement btn_loginChromeColor = driverRunning.findElement(By.xpath("//*[@class='g-signin2']/div"));
		String colorBeforeHover = btn_loginChromeColor.getCssValue("background-color");
		new CommonAction().hover(btn_loginChrome, driverRunning);
		driverRunning.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		String colorAfterHover = btn_loginChromeColor.getCssValue("background-color");
		boolean condition = colorBeforeHover.contentEquals(colorAfterHover);
		new CommonAction().assertTrue(!condition, "Button color is corect", "Button color is incorect");
		return this;
	}

	public LoginPage loginWithEmail() throws InterruptedException {
		String winHandleBefore = driverRunning.getWindowHandle();
		btn_loginChrome.click();
		// Switch to new window opened
		for(String winHandle : driverRunning.getWindowHandles()){
			if (!winHandle.equals(winHandleBefore)) {
				driverRunning.switchTo().window(winHandle);
			}
		}
		new GoogleLoginFormEmail(driverRunning).inputValidEmail();
		new GoogleLoginFormPass(driverRunning).inputValidPassword();
		// Switch back to original browser
		driverRunning.switchTo().window(winHandleBefore);
		return this;
	}

	public LoginPage loginWithInalidEmail() throws InterruptedException {
		String winHandleBefore = driverRunning.getWindowHandle();
		btn_loginChrome.click();
		// Switch to new window opened
		for(String winHandle : driverRunning.getWindowHandles()){
			if (!winHandle.equals(winHandleBefore)) {
				driverRunning.switchTo().window(winHandle);
			}
		}
		new GoogleLoginFormEmail(driverRunning).inputInvalidEmail();
		new GoogleLoginFormPass(driverRunning).inputInvalidPassword();
		// Switch back to original browser
		driverRunning.switchTo().window(winHandleBefore);
		return this;
		
	}

}
