package com.ekino.page.employeePage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ekino.page.dailyPage.DailyPage;
import com.ekino.test.commonAction.CommonAction;
import com.ekino.test.commonAction.NavBarComponent;

public class EmployeePage {

	private WebDriver driverRunning;

	private WebElement img_ekinoLogo;

	private WebElement btn_dailyPage;

	private WebElement btn_rangePage;

	private WebElement btn_employeePage;

	private WebElement blk_employeeContent;

	private WebElement input_nameSearch;

	private WebElement btn_signOut;

	private WebElement btn_export;

	private WebElement btn_save;

	public EmployeePage(WebDriver driver) {
		driverRunning = driver;

		img_ekinoLogo = driver.findElement(By.xpath("//*[@class='main100-form-title']"));

		btn_dailyPage = driver.findElement(By.xpath("//*[contains(@class,'daily ')]"));

		btn_rangePage = driver.findElement(By.xpath("//*[contains(@class,'range ')]"));

		btn_employeePage = driver.findElement(By.xpath("//*[contains(@class,'employees ')]"));

		blk_employeeContent = driver.findElement(By.id("employees"));

		input_nameSearch = driver.findElement(By.id("search-employees"));

		btn_signOut = driver.findElement(By.xpath("//*[contains(@class,'desktop')]//*[@id = 'signOut']"));

		btn_export = driver.findElement(By.id("js-employees-export"));

		btn_save = driver.findElement(By.id("js-employees-save"));
	}

	public EmployeePage verifyEmployeePage() {
		new CommonAction().assertTrue(img_ekinoLogo.isDisplayed(), "Ekino Logo is present",
				"Ekino Logo is not present");
		new CommonAction().assertTrue(btn_dailyPage.isDisplayed(), "Button daily page is present",
				"Button daily page is not present");
		new CommonAction().assertTrue(btn_rangePage.isDisplayed(), "Button range page is present",
				"Button range page is not present");
		new CommonAction().assertTrue(btn_employeePage.isDisplayed(), "Button employee page is present",
				"Button employee page is not present");
		new CommonAction().assertTrue(blk_employeeContent.isDisplayed(), "Employee Content is present",
				"Daily Content is not present");
		new CommonAction().assertTrue(input_nameSearch.isDisplayed(), "Name search field is present",
				"Name search field is not present");
		new CommonAction().assertTrue(btn_signOut.isDisplayed(), "Button sign out is present",
				"Button sign out is not present");
		new CommonAction().assertTrue(btn_export.isDisplayed(), "Button employee export is present",
				"Button range export is not present");
		new CommonAction().assertTrue(btn_save.isDisplayed(), "Button employee save is present",
				"Button range export is not present");
		return this;
	}

	public EmployeePage Deactive() {
		boolean before = driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).isSelected();
		driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).click();

		driverRunning.navigate().refresh();
		new DailyPage(driverRunning).goToEmployeePage();

		boolean after = driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).isSelected();
		new CommonAction().assertTrue(before == after, "Deactive is not success", "Deactive is success");

		driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).click();
		after = driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).isSelected();

		driverRunning.findElement(By.id("js-employees-save")).click();
		driverRunning.navigate().refresh();
		new DailyPage(driverRunning).goToEmployeePage();

		after = driverRunning.findElement(By.xpath("//*[@class='employees_active_checked']")).isSelected();

		new CommonAction().assertTrue(before != after, "Deactive is success", "Deactive is not success");
		return this;
	}

	public EmployeePage filterByName() {
		String name = "Dion Runte";
		boolean filter = true;
		input_nameSearch.sendKeys(name);
		driverRunning.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		List<WebElement> lbl_numberOfRow = driverRunning.findElements(By.xpath("//*[@class='employee employee_name']"));
		int numberOfRow = lbl_numberOfRow.size();
		if (numberOfRow < 25) {
			List<WebElement> lbl_status = driverRunning.findElements(By.xpath("//*[@class='employee employee_name']"));
			for (int j = 0; j < lbl_status.size(); j++) {
				if (!lbl_status.get(j).getText().contains(name)) {
					filter = false;
					break;
				}
			}
		} else {
			int numberOfPage = new NavBarComponent(driverRunning).getNumberOfPage();
			for (int i = 0; i < numberOfPage; i++) {
				List<WebElement> lbl_status = driverRunning
						.findElements(By.xpath("//*[@class='employee employee_name']"));
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
}
