

package com.mandou.tools;

import java.io .FileInputStream;
import java.io .FileNotFoundException;
import java.io .IOException;
import java.io .InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelMethonClass {
	/*
	 * 判断文件类型
	 */
	/*
	 * 读 cell 类型不同 处理还没有完成
	 */
	private static Logger log = Logger.getLogger(ExcelMethonClass.class);
	public List<Object[]> readXLs(String path) throws IOException {
		// 找到工作簿
		Workbook wb = null;
		List<Object[]> sheetvalue = null;
		String extString = path.substring(path.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			if (".xls".equals(extString)) {
				wb = new HSSFWorkbook(is);
				// System.out.println(".xls");
			} else if (".xlsx".equals(extString)) {
				wb = new XSSFWorkbook(is);
				// System.out.println(".xlsx");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 查看工作簿中，共有几个工作表
		int sheetCount = wb.getNumberOfSheets();
		// System.out.println(String.format("共 %d 工作表", sheetCount));

		// 读取工作表
		for (int i = 0; i < sheetCount; i++) {
			log.info("sheetCount: " + sheetCount);
			Sheet sheet = wb.getSheetAt(i);
			// System.out.println(String.format(" =======sheet  %d   =======",
			// i));

			sheetvalue = this.readSheet(sheet);

		}
		// for(Object[] a:sheetvalue) {
		// for(int i=0;i<a.length;i++) {
		// System.out.println(a[i]);
		// }
		//
		// }
		return sheetvalue;

	}

	private List<Object[]> readSheet(Sheet sheet) {
		// 获得行数
		int rows = sheet.getLastRowNum() + 1;
		// 获得列数，先获得一行，在得到改行列数
		Row tmp = sheet.getRow(0);
		if (tmp == null) {
			return null;
		}
		int cols = tmp.getPhysicalNumberOfCells();
		List<Object[]> list = new ArrayList<Object[]>();

		// 读取数据
		for (int row = 0; row < rows; row++) {
			Row r = sheet.getRow(row);
			Object[] obj = new Object[r.getLastCellNum()];
			for (int col = 0; col < cols; col++) {
				if (r.getCell(col) != null) {
					Cell cell = (Cell) r.getCell(col);
					obj[col] = cell;
					// System.out.print(this.getValue(cell)+" ");
				} else {
					continue;
				}

			}
			list.add(obj);
			// for(Object[] a:list) {
			// for(int i=0;i<a.length;i++) {
			// System.out.println(a[i]);
			// }
			//
			// }

		}
		return list;
	}

	// 转换数据格式
	private String getValue(Cell cell) {
		String result = null;
		if (cell == null) {
			log.debug("the cell is null!");
			return "";
		}

		// 通过getCellType方法判断单元格里面的内容
		switch (cell.getCellType()) {
		// 获取的内容为string类型
		case Cell.CELL_TYPE_STRING: {
			result = cell.getRichStringCellValue().getString();
		}
			break;
		// 获取的内容为数字类型（包括整型，浮点...）
		case Cell.CELL_TYPE_NUMERIC: {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date theDate = cell.getDateCellValue();
				SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result = dff.format(theDate);
			} else {
				result = cell.getNumericCellValue() + "";

			}

			break;
		}
		// 获取的内容为布尔类型
		case Cell.CELL_TYPE_BOOLEAN: {
			result = cell.getBooleanCellValue() + "";

		}
			break;
		// 获取的内容为公式类型
		case Cell.CELL_TYPE_FORMULA: {
			result = cell.getCellFormula() + "";
		}
			break;
		// 获取的内容为black
		case Cell.CELL_TYPE_BLANK: {
			result = "";
		}
			break;
		default: {
			result = "读失败了";
		}
			break;
		}

		return result;

	}

}

