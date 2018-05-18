package com.mandou.httpUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class httpUtil {
	public static CookieStore cookiestore = null;
	private static CloseableHttpClient httpclient = null;
	
	//实例化httpclient
	public static CloseableHttpClient gethttp() {		
		CloseableHttpClient httpclients = HttpClients.custom()
    		          .setDefaultCookieStore(cookiestore).build();
		return httpclients;
	}
	
	//发起post请求
	public static String getHttpPost(String url,Map<String, Object> params){
    	
    	String responseStr=null;
    	httpclient=httpUtil.gethttp();
 
    	HttpPost httppost=new HttpPost(url);
    	//把post请求的参数放入list集合中
    	List<NameValuePair> pairlist=new ArrayList<NameValuePair>(params.size());
    	for(Map.Entry<String, Object> entry:params.entrySet()) {
    		NameValuePair pair=new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
    		pairlist.add(pair);
    	}
    	System.out.println("请求的url："+httppost.getParams());
    	try {
    		//设置post请求的参数
			httppost.setEntity(new UrlEncodedFormEntity(pairlist,Charset.forName("utf-8")));
			//获取post请求的响应数据
			HttpResponse response=httpclient.execute(httppost);
			//获取该响应的cookies
			if(url.contains("/login.json")) {
				httpUtil.setCookieStore(response);
			}
			//获取从响应数据中的消息主题
			HttpEntity entity=response.getEntity();
			if(entity == null) {
				System.out.println("=====该接口返回值为空=====");
			}else {
				//把消息主体转换成String，便于断言时使用
				responseStr=EntityUtils.toString(entity);
//				System.out.println("----------"+cookiestore);
//				System.out.println(responseStr);
			}
						
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	try {
    		//关闭连接，释放资源
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseStr;
    }
	
	//发起get请求
	public static String getHttpGet(String url){
    	String responseStr=null;
    	httpclient=httpUtil.gethttp();
    	
    	HttpGet httpget=new HttpGet(url);
    	try {
    		
			//获取post请求的响应数据
			HttpResponse response=httpclient.execute(httpget);
			//获取该响应的cookies
			if(url.contains("/login.json")) {
				httpUtil.setCookieStore(response);
			}
						
			//获取从响应数据中的消息主题
			HttpEntity entity=response.getEntity();
			//把消息主体转换成String，便于断言时使用
			responseStr=EntityUtils.toString(entity);
			//System.out.println("----------"+cookie.httpCookie());
			System.out.println(responseStr);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
    		//关闭连接，释放资源
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseStr;
    }
	
	//存储cookie
   public static void setCookieStore(HttpResponse httpResponse) {
       System.out.println("----setCookieStore----");
       CookieStore cookiestore1 = new BasicCookieStore();
       // 获取JSESSIONID
       String setCookie = httpResponse.getFirstHeader("Set-Cookie")
           .getValue();
       //截取JSESSIONID的值
       String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
           setCookie.indexOf(";"));
       System.out.println("JSESSIONID:" + JSESSIONID);
       // 新建一个Cookie，把上面JSESSIONID值放入新建的这个cookie中
       BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
           JSESSIONID);
       //设置cookies的版本、域名、地址
       cookie.setVersion(0);
       cookie.setDomain("test-chappie.mandofin.com");
       cookie.setPath("/");
       cookiestore1.addCookie(cookie);
       cookiestore=cookiestore1;
       
     }
	   
}
