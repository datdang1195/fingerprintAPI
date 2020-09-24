package com.ekino.page.rangePage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ekino.test.commonAction.CommonAction;
import com.ekino.test.commonAction.NavBarComponent;

public class RangePage {

	private WebDriver driverRunning;

	private WebElement img_ekinoLogo;

	private WebElement btn_dailyPage;

	private WebElement btn_rangePage;

	private WebElement btn_employeePage;

	private WebElement blk_rangeContent;

	private WebElement btn_selectDate;

	private WebElement btn_goSelectDate;

	private WebElement input_nameSearch;

	private WebElement btn_signOut;

	private WebElement btn_export;

	public RangePage(WebDriver driver) {
		driverRunning = driver;

		img_ekinoLogo = driver.findElement(By.xpath("//*[@class='main100-form-title']"));

		btn_dailyPage = driver.findElement(By.xpath("//*[contains(@class,'daily ')]"));

		btn_rangePage = driver.findElement(By.xpath("//*[contains(@class,'range ')]"));

		btn_employeePage = driver.findElement(By.xpath("//*[contains(@class,'employees ')]"));

		blk_rangeContent = driver.findElement(By.id("range"));

		btn_selectDate = driver.findElement(By.id("reportrange"));

		btn_goSelectDate = driver.findElement(By.id("js-range-button"));

		input_nameSearch = driver.findElement(By.id("search-range"));

		btn_signOut = driver.findElement(By.xpath("//*[contains(@class,'desktop')]//*[@id = 'signOut']"));

		btn_export = driver.findElement(By.id("js-range-export"));

	}

	public RangePage verifyUrl() {
		String url = driverRunning.getCurrentUrl();
		new CommonAction().assertTrue(url.contains("main.html"), "Daily Page url is corect",
				"Daily Page url is incorect " + url);
		return this;
	}

	public RangePage verifyRangePage() {
		new CommonAction().assertTrue(img_ekinoLogo.isDisplayed(), "Ekino Logo is present",
				"Ekino Logo is not present");
		new CommonAction().assertTrue(btn_dailyPage.isDisplayed(), "Button daily page is present",
				"Button daily page is not present");
		new CommonAction().assertTrue(btn_rangePage.isDisplayed(), "Button range page is present",
				"Button range page is not present");
		new CommonAction().assertTrue(btn_employeePage.isDisplayed(), "Button employee page is present",
				"Button employee page is not present");
		new CommonAction().assertTrue(blk_rangeContent.isDisplayed(), "Daily Content is present",
				"Daily Content is not present");
		new CommonAction().assertTrue(btn_selectDate.isDisplayed(), "Button select date is present",
				"Button select date is not present");
		new CommonAction().assertTrue(btn_goSelectDate.isDisplayed(), "Button Go is present",
				"Button Go is not present");
		new CommonAction().assertTrue(input_nameSearch.isDisplayed(), "Name search field is present",
				"Name search field is not present");
		new CommonAction().assertTrue(btn_signOut.isDisplayed(), "Button sign out is present",
				"Button sign out is not present");
		new CommonAction().assertTrue(btn_export.isDisplayed(), "Button range export is present",
				"Button range export is not present");
		return this;
	}

