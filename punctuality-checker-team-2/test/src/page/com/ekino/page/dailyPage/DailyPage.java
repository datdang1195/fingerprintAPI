package com.ekino.page.dailyPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.ekino.test.commonAction.CommonAction;
import com.ekino.test.commonAction.NavBarComponent;

public class DailyPage {

	private WebDriver driverRunning;

	private WebElement img_ekinoLogo;

	private WebElement btn_dailyPage;

	private WebElement btn_rangePage;

	private WebElement btn_employeePage;

	private WebElement blk_dailyContent;

	private WebElement btn_selectDate;

	private WebElement btn_goSelectDate;

	private WebElement input_nameSearch;

	private WebElement select_status;

	private WebElement btn_signOut;

	private WebElement btn_export;

	public DailyPage(WebDriver driver) {
		driverRunning = driver;

		img_ekinoLogo = driver.findElement(By.xpath("//*[@class='main100-form-title']"));

		btn_dailyPage = driver.findElement(By.xpath("//*[contains(@class,'daily ')]"));

		btn_rangePage = driver.findElement(By.xpath("//*[contains(@class,'range ')]"));

		btn_employeePage = driver.findElement(By.xpath("//*[contains(@class,'employees ')]"));

		blk_dailyContent = driver.findElement(By.id("daily"));

		btn_selectDate = driver.findElement(By.id("datepicker-selected-date"));

		btn_goSelectDate = driver.findElement(By.id("js-daily-button"));

		input_nameSearch = driver.findElement(By.id("daily-search"));

		select_status = driver.findElement(By.id("table_select_option"));

		btn_signOut = driver.findElement(By.xpath("//*[contains(@class,'desktop')]//*[@id = 'signOut']"));

		btn_export = driver.findElement(By.id("js-daily-export"));

	}

	public DailyPage verifyUrl() {
		String url = driverRunning.getCurrentUrl();
		new CommonAction().assertTrue(url.contains("main.html"), "Daily Page url is corect",
				"Daily Page url is incorect " + url);
		return this;
	}

	public DailyPage verifyDailyPage() {
		new CommonAction().assertTrue(img_ekinoLogo.isDisplayed(), "Ekino Logo is present",
				"Ekino Logo is not present");
		new CommonAction().assertTrue(btn_dailyPage.isDisplayed(), "Button daily page is present",
				"Button daily page is not present");
		new CommonAction().assertTrue(btn_rangePage.isDisplayed(), "Button range page is present",
				"Button range page is not present");
		new CommonAction().assertTrue(btn_employeePage.isDisplayed(), "Button employee page is present",
				"Button employee page is not present");
		new CommonAction().assertTrue(blk_dailyContent.isDisplayed(), "Daily Content is present",
				"Daily Content is not present");
		new CommonAction().assertTrue(btn_selectDate.isDisplayed(), "Button select date is present",
				"Button select date is not present");
		new CommonAction().assertTrue(btn_goSelectDate.isDisplayed(), "Button Go is present",
				"Button Go is not present");
		new CommonAction().assertTrue(input_nameSearch.isDisplayed(), "Name search field is present",
				"Name search field is not present");
		new CommonAction().assertTrue(select_status.isDisplayed(), "Button select status is present",
				"Button select status is not present");
		new CommonAction().assertTrue(btn_signOut.isDisplayed(), "Button sign out is present",
				"Button sign out is not present");
		new CommonAction().assertTrue(btn_export.isDisplayed(), "Button daily export is present",
				"Button daily export is not present");
		return this;
	}

