package com.mandou.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
/*
 * 读取配.properties 配置文件
 */

public class PropertiesUtil {
//	private static String filepath=System.getProperty("user.dir")+"\\src\\main\\config\\config.properties";
	private static String filepath=("src/main/config/config.properties");
	
	//读取.properties文件
	public static String getValueBykey(String key) {
		String value=null;
		Properties ppts=new Properties();
		try {
			InputStream inst=new BufferedInputStream(new FileInputStream(filepath));//file内容读入到流
			ppts.load(inst);//从输入流中读取属性列表（键和元素对）
			value=ppts.getProperty(key);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return value;
	}
	
	public static void writeProperties(String wkey,String wvalue) {
		Properties ppts=new Properties();
		try {
			InputStream ints=new FileInputStream(filepath);
			ppts.load(ints);
			OutputStream outps=new FileOutputStream(filepath);
			ppts.setProperty(wkey, wvalue);
			ppts.store(outps, "Update " + wkey + " name");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String a=PropertiesUtil.getValueBykey("YJexcelDir");
		System.out.println(a);
	}

}
