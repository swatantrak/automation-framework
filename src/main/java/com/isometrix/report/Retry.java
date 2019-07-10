package com.isometrix.report;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

  private int retryCount = 0;
  private int maxRetryCount = 1;

  public boolean retry(ITestResult result) {
    boolean run = false;
    if (retryCount < maxRetryCount) {
    	retryCount++;
      run = true;
    }
    return run;
  }
}