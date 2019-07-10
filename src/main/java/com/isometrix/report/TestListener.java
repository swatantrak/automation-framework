package com.isometrix.report;

import com.isometrix.selenium.framework.BaseTest;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

	private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

	public static boolean status = false;
	public static String browserType = "Chrome";

	public static ExtentReports extent = new ExtentReports("target/surefire-reports/ExtentReport.html", true);
	private static Map extentTestMap = new HashMap();

	public static synchronized ExtentTest getTest() {
		return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
	}

	public static synchronized ExtentTest startTest(String testName, String desc) {
		ExtentTest test = extent.startTest(testName, desc);
		extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
		return test;
	}

	@Override
	public synchronized void onStart(ITestContext context) {
		this.status = true;
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		extent.flush();
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		logger.info("******************* Running " + result.getInstanceName() + " *******************");
		startTest(result.getMethod().getMethodName(), result.getInstance().getClass().getPackage().getName());
		getTest().assignAuthor("360Logica");
		getTest().assignCategory(result.getTestClass().getRealClass().getSimpleName());
		extent.addSystemInfo("BrowserName", browserType);
	}

	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		extent.endTest(TestListener.getTest());
		logger.info("******************* " + result.getInstanceName() + " is Passed *******************");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		extent.endTest(TestListener.getTest());
		logger.info("******************* " + result.getInstanceName() + " is Failed *******************");
		Object textclass = result.getInstance();
		WebDriver driver = ((BaseTest) textclass).getWebDriver();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		extent.endTest(TestListener.getTest());
		logger.info("******************* " + result.getInstanceName() + " is Skipped *******************");
	}

	@Override
	public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}
}