	public DailyPage selectDate(String Date) throws ParseException, InterruptedException {
		btn_selectDate.click();
		String actualDate = "", actualMonth = "", actualYear = "", month, year;
		int calendarMonth, expectedMonth, calendarYear, expectedYear;
		WebElement lbl_calendarMonthYear = driverRunning
				.findElement(By.xpath("//*[@class='datepicker-days']//*[@class='datepicker-switch']"));
		WebElement btn_next = driverRunning.findElement(By.xpath("//*[@class='datepicker-days']//*[@class='next']"));
		WebElement btn_prev = driverRunning.findElement(By.xpath("//*[@class='datepicker-days']//*[@class='prev']"));
		month = lbl_calendarMonthYear.getText().toUpperCase().replaceAll("\\d+", "");
		year = lbl_calendarMonthYear.getText().toUpperCase().replaceAll("\\D+", "");
		actualDate = Date.substring(0, 2);
		actualMonth = Date.substring(3, 5);
		actualYear = Date.substring(6, 10);
		calendarMonth = getMonthNumber(month);
		calendarYear = Integer.valueOf(year);
		expectedMonth = Integer.valueOf(actualMonth);
		expectedYear = Integer.valueOf(actualYear);
		if (expectedYear > calendarYear) {
			// open month pick calendar
			lbl_calendarMonthYear.click();
			// move next year
			btn_next.click();
			// select month
			WebElement monthSelect = driverRunning
					.findElement(By.xpath("(//span[contains(@class,'month')])[" + expectedMonth + "]"));
			monthSelect.click();
			// select date
			WebElement dateSelect = driverRunning
					.findElement(By.xpath("(//*[@class='datepicker-days']//td[@class='day'])[" + actualDate + "]"));
			dateSelect.click();
		} else {
			if (expectedYear < calendarYear) {
				// open month pick calendar
				lbl_calendarMonthYear.click();
				// move prev year
				btn_prev.click();
				// select month
				WebElement monthSelect = driverRunning
						.findElement(By.xpath("(//span[contains(@class,'month')])[" + expectedMonth + "]"));
				monthSelect.click();
				// select date
				WebElement dateSelect = driverRunning
						.findElement(By.xpath("(//*[@class='datepicker-days']//td[@class='day'])[" + actualDate + "]"));
				dateSelect.click();
			} else {
				if (expectedMonth > calendarMonth) {
					// move to expected month
					for (int i = 0; i < (expectedMonth - calendarMonth); i++) {
						btn_next.click();
					}
					// select date
					WebElement dateSelect = driverRunning.findElement(
							By.xpath("(//*[@class='datepicker-days']//td[@class='day'])[" + actualDate + "]"));
					dateSelect.click();
				} else {
					if (expectedMonth < calendarMonth) {
						// move to expected month
						for (int i = 0; i < (calendarMonth - expectedMonth); i++) {
							btn_prev.click();
						}
						// select date
						WebElement dateSelect = driverRunning.findElement(
								By.xpath("(//*[@class='datepicker-days']//td[@class='day'])[" + actualDate + "]"));
						dateSelect.click();
					} else {
						// select date
						WebElement dateSelect = driverRunning.findElement(
								By.xpath("//*[@class='datepicker-days']//td[contains(@class,'day')and text()='"
										+ actualDate + "')] "));
						dateSelect.click();
					}
				}
			}
		}
		btn_goSelectDate.click();
//		driverRunning.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Thread.sleep(5000);
		return this;
	}

	public static int getMonthNumber(String monthName) throws ParseException {
		Date dateIn;
		int monthNumber;
		Calendar calendar = Calendar.getInstance();
		dateIn = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthName);
		calendar.setTime(dateIn);
		monthNumber = calendar.get(Calendar.MONTH) + 1;
		return monthNumber;
	}

	public DailyPage filterByName() {
		String name = "Lavon Spinka";
		boolean filter = true;
		input_nameSearch.sendKeys(name);
		driverRunning.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		List<WebElement> lbl_numberOfRow = driverRunning.findElements(By.xpath("//*[@class='daily daily_name']"));
		int numberOfRow = lbl_numberOfRow.size();
		if (numberOfRow < 25) {
			List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='daily daily_name']"));
			for (int j = 0; j < lbl_status.size(); j++) {
				if (!lbl_status.get(j).getText().contains(name)) {
					filter = false;
					break;
				}
			}
		} else {
			int numberOfPage = new NavBarComponent(driverRunning).getNumberOfPage();
			for (int i = 0; i < numberOfPage; i++) {
				List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='daily daily_name']"));
				for (int j = 0; j < lbl_status.size(); j++) {
					if (!lbl_status.get(j).getText().contains(name)) {
						filter = false;
						break;
					}
				}
				if (!filter) {
					break;
				}
				new NavBarComponent(driverRunning).goNext();
			}
		}
		new CommonAction().assertTrue(filter, "Filter by name is correct", "Filter by name is not correct");
		return this;
	}

	public DailyPage filterByStatus() {
		String stringStatus = "A0P0";
		boolean filter = true;
		Select status = new Select(select_status);
		status.selectByVisibleText(stringStatus);
		List<WebElement> lbl_numberOfRow = driverRunning.findElements(By.xpath("//*[@class='daily daily_name']"));
		int numberOfRow = lbl_numberOfRow.size();
		if (numberOfRow < 25) {
			List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='daily daily_status']"));
			for (int j = 0; j < lbl_status.size(); j++) {
				if (!lbl_status.get(j).getText().contains(stringStatus)) {
					filter = false;
					break;
				}
			}
		} else {
			int numberOfPage = new NavBarComponent(driverRunning).getNumberOfPage();
			for (int i = 0; i < numberOfPage; i++) {
				List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='daily daily_status']"));
				for (int j = 0; j < lbl_status.size(); j++) {
					if (!lbl_status.get(j).getText().contains(stringStatus)) {
						filter = false;
						break;
					}
				}
				if (!filter) {
					break;
				}
				new NavBarComponent(driverRunning).goNext();
			}
		}
		new CommonAction().assertTrue(filter, "Filter by status is correct", "Filter by status is not correct");
		return this;
	}

	public DailyPage goToRangePage() {
		btn_rangePage.click();
		driverRunning.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		return this;
	}
	
	public DailyPage goToEmployeePage() {
		btn_employeePage.click();
		driverRunning.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		return this;
	}

}
