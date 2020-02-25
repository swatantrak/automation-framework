package com.isometrix.selenium.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.isometrix.report.TestListener;
/*import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;*/
import com.relevantcodes.extentreports.LogStatus;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
	public String userName;
	public String password;
	public String browserType;
	public WebDriver driver;
	public String applicationUrl;
	//public static final String FILE_DOWNLOAD_FOLDER = "/downloadfolder";
	/*
	 * public static ExtentTest test; public static ExtentReports extent;
	 */

	enum DriverType {
		Firefox, IE, Chrome, EDGE
	}
	
	
	public BaseTest(String browser) {
		this.browserType = System.getProperty("browserName");
	}

	@BeforeSuite
	public void before() {
		// extent = new ExtentReports("target/surefire-reports/ExtentReport.html",
		// true);
	}

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public synchronized void setUp(Method method) throws Exception {
		if (browserType == null) {
			browserType = Configuration.readApplicationFile("Browser");
		}
		/*
		 * test = extent.startTest(method.getName(), this.getClass().getName());
		 * extent.addSystemInfo("Browser Name", browserType.toString());
		 * test.assignAuthor("360Logica");
		 * test.assignCategory(this.getClass().getSimpleName());
		 */
		TestListener.browserType = browserType;
		if (this.getClass().getPackage().toString().contains("solutionstestscript")) {
			this.applicationUrl = Configuration.readApplicationFile("SolutionsURL");
		} else {
			this.applicationUrl = Configuration.readApplicationFile("URL");
		}		
		if (DriverType.Firefox.toString().toLowerCase().equals(browserType.toLowerCase())) {
			/*System.setProperty("webdriver.gecko.driver",
					Utilities.getPath() + "//src//test//resources//webdriver/geckodriver.exe");*/
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
		} else if (DriverType.IE.toString().toLowerCase().equals(browserType.toLowerCase())) {
			System.setProperty("webdriver.ie.driver",
					Utilities.getPath() + "//src//test//resources//webdriver/IEDriverServer.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			//capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
			capabilities.setCapability("requireWindowFocus", true);
			capabilities.setCapability("allowBlockedContent", true);
		/*	WebDriverManager.iedriver().setup();*/
			driver = new InternetExplorerDriver(capabilities);
		}

		else if (DriverType.Chrome.toString().toLowerCase().equals(browserType.toLowerCase())) {
			System.setProperty("webdriver.chrome.driver",
					Utilities.getPath() + "/src//test//resources//webdriver/chromedriver.exe");
			//WebDriverManager.chromedriver().version("80.0.3987.106").setup();
			//WebDriverManager.chromedriver().setup();
			//HashMap<String, Object> chromePrefs = new HashMap<>();
			/*chromePrefs.put("download.prompt_for_download", "false");
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", getPath() + FILE_DOWNLOAD_FOLDER);*/
			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");
			options.addArguments("--dns-prefetch-disable");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--aggressive-cache-discard");
			options.addArguments("--disable-cache");
			options.addArguments("--disable-gpu");
			options.addArguments("--log-level=3");
			options.addArguments("--silent");
			options.addArguments("--disable-browser-side-navigation");
			driver = new ChromeDriver(options);
		} else if (DriverType.EDGE.toString().toLowerCase().equals(browserType.toLowerCase())) {
			/*System.setProperty("webdriver.edge.driver",
					Utilities.getPath() + "//src//test//resources//webdriver//MicrosoftWebdriver.exe");*/
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
		} else {
			throw new Exception("Please pass valid browser type value");
		}

		/* Delete cookies */
		if (!DriverType.EDGE.toString().toLowerCase().equals(browserType.toLowerCase())) {
			driver.manage().deleteAllCookies();
		}

		/* maximize the browser */
		getWebDriver().manage().window().maximize();
		/* open application URL */
		getWebDriver().navigate().to(applicationUrl);

	}

	@AfterMethod
	public void captureScreenShot(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			try {
				captureScreenshot(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (result.getStatus() == ITestResult.SKIP) {
			try {
				captureScreenshot(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//driver.quit();
		if(driver != null)
	        driver.quit();
		/* extent.endTest(test); */

	}

	@AfterClass
	public void afterMainMethod() {

	}

	@AfterSuite
	public void tearDownSuite() {
		// reporter.endReport();
		// extent.flush();
		// extent.close();
	}

	/* Return WebDriver */
	public WebDriver getWebDriver() {
		return driver;
	}

	/* Handle child windows */
	public String switchPreviewWindow() {
		Set<String> windows = getWebDriver().getWindowHandles();
		Iterator<String> iter = windows.iterator();
		String parent = iter.next();
		getWebDriver().switchTo().window(iter.next());
		return parent;
	}

	/* capturing screenshot */
	public void captureScreenshot(ITestResult result) throws IOException, InterruptedException {
		/*
		 * try { String screenshotName = Utilities.getFileName(result.getName()); File
		 * screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		 * String path = Utilities.getPath(); String screen = path + "/screenshots/" +
		 * screenshotName + ".png"; File screenshotLocation = new File(screen);
		 * FileUtils.copyFile(screenshot, screenshotLocation); Thread.sleep(2000);
		 * InputStream is = new FileInputStream(screenshotLocation); byte[] imageBytes =
		 * IOUtils.toByteArray(is); Thread.sleep(2000); String base64 =
		 * Base64.getEncoder().encodeToString(imageBytes); test.log(LogStatus.FAIL,
		 * result.getThrowable() + " \n Snapshot below: " +
		 * test.addBase64ScreenShot("data:image/png;base64," + base64)); Reporter.log(
		 * "<a href= '" + screen + "'target='_blank' ><img src='" + screen + "'>" +
		 * screenshotName + "</a>"); } catch (Exception e) {
		 * System.out.println(e.getStackTrace()); }
		 */
		try {
			String screenshotName = Utilities.getFileName(result.getName());
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String path = Utilities.getPath();
			String screen = path + "/screenshots/" + screenshotName + ".png";
			File screenshotLocation = new File(screen);
			FileUtils.copyFile(screenshot, screenshotLocation);
			InputStream is = new FileInputStream(screenshotLocation);
			byte[] imageBytes = IOUtils.toByteArray(is);
			String base64 = Base64.getEncoder().encodeToString(imageBytes);
			if (result.getStatus() == ITestResult.SKIP) {
				TestListener.getTest().log(LogStatus.SKIP, result.getThrowable() + " \n Snapshot below: "
						+ TestListener.getTest().addBase64ScreenShot("data:image/png;base64," + base64));
			}
			if (result.getStatus() == ITestResult.FAILURE) {
				TestListener.getTest().log(LogStatus.FAIL, result.getThrowable() + " \n Snapshot below: "
						+ TestListener.getTest().addBase64ScreenShot("data:image/png;base64," + base64));
			}
			Reporter.log(
					"<a href= '" + screen + "'target='_blank' ><img src='" + screen + "'>" + screenshotName + "</a>");
		} catch (Exception e) {
			reportLog("Screenshot could not be captured for " + result.getThrowable());
			logger.warn("Screenshot could not be captured for " + result.getName());
		}
	}

	/* Report logs */
	public void reportLog(String message) {
		/*
		 * test.log(LogStatus.PASS, message); message = BREAK_LINE + message;
		 * logger.info("Message: " + message); Reporter.log(message);
		 */
		if (TestListener.status) {
			TestListener.getTest().log(LogStatus.PASS, message);
		}
		logger.info(message);
		Reporter.log(message);
	}
	
	/*public static String getPath() {
		String path = "";
		File file = new File("");
		String absolutePathOfFirstFile = file.getAbsolutePath();
		path = absolutePathOfFirstFile.replaceAll("\\\\+", "/");
		return path;
		}*/

}
