package com.isometrix.selenium.framework;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;

public abstract class BasePage {
	private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

	protected WebDriver driver;

	protected String title;
	protected static final int DEFAULT_WAIT_4_ELEMENT = 30;
	protected static final int DEFAULT_WAIT_4_PAGE = 60;
	protected static WebDriverWait ajaxWait;
	protected long timeout = 90;
	protected long pageSpinnerTimeout = 150;

	/*
	 * @Inject
	 * 
	 * @Named("framework.implicitTimeout") protected long timeout;
	 */

	public BasePage(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Get browser name
	 */
	public String getBrowserName() {
		Capabilities cap = ((RemoteWebDriver) getDriver()).getCapabilities();
		return cap.getBrowserName().toLowerCase();
	}

	/**
	 * Click On element
	 */
	public void clickOn(WebElement element) {
		waitForElement(element);
		if (getBrowserName().equalsIgnoreCase("internet explorer")) {
			mouseClick(element);
		} else {
			element.click();
		}
	}

	/**
	 * Switch to frame by id or name
	 */
	public void switchToFrame(String id) {
		getDriver().switchTo().frame(id);
	}

	/**
	 * switchTo default main Content
	 */
	public void switchToMainContents() {
		getDriver().switchTo().defaultContent();
	}

	/**
	 * switchTo new window
	 */
	public void switchToNewWindow() {
		ArrayList<String> newTab = new ArrayList<String>(getDriver().getWindowHandles());
		getDriver().switchTo().window(newTab.get(1));
	}

	/**
	 * switchTo Default window
	 */
	public void switchToDefaultWindow() {
		ArrayList<String> newTab = new ArrayList<String>(getDriver().getWindowHandles());
		getDriver().switchTo().window(newTab.get(0));
	}

	/**
	 * Click on element by webelement
	 */
	public void javascriptButtonClick(WebElement webElement) {
		waitForElement(webElement);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", webElement);
	}

	/**
	 * Wait and click on element by web element
	 */
	public void waitAndClick(WebElement element) {
		clickOn(element);
		sleepExecution(2);
	}

	/**
	 * Wait for page enable
	 */
	public void waitForPageEnable(int SECONDS) {
		sleepExecution(SECONDS);
	}

	/**
	 * Wait for page enable
	 */
	public void waitForPageEnable() {
		waitForPageEnable(2);
	}

	/**
	 * Click on element by string locator
	 */
	public void clickOn(String locator) {
		WebElement el = getDriver().findElement(ByLocator(locator));
		el.click();
	}

	public String returnTitle() {
		return title;
	}

	/**
	 * Scroll page down 250 pixel
	 */
	public void scrollDown() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,250)", "");
	}

	/**
	 * Scroll page down pixel
	 * 
	 * @Param pixel pixel to scroll down
	 */
	public void scrollDown(String pixel) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0," + pixel + ")", "");
	}

	/**
	 * Scroll page up 250 pixel
	 */
	public void scrollUp() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(250, 0)", "");
	}

	/**
	 * Scroll page up pixel
	 * 
	 * @param pixel
	 *            pixel to scroll down
	 */
	public void scrollUp(String pixel) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(" + pixel + ", 0)", "");
	}

	/**
	 * Scroll to WebElement
	 * 
	 * @param element
	 */
	public void scrollToElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].scrollIntoView();", element);
	}

	private void setImplicitWait(int timeInSec) {
		logger.info("setImplicitWait, timeInSec={}", timeInSec);
		driver.manage().timeouts().implicitlyWait(timeInSec, TimeUnit.SECONDS);
	}

	private void resetImplicitWait() {
		logger.info("resetImplicitWait");
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
	}

	public void waitFor(ExpectedCondition<Boolean> expectedCondition) {
		setImplicitWait(0);
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		wait.until(expectedCondition);
		resetImplicitWait();
	}

	/**
	 * First clear default value of input-box then enter new value
	 * 
	 * @param element
	 * @param text
	 */
	public void inputText(WebElement element, String text) {
		waitForElement(element);
		element.clear();
		element.sendKeys(text);
	}

	/**
	 * Wait for webElement once it is clickable (visible and enabled)
	 * 
	 * @param element
	 */
	public void waitForElement(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOf(element));
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			logger.info(element.toString() + " is not present on page or not clickable within " + timeout + "seconds");
		}

	}

	public void waitForElementVisible(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			logger.info(element.toString() + " is not present on page");
		}
	}

	public void waitForElement(String locator) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		if (locator instanceof String)
			wait.until(ExpectedConditions.visibilityOfElementLocated(ByLocator(locator)));
	}

	public boolean waitForElement(StringBuilder stringbuilder_locator) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		wait.until(ExpectedConditions.visibilityOfElementLocated(ByLocator(stringbuilder_locator)));
		return true;
	}

	public boolean _waitForJStoLoad() {
		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
				} catch (Exception e) {
					return true;
				}
			}
		};

		// wait for JavaScript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				Object rsltJs = ((JavascriptExecutor) driver).executeScript("return document.readyState");
				if (rsltJs == null) {
					rsltJs = "";
				}
				return rsltJs.toString().equals("complete") || rsltJs.toString().equals("loaded");
			}
		};
		boolean waitDone = false;
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		try {
			waitDone = wait.until(jQueryLoad) && wait.until(jsLoad);
		} catch (Exception Ignored) {
		}
		return waitDone;
	}

	// Handle locator type
	public By ByLocator(String locator) {
		By result = null;
		if (locator.startsWith("//") || locator.startsWith("(")) {
			result = By.xpath(locator);
		} else if (locator.startsWith("css=")) {
			result = By.cssSelector(locator.replace("css=", ""));
		} else if (locator.startsWith("#")) {
			result = By.id(locator.replace("#", ""));
		} else if (locator.startsWith("name=")) {
			result = By.name(locator.replace("name=", ""));
		} else if (locator.startsWith("link=")) {
			result = By.linkText(locator.replace("link=", ""));
		} else {
			result = By.className(locator);
		}
		return result;
	}

	public By ByLocator(StringBuilder locator) {
		By result = null;

		if (locator.charAt(0) == 'x') {
			result = By.xpath(locator.toString().replace("xpath=", ""));
		} else if (locator.charAt(0) == 'c') {
			result = By.cssSelector(locator.toString().replace("css=", ""));
		} else if (locator.charAt(0) == 'i') {
			result = By.id(locator.toString().replace("#", ""));
		}
		return result;
	}

	public static String generateRandomString(int lettersNum) {
		String finalString = "";

		int numberOfLetters = 25;
		long randomNumber;
		for (int i = 0; i < lettersNum; i++) {
			char letter = 97;
			randomNumber = Math.round(Math.random() * numberOfLetters);
			letter += randomNumber;
			finalString += String.valueOf(letter);
		}
		return finalString;
	}

	public static String generateRandomNumber(int lettersNum) {
		String finalString = "9";
		int letter;
		for (int i = 0; i < lettersNum - 1; i++) {
			letter = generateRandomNumber(0, 9);
			finalString += String.valueOf(letter);
		}
		return finalString;
	}

	public boolean verifyURL(String url) {
		boolean value = false;
		String currentUrl = driver.getCurrentUrl();
		if (currentUrl.contains(url))
			return true;
		else
			return value;
	}

	public void refreshPage() {
		getDriver().navigate().refresh();
	}

	public WebDriver getDriver() {
		return driver;
	}

	public WebElement findElement(By by) {
		if (driver instanceof ChromeDriver || driver instanceof InternetExplorerDriver) {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		WebElement foundElement = null;
		for (int milis = 0; milis < 3000; milis = milis + 200) {
			try {
				foundElement = driver.findElement(by);
				return foundElement;
			} catch (Exception e) {
				// Utils.hardWaitMilliSeconds(200);
			}
		}
		return null;
	}

	public void assertByPageTitle() {
		try {
			if (driver instanceof ChromeDriver || driver instanceof InternetExplorerDriver
					|| driver instanceof FirefoxDriver) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertTrue(returnTitle().equals(driver.getTitle()));
	}

	public List<String> findAllLinksOnPage() {
		List<String> links = new ArrayList<String>();
		List<WebElement> linkElements = driver.findElements(By.tagName("a"));
		for (WebElement each : linkElements) {
			String link = each.getAttribute("href");
			if (link == null || link.contains("mailto") || link.contains("javascript")) {
				continue;
			}
			links.add(link);
		}
		return links;
	}

	public boolean isResponseForLinkTwoHundredOrThreeOTwo(String link) {
		int code = 0;
		Reporter.log("Link: " + link);
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			code = connection.getResponseCode();
			Reporter.log("Code: " + code);
		} catch (Exception e) {
			Reporter.log(e.toString());
			return false;
		}
		if (link.contains("pager") || code == 403) {
			return true;
		}
		return code == 200 || code == 302;
	}

	public void setWaitTime(WebDriver driver, int waitTime) {
		driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.SECONDS);
	}

	public void setWaitTimeToZero(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	}

	public void customizableCondition(WebDriver driver, int waitTime, final Boolean condition) {
		// setWaitTimeToZero(driver);
		new WebDriverWait(driver, waitTime).until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				return condition;
			}
		});
		// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
	}

	public WebElement waitForElementClickable(WebElement webElement, int timeOutInSeconds) {
		WebElement element;
		try {
			// setWaitTimeToZero(driver);
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions.elementToBeClickable(webElement));

			// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
			return element;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public WebElement waitForElementPresent(final By by, int timeOutInSeconds) {
		WebElement element;
		try {

			// setWaitTimeToZero(driver);
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

			// setWaitTime(driver, DEFAULT_WAIT_4_ELEMENT);
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public WebElement waitForElementPresent(WebElement webElement, int timeOutInSeconds) {
		WebElement element;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			element = wait.until(ExpectedConditions.visibilityOf(webElement));
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean waitForTextPresentInElement(WebElement webElement, String text, int timeOutInSeconds) {
		boolean notVisible;
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		notVisible = wait.until(ExpectedConditions.textToBePresentInElement(webElement, text));

		return notVisible;
	}

	public boolean waitForTextPresentInElement(By by, String text, int timeOutInSeconds) {
		boolean notVisible;
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		notVisible = wait.until(ExpectedConditions.textToBePresentInElementLocated(by, text));

		return notVisible;
	}

	public Boolean isElementPresent(WebElement element) {
		try {
			waitForElementVisible(element);
			element.isDisplayed();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	public Boolean isElementPresent(String locator) {
		Boolean result = false;
		try {
			getDriver().findElement(ByLocator(locator));
			result = true;
		} catch (Exception ex) {
		}
		return result;
	}

	public Boolean isElementDisplayed(WebElement element) {
		try {
			element.isDisplayed();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	public Boolean isElementDisplayed(String element) {
		try {
			getDriver().findElement(ByLocator(element)).isDisplayed();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	public void WaitForElementNotPresent(String locator, int timeout) {
		for (int i = 0; i < timeout; i++) {
			if (!isElementPresent(locator)) {
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void WaitForElementPresent(String locator, int timeout) {
		for (int i = 0; i < timeout; i++) {
			if (isElementPresent(locator)) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int findNumberOfSpecificElementsInContainer(By container, By element) {
		WebElement mainDiv = driver.findElement(container);
		List<WebElement> divs = mainDiv.findElements(element);
		return divs.size();
	}

	public WebDriver hoverOverElementAndClick(WebElement toBeHovered, WebElement toBeClicked) {
		Actions builder = new Actions(driver);
		builder.moveToElement(toBeHovered).build().perform();
		waitForElementPresent(toBeClicked, DEFAULT_WAIT_4_ELEMENT);
		toBeClicked.click();
		waitForPageLoaded(driver);
		return driver;
	}

	public void mouseClick(WebElement element) {
		Actions builder = new Actions(driver);
		builder.moveToElement(element).click().build().perform();
	}

	/**
	 * Select element by visible text
	 * 
	 * @Param element
	 * 
	 * @Patram targetValue: visible text
	 */
	public void selectDropDownByText(WebElement element, String targetValue) {
		waitForElement(element);
		new Select(element).selectByVisibleText(targetValue);
	}

	/**
	 * Select element by Index
	 * 
	 * @Param element
	 * 
	 * @Patram index
	 */
	public void selectDropDownByIndex(WebElement element, int index) {
		waitForElement(element);
		new Select(element).selectByIndex(index);
	}

	/**
	 * Select element by value
	 * 
	 * @Param element
	 * 
	 * @Patram targetValue: value
	 */
	public void selectDropDownByValue(WebElement element, String targetValue) {
		waitForElement(element);
		new Select(element).selectByValue(targetValue);
	}

	public void waitForElementToBecomeVisible(By by) {
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void waitForElementToBecomeInvisible(By by) {
		WebDriverWait wait = new WebDriverWait(driver, 180);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	public void waitForAjaxRequestsToComplete() {
		(new WebDriverWait(driver, DEFAULT_WAIT_4_PAGE)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				JavascriptExecutor js = (JavascriptExecutor) d;
				return (Boolean) js.executeScript("return jQuery.active == 0");
			}
		});
	}

	public void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, 20);
		wait.until(expectation);
	}

	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean isTextPresentOnPage(String text) {
		return driver.findElement(By.tagName("body")).getText().contains(text);
	}

	public boolean isFileAvailableForDownload(WebElement webElement) throws Exception {
		int code = 0;
		String downloadUrl = webElement.getAttribute("href");
		URL url = new URL(downloadUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		code = connection.getResponseCode();
		Reporter.log("The response code for download is " + code);
		return code == 200;
	}

	public void takeRemoteWebDriverScreenShot(String fileName) {
		File screenshot = ((TakesScreenshot) new Augmenter().augment(driver)).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshot, new File(fileName));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void waitForTextNotToBeVisible(String text, int timeoutInSeconds) {
		int startWait = 0;
		while (isTextPresentOnPage(text)) {
			// Utils.hardWaitSeconds(1);
			startWait++;
			if (startWait == timeoutInSeconds) {
				throw new TimeoutException();
			}
		}
	}

	public void waitForWebElementPresent(WebElement element) {
		WebDriverWait ajaxWait = new WebDriverWait(driver, 30);
		ajaxWait.until(ExpectedConditions.visibilityOf(element));
	}

	public Boolean selectRadioBtn(WebElement element) {

		boolean flag = element.isSelected();
		try {
			if (!flag) {
				element.click();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}

	private void sleepExecution(int sec) {
		sec = sec * 1000;
		try {
			Thread.sleep(sec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate Random number from given range
	 * 
	 * @param min
	 * @param max
	 * @return Random number
	 */
	public static int generateRandomNumber(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max);
		return randomNum;
	}

	// public void sleep(int time) {
	// try {
	// Thread.sleep(time);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Verify WebElement present on the page
	 * 
	 * @param element
	 */
	public void verifyElementPresent(WebElement element) {
		Assert.assertTrue(isElementDisplayed(element), element.toString() + " is not present");
	}

	/**
	 * Verify WebElement not present on the page
	 * 
	 * @param element
	 */
	public void verifyElementNotPresent(WebElement element) {
		Assert.assertFalse(isElementDisplayed(element), element.toString() + " is present");
	}

	/**
	 * Verify text of the WebElement and present the WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementText(WebElement element, String text) {
		Assert.assertTrue(isElementPresent(element), element.toString() + " is not present");
		String actualText = element.getText().trim();
		if (actualText.isEmpty())
			actualText = element.getAttribute("value");
		if (actualText == null)
			actualText = "";
		Assert.assertEquals(actualText, text,
				element.toString() + " text '" + actualText + "' not matched with '" + text + "'");
	}

	/**
	 * WebElement present having text in the list WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementsText(List<WebElement> element, String text) {
		boolean status = false;
		for (int i = 0; i < element.size(); i++) {
			String actualText = element.get(i).getText().trim();
			if (actualText.isEmpty())
				actualText = element.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.equalsIgnoreCase(text)) {
				status = true;
				break;
			}
		}
		Assert.assertTrue(status, text + " text not present in " + element.toString());
	}

	/**
	 * WebElement present having text in the list WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementsTextContains(List<WebElement> element, String text) {
		boolean status = false;
		for (int i = 0; i < element.size(); i++) {
			String actualText = element.get(i).getText().trim();
			if (actualText.isEmpty())
				actualText = element.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.contains(text)) {
				status = true;
				break;
			}
		}

		Assert.assertTrue(status, element.toString() + " Text not contains");
	}

	/**
	 * WebElement present having text starts with in the list WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementsTextStartWith(List<WebElement> element, String text) {
		boolean status = false;
		for (int i = 0; i < element.size(); i++) {
			String actualText = element.get(i).getText().trim();
			if (actualText.isEmpty())
				actualText = element.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.startsWith(text)) {
				status = true;
				break;
			}
		}
		Assert.assertTrue(status, element.toString() + " text not starts with " + text);
	}

	/**
	 * WebElement present having text ends with in the list WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementsTextEndsWith(List<WebElement> element, String text) {
		boolean status = false;
		for (int i = 0; i < element.size(); i++) {
			String actualText = element.get(i).getText().trim();
			if (actualText.isEmpty())
				actualText = element.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.endsWith(text)) {
				status = true;
				break;
			}
		}
		Assert.assertTrue(status, element.toString() + " text not ends with " + text);
	}

	/**
	 * WebElement not present having text in the list WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementsTextNot(List<WebElement> element, String text) {
		boolean status = false;
		for (int i = 0; i < element.size(); i++) {
			String actualText = element.get(i).getText().trim();
			if (actualText.isEmpty())
				actualText = element.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.equalsIgnoreCase(text)) {
				status = true;
				break;
			}
		}

		Assert.assertFalse(status, element.toString() + " Text present");
	}

	/**
	 * Matching two string
	 * 
	 * @param string1
	 * @param string2
	 */
	public void verifyTextMatch(String string1, String string2) {
		Assert.assertEquals(string1, string2, string1 + " and " + string2 + " are not matched");

	}

	public void verifyTextMatch(String string1, String string2, String msg) {
		Assert.assertEquals(string1, string2, msg);
	}

	/**
	 * First string contains second string
	 * 
	 * @param string1
	 * @param string2
	 */
	public void verifyTextContains(String string1, String string2) {
		boolean status = false;
		if (string1.contains(string2)) {
			status = true;
		}
		Assert.assertTrue(status, string1 + " not contains " + string2);
	}

	public void verifyTextContains(String string1, String string2, String msg) {
		boolean status = false;
		if (string1.contains(string2)) {
			status = true;
		}
		Assert.assertTrue(status, msg);
	}

	/**
	 * Zoom in the html page
	 * 
	 * @throws AWTException
	 */
	// @SuppressWarnings("restriction")
	public void pageZoomIn() throws AWTException {
		_waitForJStoLoad();
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_MINUS);
		robot.keyRelease(KeyEvent.VK_MINUS);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	@FindBy(xpath = "//*[@class='jstree-rename-input']")
	private WebElement element;

	/**
	 * Press tab keyboard key
	 * 
	 * @throws AWTException
	 */
	// @SuppressWarnings("restriction")
	public void pressTabKey() throws AWTException {
		try {
			element.sendKeys(Keys.TAB);
		} catch (Exception e) {
			_waitForJStoLoad();
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);
			sleepExecution(1);
			robot.keyRelease(KeyEvent.VK_TAB);
			sleepExecution(2);
		}

	}

	/**
	 * Press Enter key from keyboard
	 * 
	 * @throws AWTException
	 */
	// @SuppressWarnings("restriction")
	public void pressEnter() throws AWTException {
		try {
			sleepExecution(1);
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ENTER);
			sleepExecution(1);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			sleepExecution(1);
			robot.keyRelease(KeyEvent.VK_ENTER);
			sleepExecution(1);
		} catch (Exception e) {
		}

	}

	/**
	 * Right click and select element
	 * 
	 * @param element
	 * @param elementToSelect
	 */
	public void rightClickAndSelect(WebElement element, WebElement elementToSelect) {
		Actions action = new Actions(driver).contextClick(element);
		action.build().perform();
		waitForElement(elementToSelect);
		sleepExecution(2);
		clickOn(elementToSelect);
	}

	/**
	 * Right click and select first level element then click on second level element
	 * 
	 * @param element
	 * @param elementFirstLevelToSelect
	 * @param elementSecondLevelToClick
	 */
	public void rightClickAndSelectSecondLevelOption(WebElement element, WebElement elementFirstLevelToSelect,
			WebElement elementSecondLevelToClick) {
		Actions action = new Actions(driver).contextClick(element);
		action.build().perform();
		clickOn(elementFirstLevelToSelect);
		sleepExecution(1);
		clickOn(elementSecondLevelToClick);

	}

	/**
	 * Drag the first element and drop to another element.
	 * 
	 * @param onElement
	 *            Web Element to be dragged
	 * @param toElement
	 *            Web Element to be dropped
	 */
	public void dragAndDrop(WebElement onElement, WebElement toElement) {
		Actions builder = new Actions(driver);
		builder.clickAndHold(onElement).moveToElement(toElement).release(toElement).build().perform();

	}

	/**
	 * Verify two ArrayList matched.
	 * 
	 * @param list1
	 * @param list2
	 */
	public void verifyListMatch(ArrayList<?> list1, ArrayList<?> list2) {
		Assert.assertEquals(list1, list2, "List are not matched");
	}

	/**
	 * ArrayList sorting as Ascending order.
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> sortingAscending(ArrayList<String> list) {
		Collections.sort(list);
		return list;
	}

	public ArrayList<Integer> sortingAscendingInteger(ArrayList<Integer> list) {
		Collections.sort(list);
		return list;
	}

	/**
	 * ArrayList sorting as Descending order.
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> sortingDescending(ArrayList<String> list) {
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}

	public ArrayList<Integer> sortingDescendingInterger(ArrayList<Integer> list) {
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}

	/**
	 * verify CheckBox is checked.
	 * 
	 * @param element
	 */
	public void verifyChecked(WebElement element) {
		Assert.assertEquals(element.getAttribute("checked"), "true",
				element.toString() + "The checkbox is not checked");
	}

	/**
	 * Return the reference of WebElement by locator in string
	 * 
	 * @param locator
	 *            xpath or css are in form of string
	 * @return reference of WebElement
	 */
	public WebElement getElement(String locator) {
		waitForElement(locator);
		return getDriver().findElement(ByLocator(locator));

	}

	/**
	 * Return the reference of WebElement when WebElement text matched
	 * 
	 * @param elements
	 *            Collection of WebElement
	 * @param text:
	 *            to be matched
	 * @return
	 */
	public WebElement getElement(List<WebElement> elements, String text) {
		WebElement element = null;
		for (int i = 0; i < elements.size(); i++) {
			String actualText = elements.get(i).getText();
			if (actualText.equalsIgnoreCase(""))
				actualText = elements.get(i).getAttribute("value");
			if (actualText == null)
				actualText = "";
			if (actualText.equalsIgnoreCase(text)) {
				element = elements.get(i);
				break;
			}
		}
		return element;
	}

	/**
	 * Return the reference of first WebElement which displayed on current page
	 * 
	 * @param elements
	 *            Collection of WebElement
	 * @return first displayed element
	 */
	public WebElement getFirstElement(List<WebElement> elements) {
		for (WebElement element : elements) {
			if (element.isDisplayed()) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Return the text of WebElement
	 * 
	 * @param element
	 *            Web Element
	 * @return Text of WebElement
	 */
	public String getElementText(WebElement element) {
		String actualText = element.getText().trim();
		if (actualText.isEmpty())
			actualText = element.getAttribute("value");
		if (actualText == null)
			actualText = "";
		return actualText;
	}

	/**
	 * Return the attribute of element
	 * 
	 * @param element
	 * @param attributeName
	 * @return Attribute in string format
	 */
	public String getElementAttribute(WebElement element, String attributeName) {
		return element.getAttribute(attributeName);
	}

	/**
	 * Return the css value of element
	 * 
	 * @param element
	 * @param cssValue
	 * @return
	 */
	public String getElementCssValue(WebElement element, String cssValue) {
		return element.getCssValue(cssValue);
	}

	/**
	 * Return the count of WebElement
	 * 
	 * @param elements
	 *            Collection of Web Element
	 * @return WebElement count
	 */
	public int getElementCount(List<WebElement> elements) {
		return elements.size();
	}

	/**
	 * Matching two numbers
	 * 
	 * @param firstNumber
	 * @param secondNumber
	 */
	public void verifyNumberMatched(int firstNumber, int secondNumber) {
		Assert.assertEquals(firstNumber, secondNumber, firstNumber + " and " + secondNumber + " number not matched");
	}

	/**
	 * Verify first number is greater or equals with second number
	 * 
	 * @param firstNumber
	 * @param secondNumber
	 */
	public void verifyNumberGreaterOrEquals(int firstNumber, int secondNumber) {
		boolean status = false;
		if (firstNumber >= secondNumber) {
			status = true;
		}
		Assert.assertTrue(status, firstNumber + " not equals with " + secondNumber);
	}

	/**
	 * Verify item in the list is ascending order
	 * 
	 * @param itemList
	 */
	@SuppressWarnings("unchecked")
	public void veryfyListAscending(List<WebElement> itemList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++) {
			list.add(itemList.get(i).getText().toLowerCase());
		}
		ArrayList<String> list1 = new ArrayList<String>();
		list1 = (ArrayList<String>) list.clone();
		verifyListMatch(list1, sortingAscending(list));
	}

	@SuppressWarnings("unchecked")
	public void veryfyListAscendingInteger(List<WebElement> itemList) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < itemList.size(); i++) {
			list.add(Integer.parseInt(itemList.get(i).getText()));
		}
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1 = (ArrayList<Integer>) list.clone();
		verifyListMatch(list1, sortingAscendingInteger(list));
	}

	/**
	 * Verify item in the list is descending order
	 * 
	 * @param itemList
	 */
	@SuppressWarnings("unchecked")
	public void veryfyListDescending(List<WebElement> itemList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++) {
			list.add(itemList.get(i).getText().toLowerCase());
		}
		ArrayList<String> list1 = new ArrayList<String>();
		list1 = (ArrayList<String>) list.clone();
		verifyListMatch(list1, sortingDescending(list));
	}

	@SuppressWarnings("unchecked")
	public void veryfyListDescendingInteger(List<WebElement> itemList) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < itemList.size(); i++) {
			list.add(Integer.parseInt(itemList.get(i).getText()));
		}
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1 = (ArrayList<Integer>) list.clone();
		verifyListMatch(list1, sortingDescendingInterger(list));
	}

	/**
	 * Verify text matches of the WebElement and present the WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementTextMatches(WebElement element, String pattern) {
		Assert.assertTrue(isElementPresent(element), element.toString() + " is not present");
		String actualText = element.getText().trim();
		if (actualText.isEmpty())
			actualText = element.getAttribute("value");
		if (actualText == null)
			actualText = "";
		Assert.assertTrue(actualText.matches(pattern), "Assert fails test does not matches to the pattern");
	}

	/**
	 * Return the current date in given format
	 * 
	 * @param format
	 * @return
	 */
	public static String GetCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * Return the current time in given format
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrentTime() {
		DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		return timeFormat.format(cal.getTime());
	}

	/**
	 * Verify text of the WebElement contains and present the WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementTextContains(WebElement element, String text) {
		Assert.assertTrue(isElementPresent(element), element.toString() + " is not present");
		String actualText = element.getText().trim();
		if (actualText.isEmpty())
		actualText = element.getAttribute("value");
		if (actualText == null)
		actualText = "";
		Assert.assertTrue(actualText.contains(text),
		"Actual text does not contains given text, Actual:" + actualText + " Expected:" + text);
		}

	/**
	 * Verify text of the WebElement does not contains and present the WebElement
	 * 
	 * @param element
	 * @param text
	 */
	public void verifyElementTextNotContains(WebElement element, String text) {
		Assert.assertTrue(isElementPresent(element), element.toString() + " is not present");
		String actualText = element.getText().trim();
		if (actualText.isEmpty())
			actualText = element.getAttribute("value");
		if (actualText == null)
			actualText = "";
		Assert.assertTrue(!actualText.contains(text), actualText + " contains " + text);
	}

	public void hoverOverElement(WebElement toBeHovered) {
		Actions builder = new Actions(driver);
		builder.moveToElement(toBeHovered).build().perform();
	}

	public void uploadFile(String path) throws AWTException {
		StringSelection stringSelection = new StringSelection(path);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, stringSelection);
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
	}

	public void verifyElementPresent(WebElement element, String message) {
		Assert.assertTrue(isElementDisplayed(element), element.toString() + message);
	}

	public void verifyElementNotPresent(WebElement element, String message) {
		Assert.assertFalse(isElementDisplayed(element), element.toString() + message);
	}

	/**
	 * First string not contains second string
	 * 
	 * @param string1
	 * @param string2
	 */
	public void verifyTextNotContains(String string1, String string2, String msg) {
		boolean status = false;
		if (!string1.contains(string2)) {
			status = true;
		}
		Assert.assertTrue(status, string1 + msg);
	}

	/**
	 * Nor equals two string
	 * 
	 * @param string1
	 * @param string2
	 */
	public void verifyTextNotMatch(String string1, String string2, String msg) {
		boolean status = false;
		if (!string1.equals(string2)) {
			status = true;
		}
		Assert.assertTrue(status, string1 + msg);
	}
}
