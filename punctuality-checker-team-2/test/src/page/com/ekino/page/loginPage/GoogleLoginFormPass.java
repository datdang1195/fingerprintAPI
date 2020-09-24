package com.ekino.page.loginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GoogleLoginFormPass {

	private WebDriver driverRunning;

	private WebElement input_password;

	private WebElement btn_next;
	
	public GoogleLoginFormPass(WebDriver driver) {

		driverRunning = driver;

		input_password = driverRunning.findElement(By.xpath("//*[@name='password']"));

		btn_next = driverRunning.findElement(By.xpath("(//*[@type='button'])[2]"));
	}
	
	public GoogleLoginFormPass inputValidPassword() {
		input_password.sendKeys("Ekino123!@#");
		btn_next.click();
		return this;
	}
	
	public GoogleLoginFormPass inputInvalidPassword() {
		input_password.sendKeys("Ekino123!@#");
		btn_next.click();
		return this;
	}
}
