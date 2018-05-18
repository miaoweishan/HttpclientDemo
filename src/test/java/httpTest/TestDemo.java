package httpTest;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//http://bsr1983.iteye.com/blog/2211102
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mandou.httpUtil.httpsUtil;
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
    public void Login(Object caseid,Object mobile,Object password,Object expected) {
    	String url="https://test-mobile.mandofin.com/user/login.json";
    	//把请求的参数放到Map集合里
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("mobile", mobile);
    	params.put("password", password);  	
    	String resStr = httpsUtil.getHttpsPost(url, params);//
    	System.out.println("resStr: "+resStr);
    	
    	JSONObject actual01=new JSONObject(resStr);
    	String actual=actual01.get("success").toString();
    	assertEquals(actual, expected.toString());
    }
    
    @Test(priority=1)
    public void Submit() {
    	String url="https://test-mobile.mandofin.com/payment/submit.json";
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("money", "10");
    	params.put("ot", "INCOME"); 
    	params.put("payPass", "258147");
    	String resStr = httpsUtil.getHttpsPost(url, params);
    	System.out.println("resStr: "+resStr); 
    	JSONObject act1=new JSONObject(resStr);
    	String model = act1.get("model").toString();
    	JSONObject act2=new JSONObject(model);
    	no=act2.get("no").toString();//订单号
    }
    
    @Test(priority=2)
    public void paymentOrde() {
    	String url="https://test-mobile.mandofin.com/payment/paymentOrder.json";
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("amount", "10");
    	params.put("cardId", "5036");
    	params.put("method", "BANK");
    	params.put("orderNo", no); 
    	params.put("terminalType", "IOS");
    	params.put("type", "CHARGE_PAYMENT"); 
    	
    	String resStr = httpsUtil.getHttpsPost(url, params);
    	System.out.println("resStr: "+resStr); 
    	JSONObject act1=new JSONObject(resStr);
    	String data = act1.get("data").toString();
    	JSONObject act2=new JSONObject(data);
    	String paymentResult=act2.get("paymentResult").toString();
    	JSONObject act3=new JSONObject(paymentResult);
    	forwardUrl=act3.get("forwardUrl").toString();
    }
    
    @Test(priority=3)
    public void rechargeSwift() throws Exception {
    	String url="https://hubk.lanmaoly.com/bha-neo-app/gateway/mobile/recharge/rechargeSwift.do";
    	String requestKey=null;
    	String Locurl = urlRedirect.getRedirectUrl(forwardUrl);
    	String[] values = Locurl.split("=");
    	requestKey = values[1];
    	httpsUtil.getHttpsGet(Locurl);
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("maskedBankcardNo", "9954");
    	params.put("pageBank", "PCBC");
    	params.put("projectName", "");
    	params.put("maskedMobile", "186****2620"); 
    	params.put("needSecurityCode", "false");
    	params.put("password", "123456"); 
    	params.put("requestKey", requestKey);  
    	String resStr = httpsUtil.getHttpsPost(url, params);
    	//Thread.currentThread().sleep(1000);
    	String actual=String.valueOf(JDBCUtil.getSelect("select * from `order` where `no`="+"'"+no+"'","state").get(0));
    	System.out.println("autal: " +actual);
    	assertEquals(actual, "success");
    }

}
