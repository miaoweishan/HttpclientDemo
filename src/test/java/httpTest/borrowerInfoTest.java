package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.mandou.httpUtil.httpsUtil;
/*
 * 添加问卷信息
 */
public class borrowerInfoTest {
	private static Logger log = Logger.getLogger(borrowerInfoTest.class);
	private static String brrurl = "https://test-mobile.mandofin.com/loan/borrowerInfo.json";
	private static String qnurl="https://test-mobile.mandofin.com/loan/questionnaire.json";
	
	/*
	 * 消费金融类问卷
	 */
	public void userByBorrowerInfo(String platformNo, String reqData) {
		String sign = testSignTest.testSign(reqData);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformNo", platformNo);
		params.put("reqData", reqData);
		params.put("Sign", sign);
		
		String resactual=httpsUtil.getHttpsPost(brrurl, params);
		JSONObject resactual01 = new JSONObject(resactual);
		String actual = resactual01.getString("success");
		if(actual.equals("false")) {
			log.debug("生成问卷失败： "+resactual);
		}
	}
	
	/*
	 * 车贷类问卷
	 */
	public void questionnaire(String platformNo, String reqData) {
		String sign = testSignTest.testSign(reqData);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformNo", platformNo);
		params.put("reqData", reqData);
		params.put("Sign", sign);
		
		String resactual=httpsUtil.getHttpsPost(qnurl, params);
		JSONObject resactual01 = new JSONObject(resactual);
		String actual = resactual01.getString("success");
		if(actual.equals("false")) {
			log.debug("生成问卷失败： "+resactual);
		}
	}
	

}
