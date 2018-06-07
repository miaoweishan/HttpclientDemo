package com.mandou.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/*
 * fastjson 方法
 */
public class FastJsonUtil {
	/** 
	 
	    * 
	 
	    * @param responseJson ,这个变量是拿到响应字符串通过json转换成json对象 
	 
	    * @param jpath,这个jpath指的是用户想要查询json对象的值的路径写法 
	 
	    * jpath写法举例：1) per_page  2)data[1]/first_name ，data是一个json数组，[1]表示索引 
	 
	    * /first_name 表示data数组下某一个元素下的json对象的名称为first_name 
	 
	    * @return，返回first_name这个json对象名称对应的值 
	 
	    */  	  
	   public static String getValueByJPath(String StrJson, String jpath){  
	  
		  //创建Json对象，把上面字符串序列化成Json对象   
		  JSONObject responseJson = JSON.parseObject(StrJson);
	      Object obj = responseJson;  
	  
	      for(String s : jpath.split("/")) {  
	  
	        if(!s.isEmpty()) {  
	  
	           if(!(s.contains("[") || s.contains("]"))) {  
	  
	              obj = ((JSONObject) obj).get(s);  
	  
	           }else if(s.contains("[") || s.contains("]")) {  
	  
	              obj =((JSONArray)((JSONObject)obj).get(s.split("\\[")[0])).get(Integer.parseInt(s.split("\\[")[1].replaceAll("]", "")));  
	  
	           }  
	        }  
	      }
	      return obj.toString();  
	   }
	   
	   public static void main(String[] args) {
		   String responseStr="{\"success\":true,\"items\":[{\"id\":424,\"createDate\":\"2017-09-19 15:45\",\"modifyDate\":\"2018-04-26 10:54\",\"title\":\"庆国庆\",\"sequence\":2,\"bannerUrl\":\"www.baidu.com\",\"picture\":\"dfs/group1/M00/00/0D/wKgAGFoNXRaAcnnLAAmSwcgvTNs127.jpg\",\"message\":\"2222\",\"type\":{\"text\":\"移动端通用\",\"value\":\"ALL\",\"name\":\"TerminalType\"},\"terminal\":\"MOBILE\",\"bgColor\":\"\",\"useTo\":\"MARK_NOTICE\",\"useTypeName\":null,\"share\":false,\"icon\":\"\",\"shareTitle\":\"\",\"shareMessage\":\"\",\"shareUrl\":\"\",\"releaseTime\":null,\"releaseNow\":true,\"markContent\":\"国庆节即将到来！！！\"}]}";
//		   String jpath="success";
		   String jpath="items[0]/type/text";

		   String a = FastJsonUtil.getValueByJPath(responseStr, jpath);
		   System.out.println("------------ "+a);
	   }
}
