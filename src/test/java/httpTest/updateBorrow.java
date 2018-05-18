package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeTest;

import com.mandou.httpUtil.httpUtil;
/*
 * 审核
 */
public class updateBorrow {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);
	//接单
	private static String roAurl="http://test-chappie.mandofin.com/workOrder/receiveOrder.json";
	//审核
	private static String upurl="http://test-chappie.mandofin.com/workOrder/updateBorrowLoan.json";
	

	/*
	 * 接单+审核
	 */
	
	public static void checkOrderTest(String noList, String username, String password) {
		ChappieLoginTest.chappieLogin(username, password);
		
		Map<String, Object> roparams = new HashMap<String, Object>();
		roparams.put("noList", noList);
		roparams.put("state", "WAIT");		
		httpUtil.getHttpPost(roAurl, roparams);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("noList", noList);
		params.put("state", "state");
		params.put("descr", "审核");		
		httpUtil.getHttpPost(upurl, params);
		
	}

}
