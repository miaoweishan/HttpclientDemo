package httpTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.mandou.httpUtil.HttpUtils;
/*
 * 审核
 */
public class updateBorrow {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);

	/*
	 * 接单+审核
	 */
	
	public static void checkOrderTest(String noList, String username, String password) {
		//接单
		String roAurl = "http://test-chappie.mandofin.com";
		String roApath = "/workOrder/receiveOrder.json";
		Map<String, String> roAheaders = null;	
		//审核
		String upurl = "http://test-chappie.mandofin.com";
		String uppath = "/workOrder/updateBorrowLoan.json";
		Map<String, String> upheaders = null;
		
		ChappieLoginTest.chappieLogin(username, password);		
		Map<String, Object> roparams = new HashMap<String, Object>();
		roparams.put("noList", noList);
		roparams.put("state", "WAIT");		
		try {
			HttpUtils.doPost(roAurl, roApath, roAheaders, roparams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("noList", noList);
		params.put("state", "state");
		params.put("descr", "审核");		
		try {
			HttpUtils.doPost(upurl, uppath, upheaders, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
