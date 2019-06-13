package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.HttpUtils;
import com.mandou.tools.TransferDataExcel;

public class MandoLoginTest {
	private static Logger log=Logger.getLogger(MandoLoginTest.class);

	//读取Excel表格中测试数据
	@DataProvider
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("1");
		//System.out.println(data.toString());
		return data;
		
	}
	
    @Test(priority=0,dataProvider="testCasedata")
    public void Login(Object caseid,Object host,Object path, Object mobile,Object password,Object expected) {
		Map<String, String> headers = null;
		
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);
    	
    	String resStr;
		try {
			resStr = HttpUtils.doPost(host.toString(), path.toString(), headers, params);
			//调用post请求方法，发起post请求，接受接口的返回值
	    	System.out.println("resStr: "+resStr);
	    	log.info("接口返回的报文主体内容： "+resStr);
	    	//对接口返回值进行解析
	    	JSONObject actual01=JSON.parseObject(resStr);
	    	String actual=actual01.get("success").toString();
	    	log.info("接口返回的实际结果： "+actual);
	    	assertEquals(actual, expected.toString());//对接口返回的实际结果与期望结果进行对比(断言)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    public static void MandoAppLogin(Object mobile,Object password) {
    	String host = "https://test-mobile.mandofin.com";
		String path = "/user/login.json";
		Map<String, String> headers = null;
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);
    	
    	try {
    		//调用post请求方法，发起post请求，接受接口的返回值
			String resStr = HttpUtils.doPost(host.toString(), path.toString(), headers, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