	public RangePage filterByName() {
		String name = "Dion Runte";
		boolean filter = true;
		input_nameSearch.sendKeys(name);
		driverRunning.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		List<WebElement> lbl_numberOfRow = driverRunning.findElements(By.xpath("//*[@class='range range_name']"));
		int numberOfRow = lbl_numberOfRow.size();
		if (numberOfRow < 25) {
			List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='range range_name']"));
			for (int j = 0; j < lbl_status.size(); j++) {
				if (!lbl_status.get(j).getText().contains(name)) {
					filter = false;
					break;
				}
			}
		} else {
			int numberOfPage = new NavBarComponent(driverRunning).getNumberOfPage();
			for (int i = 0; i < numberOfPage; i++) {
				List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='range range_name']"));
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

	public RangePage selectLastMonth() throws InterruptedException {
		btn_selectDate.click();
		Thread.sleep(2000);
		WebElement lnk_rangeLastMonth = driverRunning.findElement(By.xpath("//*[@data-range-key='Last Month']"));
		lnk_rangeLastMonth.click();
		Thread.sleep(2000);
		btn_goSelectDate.click();
		Thread.sleep(15000);

		String present = driverRunning.findElement(By.xpath("//*[@class='range range_present']")).getText();
		String dayOff = driverRunning.findElement(By.xpath("//*[@class='range range_dayoff']")).getText();
		float presentDate = Float.parseFloat(present);
		float dayOffDate = Float.parseFloat(dayOff);
		new CommonAction().assertTrue(23 == dayOffDate + presentDate, "Filter is correct", "Filter is not correct");
		return this;
	}

	public RangePage selectSelectCustomRange(String DateIn, String DateOut)
			throws InterruptedException, ParseException {
		btn_selectDate.click();
		Thread.sleep(2000);
		WebElement lnk_rangeCustom = driverRunning.findElement(By.xpath("//*[@data-range-key='Custom Range']"));
		lnk_rangeCustom.click();
		Thread.sleep(2000);

		Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(DateIn);
		SimpleDateFormat formatterStart = new SimpleDateFormat("MM/dd/yyyy");
		String fromDate = formatterStart.format(startDate);

		Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(DateOut);
		SimpleDateFormat formatterEnd = new SimpleDateFormat("MM/dd/yyyy");
		String toDate = formatterEnd.format(endDate);

		driverRunning
				.findElement(By.xpath("//*[@class='calendar left']//*[contains(@class,'input-mini form-control')]"))
				.clear();
		driverRunning
				.findElement(By.xpath("//*[@class='calendar left']//*[contains(@class,'input-mini form-control')]"))
				.sendKeys(fromDate);
		driverRunning
				.findElement(By.xpath("//*[@class='calendar right']//*[contains(@class,'input-mini form-control')]"))
				.clear();
		driverRunning
				.findElement(By.xpath("//*[@class='calendar right']//*[contains(@class,'input-mini form-control')]"))
				.sendKeys(toDate);
		driverRunning.findElement(By.xpath("//*[@class='applyBtn btn btn-sm btn-success']")).click();

		btn_goSelectDate.click();
		Thread.sleep(15000);
		float numberOfWorkingDay = numberOfWorkingDay(DateIn, DateOut);

		String present = driverRunning.findElement(By.xpath("//*[@class='range range_present']")).getText();
		String dayOff = driverRunning.findElement(By.xpath("//*[@class='range range_dayoff']")).getText();
		float presentDate = Float.parseFloat(present);
		float dayOffDate = Float.parseFloat(dayOff);
		new CommonAction().assertTrue(numberOfWorkingDay == dayOffDate + presentDate, "Filter is correct",
				"Filter is not correct");
		return this;
	}

	public float numberOfWorkingDay(String DateIn, String DateOut) throws ParseException {
		String DateInDate = DateIn.substring(0, 2);
		String DateInMonth = DateIn.substring(3, 5);
		String DateOutDate = DateOut.substring(0, 2);
		String DateOutMonth = DateOut.substring(3, 5);
		int expectedDateInDate = Integer.valueOf(DateInDate);
		int expectedDateInMonth = Integer.valueOf(DateInMonth);
		int expectedDateOutDate = Integer.valueOf(DateOutDate);
		int expectedDateOutMonth = Integer.valueOf(DateOutMonth);
		float numberOfWorkingDay = 0;

		if (expectedDateOutMonth > expectedDateInMonth) {
			Calendar cal = Calendar.getInstance();
			int lastDateDateInMonth = cal.getActualMaximum(expectedDateInMonth);
			numberOfWorkingDay = lastDateDateInMonth - expectedDateInDate + expectedDateOutDate + 1;
		} else {
			numberOfWorkingDay = expectedDateOutDate - expectedDateInDate + 1;
		}

		int weekend = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String newDate = DateIn;
		for (int i = 0; i < numberOfWorkingDay; i++) {
			Date date1 = sdf.parse(newDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date1);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == 1 || dayOfWeek == 7) {
				weekend++;
			}
			c.add(Calendar.DATE, 1);
			newDate = sdf.format(c.getTime());
		}

		numberOfWorkingDay = numberOfWorkingDay - weekend;

		return numberOfWorkingDay;
	}

}
