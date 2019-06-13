package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.HttpUtils;
import com.mandou.tools.TransferDataExcel;

public class ChappieLoginTest {
	private static Logger log = Logger.getLogger(ChappieLoginTest.class);
	
	//读取Excel表格中测试数据
	@DataProvider
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("2");
	 	//System.out.println(data.toString());
		return data;
	}
	
	@Test(dataProvider = "testCasedata")
	public void Testlogin(Object caseid, Object host, Object path, Object userName, Object password, Object expected) {
		Map<String, String> headers = null;
		String resStr = "";
		//把参数放到map集合内
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("userName", userName);
		params.put("password", password);
		
		try {
			resStr = HttpUtils.doPost(host.toString(), path.toString(), headers, params);
			JSONObject actual01 = JSON.parseObject(resStr);
			String actual = actual01.get("success").toString();
			log.info("响应结果： "+actual);
			assertEquals(actual, expected.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void chappieLogin(String userName, String password) {
		Map<String, String> headers = null;
		String resStr = "";
		String host = "";
		String path = "";
		
		//把参数放到map集合内
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("userName", userName);
		params.put("password", password);
		
		try {
			resStr = HttpUtils.doPost(host, path, headers, params);
			JSONObject actual01 = JSON.parseObject(resStr);
			String actual = actual01.get("success").toString();
			log.info("响应结果： "+actual);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
