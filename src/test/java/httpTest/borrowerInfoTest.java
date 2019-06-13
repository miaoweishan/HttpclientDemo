package httpTest;

import static org.testng.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mandou.httpUtil.HttpUtils;
/*
 * 添加问卷信息
 */
public class borrowerInfoTest {
	private static Logger log = Logger.getLogger(borrowerInfoTest.class);
	
	/*
	 * 消费金融类问卷
	 */
	public void userByBorrowerInfo(String platformNo, String reqData) {
		String host = "https://test-mobile.mandofin.com";
		String path = "/loan/borrowerInfo.json";
		Map<String, String> headers = null;
		String sign = testSignTest.testSign(reqData);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformNo", platformNo);
		params.put("reqData", reqData);
		params.put("Sign", sign);
		
		String resactual;
		try {
			resactual = HttpUtils.doPost(host, path, headers, params);
			JSONObject resactual01 = JSON.parseObject(resactual);
			String actual = resactual01.getString("success");
			if(actual.equals("false")) {
				log.debug("生成问卷失败： "+resactual);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 车贷类问卷
	 */
	public void questionnaire(String platformNo, String reqData) {
		String host = "https://test-mobile.mandofin.com";
		String path = "/loan/questionnaire.json";
		Map<String, String> headers = null;
		String sign = testSignTest.testSign(reqData);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformNo", platformNo);
		params.put("reqData", reqData);
		params.put("Sign", sign);
		
		String resactual;
		try {
			resactual = HttpUtils.doPost(host, path, headers, params);
			JSONObject resactual01 = JSON.parseObject(resactual);
			String actual = resactual01.getString("success");
			if(actual.equals("false")) {
				log.debug("生成问卷失败： "+resactual);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
