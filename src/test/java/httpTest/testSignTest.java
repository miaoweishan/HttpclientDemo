package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mandou.httpUtil.httpsUtil;

public class testSignTest {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);
	private static String TSurl="https://test-mobile.mandofin.com/loan/testSign.json";
	
	public static String testSign(String reqData) {
		Map<String, Object> tsparams = new HashMap<String, Object>();
		tsparams.put("data", reqData);
		String resStr=httpsUtil.getHttpsPost(TSurl, tsparams);
		JSONObject actual01 = new JSONObject(resStr);	
		String sign = actual01.getString("data");
		log.info("sign: "+sign);
		return sign;
		
	}

}
