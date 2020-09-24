package com.ekino.page.loginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GoogleLoginFormEmail {

	private WebDriver driverRunning;

	private WebElement input_email;

	private WebElement btn_next;
	
	public GoogleLoginFormEmail(WebDriver driver) {

		driverRunning = driver;

		input_email = driver.findElement(By.id("identifierId"));

		btn_next = driverRunning.findElement(By.xpath("//*[@type='button']"));
	}
	
	public GoogleLoginFormEmail inputValidEmail() throws InterruptedException {
		input_email.clear();
		input_email.sendKeys("auto.testaccor2020@gmail.com");
		Thread.sleep(5000);
//		btn_next.click();
		
//		driverRunning.findElement(By.xpath("(//*[@role='button'])[2]")).sendKeys(Keys.ENTER);
//		btn_next.click();
		
		return this;
	}
	
	public GoogleLoginFormEmail inputInvalidEmail() throws InterruptedException {
		input_email.clear();
		input_email.sendKeys("apcinvalid08122020@gmail.com");
		Thread.sleep(5000);
//		btn_next.click();
		return this;
	}
}
