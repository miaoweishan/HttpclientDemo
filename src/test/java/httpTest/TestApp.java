package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mandou.httpUtil.httpsUtil;
import com.mandou.httpUtil.urlRedirect;

public class TestApp {
	String json4 = null;
	@BeforeTest
	public void mandoLogin() {
		MandoLoginTest.MandoAppLogin("18667112620", "a123456");
		
		Map<String, Object> mapparams = new HashMap<String, Object>();
		mapparams.put("money", "100");
		String a = httpsUtil.getHttpsPost("https://test-mobile.mandofin.com/user/verifyPassword.json", mapparams);
		JSONObject json1 = new JSONObject(a);
		String json2 = json1.get("data").toString();
		JSONObject json3 = new JSONObject(json2);
		json4 = json3.get("pgRetUrl").toString();
		
		
		System.out.println(json4);
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
