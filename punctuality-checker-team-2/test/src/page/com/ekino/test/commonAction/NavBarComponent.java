package com.ekino.test.commonAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NavBarComponent {

	private WebDriver driverRunning;

	private WebElement btn_first;

	private WebElement btn_last;

	private WebElement btn_next;

	private WebElement btn_pre;

	public NavBarComponent(WebDriver driver) {

		driverRunning = driver;

		btn_first = driver.findElement(
				By.xpath("//*[@id='pagination__wrapper__daily']//*[contains(@class,'page-items first')]/a"));

		btn_last = driver.findElement(
				By.xpath("//*[@id='pagination__wrapper__daily']//*[contains(@class,'page-items last')]/a"));

		btn_next = driver.findElement(
				By.xpath("//*[@id='pagination__wrapper__daily']//*[contains(@class,'page-items next')]/a"));

		btn_pre = driver.findElement(
				By.xpath("//*[@id='pagination__wrapper__daily']//*[contains(@class,'page-items prev')]/a"));

	}

	public int getNumberOfPage() {
		new CommonAction().scrollIntoViewIfNeeded(btn_last, driverRunning);
		btn_last.click();
		WebElement lastPage = driverRunning
				.findElement(By.xpath("//*[@id='pagination__wrapper__daily']//*[@class='page-item active']"));
		String lasPageNumber = lastPage.getText().trim();
		int numberOfPage = Integer.valueOf(lasPageNumber);
		btn_first = driverRunning.findElement(
				By.xpath("//*[@id='pagination__wrapper__daily']//*[contains(@class,'page-items first')]/a"));
		new CommonAction().scrollIntoViewIfNeeded(btn_first, driverRunning);
		btn_first.click();
		return numberOfPage;
	}

	public NavBarComponent goNext() {
		new CommonAction().scrollIntoViewIfNeeded(btn_next, driverRunning);
		btn_next.click();
		return this;
	}

	public NavBarComponent goPrev() {
		new CommonAction().scrollIntoViewIfNeeded(btn_pre, driverRunning);
		btn_pre.click();
		return this;
	}
}
