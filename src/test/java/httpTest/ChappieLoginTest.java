package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.httpUtil;
import com.mandou.tools.TransferDataExcel;

public class ChappieLoginTest {
	private static Logger log = Logger.getLogger(ChappieLoginTest.class);
	private static String loginurl = "http://test-chappie.mandofin.com/exclude/manager/login.json";
	
	//读取Excel表格中测试数据
	@DataProvider
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("2");
	 	//System.out.println(data.toString());
		return data;
	}
	
	@Test(dataProvider = "testCasedata")
	public void Testlogin(Object caseid, Object userName, Object password, Object expected) {
		//把参数放到map集合内
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("password", password);
		
		String resStr = httpUtil.getHttpPost(loginurl, params);
		
		JSONObject actual01 = new JSONObject(resStr);
		String actual = actual01.get("success").toString();
		log.info("响应结果： "+actual);
		assertEquals(actual, expected.toString());
	}
	
	public static void chappieLogin(Object userName, Object password) {
		//把参数放到map集合内
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("password", password);
		
		httpUtil.getHttpPost(loginurl, params);
	}

}
