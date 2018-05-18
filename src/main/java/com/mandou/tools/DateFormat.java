package com.mandou.tools;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/*
 * 转换时间格式
 */
public class DateFormat {
	
	public static String dateRevise(Date date,String dateformat) {

		SimpleDateFormat sf=new SimpleDateFormat(dateformat);
		String nowdate=sf.format(date);//转换成设定的格式
		Calendar cal=Calendar.getInstance();
		try {
			cal.setTime(sf.parse(nowdate));
			cal.add(Calendar.DAY_OF_YEAR,+60);
			String newdate=sf.format(cal.getTime());
			cal.setTime(sf.parse(nowdate));
			cal.add(Calendar.DAY_OF_YEAR, +61);
			String newdate2=sf.format(cal.getTime());
			return newdate2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Date date=new Date();//获取当前时间
		System.out.println(DateFormat.dateRevise(date,"yyyy-MM-dd"));
		System.out.println(DateFormat.dateRevise(date,"yyyyMMddHHmmss"));
	}

}
