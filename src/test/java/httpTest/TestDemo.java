package httpTest;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mandou.httpUtil.HttpUtils;
import com.mandou.httpUtil.urlRedirect;
import com.mandou.tools.JDBCUtil;
import com.mandou.tools.TransferDataExcel;

@SuppressWarnings("deprecation")
public class TestDemo {
	private static Logger log=Logger.getLogger(TestDemo.class);
	private static String no=null;
	private static String forwardUrl=null;
	
	@DataProvider //读取Excel表格中测试数据
	public static Object[][] testCasedata(){
		Object[][] data = TransferDataExcel.getObjectData("1");
		//System.out.println(data.toString());
		return data;
		
	}
	
    @Test(priority=0,dataProvider="testCasedata")
    public void Login(Object caseid,Object host,Object path,Object mobile,Object password,Object expected) {
    	Map<String, String> headers = null;
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);  	
    	String resStr;
		try {
			resStr = HttpUtils.doPost(host.toString(), path.toString(), headers, params);
			System.out.println("resStr: "+resStr);   	
	    	JSONObject actual01= JSON.parseObject(resStr);
	    	String actual=actual01.get("success").toString();
	    	assertEquals(actual, expected.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @Test(priority=1)
    public void Submit() {
    	String host = "https://test-mobile.mandofin.com";
		String path = "/payment/submit.json";
		Map<String, String> headers = null;
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("money", "10");
    	params.put("ot", "INCOME"); 
    	params.put("payPass", "258147");
    	String resStr;
		try {
			resStr = HttpUtils.doPost(host, path, headers, params);
			System.out.println("resStr: "+resStr); 
	    	JSONObject act1= JSON.parseObject(resStr);
	    	String model = act1.get("model").toString();
	    	JSONObject act2= JSON.parseObject(model);
	    	no=act2.get("no").toString();//订单号
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @Test(priority=2)
    public void paymentOrde() {
    	String host = "https://test-mobile.mandofin.com";
		String path = "/payment/paymentOrder.json";
		Map<String, String> headers = null;
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("amount", "10");
    	params.put("cardId", "5036");
    	params.put("method", "BANK");
    	params.put("orderNo", no); 
    	params.put("terminalType", "IOS");
    	params.put("type", "CHARGE_PAYMENT"); 
    	
    	String resStr;
		try {
			resStr = HttpUtils.doPost(host, path, headers, params);
			System.out.println("resStr: "+resStr); 
	    	JSONObject act1= JSON.parseObject(resStr);
	    	String data = act1.get("data").toString();
	    	JSONObject act2= JSON.parseObject(data);
	    	String paymentResult=act2.get("paymentResult").toString();
	    	JSONObject act3 = JSON.parseObject(paymentResult);
	    	forwardUrl=act3.get("forwardUrl").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @Test(priority=3)
    public void rechargeSwift() throws Exception {
    	String hubkhost = "https://hubk.lanmaoly.com";
		String hubkpath = "/bha-neo-app/gateway/mobile/recharge/rechargeSwift.do";
		Map<String, String> hubkquerys=null;
		Map<String, String> hubkheaders = null;
    	String requestKey=null;
    	String Locurl = urlRedirect.getRedirectUrl(forwardUrl);
    	String[] values = Locurl.split("=");
    	requestKey = values[1];
    	HttpUtils.doGet(hubkhost, hubkpath, hubkquerys, hubkheaders);
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("maskedBankcardNo", "9954");
    	params.put("pageBank", "PCBC");
    	params.put("projectName", "");
    	params.put("maskedMobile", "186****2620"); 
    	params.put("needSecurityCode", "false");
    	params.put("password", "123456"); 
    	params.put("requestKey", requestKey);  
    	String resStr = HttpUtils.doPost(hubkhost, hubkpath, hubkheaders, params);
    	//Thread.currentThread().sleep(1000);
    	String actual=String.valueOf(JDBCUtil.getSelect("select * from `order` where `no`="+"'"+no+"'","state").get(0));
    	System.out.println("autal: " +actual);
    	assertEquals(actual, "success");
    }

}
