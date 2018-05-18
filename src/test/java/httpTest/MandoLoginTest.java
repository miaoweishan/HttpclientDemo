package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.httpsUtil;
import com.mandou.tools.TransferDataExcel;

public class MandoLoginTest {
	private static Logger log=Logger.getLogger(MandoLoginTest.class);
	private static String url="https://test-mobile.mandofin.com/user/login.json";

	//读取Excel表格中测试数据
	@DataProvider
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("1");
		//System.out.println(data.toString());
		return data;
		
	}
	
    @Test(priority=0,dataProvider="testCasedata")
    public void Login(Object caseid,Object mobile,Object password,Object expected) {
    	
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);
    	
    	String resStr = httpsUtil.getHttpsPost(url, params);//调用post请求方法，发起post请求，接受接口的返回值
    	System.out.println("resStr: "+resStr);
    	log.info("接口返回的报文主体内容： "+resStr);
    	//对接口返回值进行解析
    	JSONObject actual01=new JSONObject(resStr);
    	String actual=actual01.get("success").toString();
    	log.info("接口返回的实际结果： "+actual);
    	
    	assertEquals(actual, expected.toString());//对接口返回的实际结果与期望结果进行对比(断言)
    }
    
    
    public static void MandoAppLogin(Object mobile,Object password) {
    	
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);
    	
    	String resStr = httpsUtil.getHttpsPost(url, params);//调用post请求方法，发起post请求，接受接口的返回值

    }
}
