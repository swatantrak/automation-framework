package com.isometrix.selenium.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Configuration {

	private String fileName;
	static String path = getFilePath();

	public Configuration(String fileName) {
		this.fileName = fileName;
	}

	public static String readApplicationFile(String key) throws Exception {
		String value = "";
		try {
			Properties prop = new Properties();
			File f = new File(path + "/Config.properties");
			if (f.exists()) {
				prop.load(new FileInputStream(f));
				value = prop.getProperty(key);
			} else {
				throw new Exception("File not found");
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Failed to read from application.properties file.");
			throw ex;
		}
		if (value == null)
			throw new Exception("Key not found in properties file");
		return value;
	}

	public String readApplicationData(String key) throws Exception {
		String value = "";
		try {
			Properties prop = new Properties();
			File f = new File(path + "//src//test//resources//testdata//" + fileName + ".properties");
			if (f.exists()) {
				prop.load(new FileInputStream(f));
				value = prop.getProperty(key);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Failed to read from application.properties file.");
			throw new FileNotFoundException("File not found " + fileName);
		}
		return value;
	}

	public void updateApplicationData(String key, String val) throws Exception {
		try {
			Properties prop = new Properties();
			File f = new File(path + "//src//test//resources//testdata//" + fileName + ".properties");
			if (f.exists()) {
				FileInputStream in = new FileInputStream(f);
				prop.load(in);
				in.close();
				prop.setProperty(key, val);
				FileOutputStream out = new FileOutputStream(f);
				prop.store(out, null);
				out.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Failed to update " + fileName + ".properties file");
			throw new FileNotFoundException("File not found " + fileName);
		}
	}

	public static List<String> readExcelData(String filePath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		 Workbook workbook = WorkbookFactory.create(new File(filePath));
		 Sheet sheet=workbook.getSheetAt(0);
		 int lastRowNum=sheet.getLastRowNum();
		 List<String> list=new ArrayList<String>();
		 for (int i=1; i<=lastRowNum; i++ ) {
			 list.add(
					 sheet.getRow(i)
					 .getCell(0)
					 .getStringCellValue());
		 }
		return list;
		 
	}
	
	public static String getFilePath() {
		String filepath = "";
		File file = new File("");
		String absolutePathOfFirstFile = file.getAbsolutePath();
		filepath = absolutePathOfFirstFile.replaceAll("\\\\+", "/");
		return filepath;
	}

}