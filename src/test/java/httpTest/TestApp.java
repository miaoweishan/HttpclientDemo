package httpTest;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mandou.httpUtil.HttpUtils;
import com.mandou.httpUtil.urlRedirect;

public class TestApp {
	String json4 = null;
	@BeforeTest
	public void mandoLogin() {
		String host = "https://test-mobile.mandofin.com";
		String path = "/user/verifyPassword.json";
		Map<String, String> headers = null;
		MandoLoginTest.MandoAppLogin("18667112620", "a123456");
		
		Map<String, Object> mapparams = new HashMap<String, Object>();
		mapparams.put("money", "100");
		String a;
		try {
			a = HttpUtils.doPost(host, path, headers, mapparams);
			JSONObject json1 = JSON.parseObject(a);
			String json2 = json1.get("data").toString();
			JSONObject json3 = JSON.parseObject(json2);
			json4 = json3.get("pgRetUrl").toString();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void getUrl() {
		try {
			String mm = urlRedirect.getRedirectUrl(json4);
			System.out.println(mm);
			String[] dd = mm.split("=");
			String requestKey = dd[1];
			System.out.println(requestKey);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
