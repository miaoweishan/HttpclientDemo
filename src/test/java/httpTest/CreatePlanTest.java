package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.httpUtil;
import com.mandou.tools.JDBCUtil;
import com.mandou.tools.TransferDataExcel;
/*
 * 创建预购计划
 */
public class CreatePlanTest {
	private static Logger log = Logger.getLogger(ChappieLoginTest.class);
	private static String createplanurl = "http://test-chappie.mandofin.com/plan/create.json";
	
	//读取Excel表格中测试数据
	@DataProvider
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("3");
	 	//System.out.println(data.toString());
		return data;
		
	}
	
	@Test(dataProvider="testCasedata")
	public void TestCreatePlan(Object caseid, Object name, Object planRate, Object raiseRate, Object startTime, Object endTime,
			 Object settlementMode, Object transfer, Object vipAppendRate, Object limitMoney, Object purchaseMoney,Object commonLabel,
			 Object hotLabel, Object hotLabelUrl, Object showTime, Object advertisingState, Object advertisingText,Object advertisingUrl,Object expected) 
	{
		
		String value= JDBCUtil.getSelect("select * from purchase_advance_plan where `name`="+"'"+name+"'", "id").toString();
		if(value !="") {
			JDBCUtil.setDelete("delete from purchase_advance_plan where `name`="+"'"+name+"'");
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("planRate", planRate);
		params.put("raiseRate", raiseRate);
		params.put("startTime", startTime);
		params.put("advertisingUrl", advertisingUrl);
		params.put("endTime", endTime);
		params.put("settlementMode", settlementMode.toString());
		System.out.println("settlementMode: "+settlementMode);
		params.put("transfer", transfer);
		params.put("vipAppendRate", vipAppendRate);
		params.put("limitMoney", limitMoney);
		params.put("purchaseMoney", purchaseMoney);
		params.put("advertisingText", advertisingText);
		params.put("commonLabel", commonLabel);
		params.put("hotLabel", hotLabel);
		params.put("hotLabelUrl", hotLabelUrl);
		params.put("showTime", showTime);
		params.put("advertisingState", advertisingState);
		
		String resStr = httpUtil.getHttpPost(createplanurl, params);
		JSONObject actual01 = new JSONObject(resStr);
		String actual = actual01.get("success").toString();
		System.out.println("actual01:"+actual01);
		assertEquals(actual, expected.toString());
		
	}

}
