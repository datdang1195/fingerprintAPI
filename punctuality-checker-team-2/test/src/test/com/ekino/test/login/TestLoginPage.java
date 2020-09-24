package com.ekino.test.login;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ekino.page.dailyPage.DailyPage;
import com.ekino.page.loginPage.GoogleLoginFormEmail;
import com.ekino.page.loginPage.GoogleLoginFormPass;
import com.ekino.page.loginPage.LoginPage;
import com.ekino.test.commonAction.CommonAction;

public class TestLoginPage {

	private String url = "https://web.t2.pc.ekinoffy.com/";

	public TestLoginPage loginGoogle(WebDriver driver) throws InterruptedException {
		driver.get("http://gmail.com");
		new GoogleLoginFormEmail(driver).inputValidEmail();
		new GoogleLoginFormPass(driver).inputValidPassword();
		return this;
	}

	@BeforeClass
	public void beforeClass() {
		System.err.println("<===============================START===============================>");
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver.exe");
	}

	@AfterClass
	public void afterClass() {
		System.err.println("<===============================END===============================>");

	}

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("<===============================TEST START===============================>");

	}

	@AfterMethod
	public void afterMethod() {
		System.err.println("<===============================TEST END===============================>");
	}

	@Test
	public void testVerifyLoginPage() {
		ChromeOptions options = new CommonAction().setParam();
		WebDriver driver = new ChromeDriver(options);
		driver.get(url);
		new LoginPage(driver).verifyEkinoLogoIsPresent().verifyButtonLoginChromeIsPresent().verifyWelcomeMessIsPresent()
				.verifyWelcomeMessIsCorect().verifyLoginButtonColor();
		driver.quit();
	}

	@Test
	public void testLoginWithValidEmail() throws InterruptedException {
		ChromeOptions options = new CommonAction().setParam();
		WebDriver driver = new ChromeDriver(options);
		driver.get(url);
		new LoginPage(driver).verifyEkinoLogoIsPresent().verifyButtonLoginChromeIsPresent().verifyWelcomeMessIsPresent()
				.loginWithEmail();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		new DailyPage(driver).verifyUrl();
		driver.quit();
	}

	@Test
	public void testLoginWithInalidEmail() throws InterruptedException {
		ChromeOptions options = new CommonAction().setParam();
		WebDriver driver = new ChromeDriver(options);
		driver.get(url);
		new LoginPage(driver).verifyEkinoLogoIsPresent().verifyButtonLoginChromeIsPresent()
				.verifyWelcomeMessIsPresent().loginWithInalidEmail();
		driver.quit();
	}
}
