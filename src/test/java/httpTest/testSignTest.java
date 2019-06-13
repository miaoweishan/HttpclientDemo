package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mandou.httpUtil.HttpUtils;

public class testSignTest {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);
	
	public static String testSign(String reqData) {
		String host = "https://test-mobile.mandofin.com";
		String path = "/loan/testSign.json";
		Map<String, String> headers = null;
		Map<String, Object> tsparams = new HashMap<String, Object>();
		tsparams.put("data", reqData);
		String resStr;
		try {
			resStr = HttpUtils.doPost(host, path, headers, tsparams);
			JSONObject actual01 = JSON.parseObject(resStr);	
			String sign = actual01.getString("data");
			log.info("sign: "+sign);
			return sign;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}

	}

}
