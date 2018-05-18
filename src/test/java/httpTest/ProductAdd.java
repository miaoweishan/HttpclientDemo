//package httpTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.DataProvider;
//
//import com.mandou.tools.JDBCUtil;
//import com.mandou.tools.TransferDataExcel;
///*
// * 产品打包，发布
// */
//public class ProductAdd {
//	private static Logger log = Logger.getLogger(ProductAdd.class);
//	private static String padurl="http://test-chappie.mandofin.com/product/add.json";
//	
//	//读取Excel表格中测试数据
//	@DataProvider
//	public static Object[][] testCasedata(){
//		Object[][] data = TransferDataExcel.getObjectData("5");
//	 	//System.out.println(data.toString());
//		return data;
//		
//	}
//	
//	@BeforeTest
//	public void planIsExist() {
//		ChappieLoginTest.chappieLogin("admin", "123456");;
//	}
//	
//	public static void ProductAddTest(Object productname,Object outoutsideno) {
//		
//		String principal= JDBCUtil.getSelect("select * from disburse_evidence where loanOrderId =(select id from loan_order_direct where outsideNo='"+outoutsideno+"')","principal").get(0).toLowerCase();
//		String rate= JDBCUtil.getSelect("select * from disburse_evidence where loanOrderId =(select id from loan_order_direct where outsideNo='"+outoutsideno+"')","rate").get(0).toLowerCase();
//		String no= JDBCUtil.getSelect("select * from disburse_evidence where loanOrderId =(select id from loan_order_direct where outsideNo='"+outoutsideno+"')","no").get(0).toLowerCase();
//		String termNum= JDBCUtil.getSelect("select * from disburse_evidence where loanOrderId =(select id from loan_order_direct where outsideNo='"+outoutsideno+"')","termNum").get(0).toLowerCase();
//	
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("type", "periodical");
//		params.put("planNo", planNo);
//		params.put("name", productname);
//		params.put("flag", "");
//		params.put("category", "QUALITY");
//		params.put("capitalDetailType", "person_consume");
//		params.put("raise", principal);
//		params.put("mode", "loan");
//		params.put("productGroup", "");
//		params.put("disburseName", disburseName);
//		params.put("rePaymentType", "debtType");
//		params.put("rate", rate);
//		params.put("maxAmount", maxAmount);
//		params.put("unit", "day");
//		params.put("interval", termNum);	
//		params.put("minAmount", 1);
//		params.put("previewtTime", "");
//		params.put("raisingTime", "");
//		params.put("productDetails", "[]");
//		params.put("productSecuritys", "[]");
//		params.put("no", no);
//		params.put("commonLabel", "");
//		params.put("hotLabel", "");
//		
//		params.put("vipAppendRate", 1);
//		params.put("role", "ORDINARY");
//		params.put("estimateSuccess", estimateSuccess);
//		params.put("beginInterest", beginInterest);
//		params.put("estimateCeaseDate", estimateCeaseDate);
//		params.put("estimateExpireDate", estimateExpireDate);
//		params.put("appendRate", "");
//		params.put("recommend", 1);
//		params.put("attorn", 0);
//		params.put("is_use_coupon", 1);
//		params.put("tag", "ORDINARY");
//		params.put("borrowType", "CONSUMERCREDIT");
//		params.put("info", "");
//	}
//
//}
