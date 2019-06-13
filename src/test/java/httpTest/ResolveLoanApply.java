package httpTest;

import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.mandou.httpUtil.HttpUtils;
import com.mandou.tools.DateFormat;
import com.mandou.tools.JDBCUtil;
import com.mandou.tools.TransferDataExcel;
/*
 * 发起借款申请
 */
public class ResolveLoanApply {
	private static Logger log = Logger.getLogger(ResolveLoanApply.class);
			
	@DataProvider
	public static Object[][] testCaseData(){
		Object[][] data = TransferDataExcel.getObjectData("4");
		return data;	
	}
	
	@BeforeTest()
	public void a() {
		Object[][] cpdata = ResolveLoanApply.testCaseData();

		for(int i=0;i<cpdata.length;i++) {
			String mb = cpdata[i][4].toString();	
			// 删除上传资料包解析数据
			JDBCUtil.setDelete("DELETE from loan_apply_img where applyNo=(select `no` from loan_apply where outsideNo='"+mb+"')");	
			// 删除借款申请详情
			JDBCUtil.setDelete("DELETE from loan_apply_detail where applyId=(select id from loan_apply where outsideNo='"+mb+"')");
			// 删除审核工单
			JDBCUtil.setDelete("delete from work_order where orderNo=(select `no` from loan_order_direct where outsideNo='"+mb+"')");
			// 删除支用凭证
			JDBCUtil.setDelete("DELETE from disburse_evidence where loanOrderId=(select id from loan_order_direct where outsideNo='"+mb+"')");
			// 删除支用订单信息
			JDBCUtil.setDelete("DELETE from loan_order_direct where outsideNo='"+mb+"'");
			// 删除借款申请工单
			JDBCUtil.setDelete("DELETE from loan_apply where outsideNo='"+mb+"'");
			
		}
		
	}
	
	@Test(dataProvider="testCaseData")
	public void resolveLoanApolyTset(Object caseid,Object host,Object path,Object platformNo,Object mobile,Object rate,Object outsideNo,Object loanTerm,Object money,Object expected) {
		Gson gson = new Gson();
		Map<String, String> headers = null;
		
		String accountNo = JDBCUtil.getSelect("select * from account where userId=(select id from `user` where mobile="+"'"+mobile+"') and role='BORROWERS'", "no").get(0).toString();	
		String applyIdCard = JDBCUtil.getSelect("select * from `user` where mobile="+"'"+mobile+"'", "idCard").get(0).toString();
		String name = JDBCUtil.getSelect("select * from `user` where mobile="+"'"+mobile+"'", "realname").get(0).toString();
		String applyBankCard = JDBCUtil.getSelect("select * from bank_card b INNER JOIN account a on b.accountNo=a.`no` INNER JOIN `user` u on b.userId=u.id where a.role='BORROWERS' and u.mobile="+"'"+mobile+"'", "b.account").get(0).toString();
		log.info("accountNo: "+accountNo);
		Date date = new Date();
		String simpledate = DateFormat.dateRevise(date, "yyyyMMddHHmmss");
		
		Map<String, Object> reqDateparams = new HashMap<String, Object>();
		reqDateparams.put("repaymentMethod", "debtType");
		reqDateparams.put("rate", rate.toString());
		reqDateparams.put("periods", "1");
		reqDateparams.put("outsideNo", outsideNo.toString());
		reqDateparams.put("applyIdCard", applyIdCard.toString());
		reqDateparams.put("loanTerm", loanTerm.toString());
		reqDateparams.put("money", money.toString());
		reqDateparams.put("name", name.toString());
		reqDateparams.put("applyBankCard", applyBankCard.toString());
		reqDateparams.put("dueDate", "20180126091212");
		reqDateparams.put("repurchaseDate", simpledate.toString());
		reqDateparams.put("licensePlate", "浙A201608888");
		reqDateparams.put("vin", "LFV388888");
		reqDateparams.put("applyDate", simpledate.toString());
		reqDateparams.put("accountNo", accountNo.toString());
		reqDateparams.put("mobile", mobile.toString());
		reqDateparams.put("cartype", "雪佛兰");
		reqDateparams.put("engineNumber", "YZZ2222222");
		reqDateparams.put("insurance", "0");
		String reqData = gson.toJson(reqDateparams);
		log.info("reqData: "+reqData);;
		
		String sign = testSignTest.testSign(reqData);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformNo", platformNo);
		params.put("reqData", reqData);
		params.put("Sign", sign);		
//		params.put("protocolPdf", value);
		
		String resStrIS;
		try {
			resStrIS = HttpUtils.doPost(host.toString(), path.toString(), headers, params);
			JSONObject actualIS01 = JSON.parseObject(resStrIS);	
			log.info("actualIS01: " + actualIS01);
			String actualIS = actualIS01.get("success").toString();
			log.info("actualIS: " + actualIS);
			assertEquals(actualIS, expected.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	

}
