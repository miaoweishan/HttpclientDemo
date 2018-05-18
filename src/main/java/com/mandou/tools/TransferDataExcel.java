package com.mandou.tools;

import java.io.IOException;
import java.util.List;

public class TransferDataExcel {
	static ExcelMethonClass EMC=new ExcelMethonClass();
	static String filepath=PropertiesUtil.getValueBykey("YJexcelDir");//从配置文件中读取Excel表格的路径
	
	public static Object[][] getObjectData(String caseId){
		Object[][] data=null;
		Object[][] handledata=null;
		try {
			List<Object[]> paramsList = EMC.readXLs(filepath);//读取Excel表格中的数据
			Object[][] params=new Object[paramsList.size()-1][];
//			System.out.println("    11   "+paramsList.size());
			int num=0;
			//从全部的数据中，读取目标测试接口需要的数据 (以表格中第一列数据为标识)
			for(int i=0;i<paramsList.size();i++) {
				
				if(paramsList.get(i)[0].toString().equals(caseId)) {				
					params[num]=paramsList.get(i);
					num++;
				}
			}
			//去掉第一行数据
			data=new Object[num-1][];
			for(int j=0;j<num-1;j++) {			
				data[j]=params[j+1];
			}
			
			for(int drow=0;drow<data.length;drow++) {
				for(int dcol=0;dcol<data[drow].length;dcol++) {
					if(data[drow][dcol]==null) {
						data[drow][dcol]="";
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;		
	}
	
	public static void main(String[] args) {
		TransferDataExcel.getObjectData("3");
		
	}

}
