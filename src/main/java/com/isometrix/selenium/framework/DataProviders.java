package com.isometrix.selenium.framework;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;

import com.isometrix.selenium.framework.Configuration;

public class DataProviders {

	@DataProvider(name = "Config")
	public static Object[][] getBrowseDetails() throws Exception {
		String bData[][]= {{""}};
		List<String> browserData = new ArrayList<String>();

		String chrome = Configuration.readApplicationFile("Chrome");
		if (chrome.toLowerCase().equals("true"))
			browserData.add("Chrome");
		String ie = Configuration.readApplicationFile("IE");
		if (ie.toLowerCase().equals("true"))
			browserData.add("IE");

		String firefox = Configuration.readApplicationFile("Firefox");
		if (firefox.toLowerCase().equals("true"))
			browserData.add("Firefox");

		Object[][] browsers = new Object[browserData.size()][1];
		int i = 0;
		for (String data : browserData) {
			browsers[i][0] = data;
			i++;
		}
		if(browsers.length<=0)
			return bData;
		else
		return browsers;
	}

}
