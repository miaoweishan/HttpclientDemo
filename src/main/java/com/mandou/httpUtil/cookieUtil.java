package com.mandou.httpUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class cookieUtil {
	public static CookieStore cookiestore;
	public static String setCookie = null;
	
	//存储cookie
   public static CookieStore setCookieStore(HttpResponse httpResponse) {
       System.out.println("----setCookieStore----");
       cookiestore = new BasicCookieStore();
       // JSESSIONID
       try {
    	   setCookie = httpResponse.getFirstHeader("Set-Cookie")
    	           .getValue();
       }catch(NullPointerException e){
    	   e.getMessage();
       }
       
       
       String JSESSIONID = setCookie.substring("SESSION=".length(),
           setCookie.indexOf(";"));
       System.out.println("SESSION:" + JSESSIONID);
       // 新建一个Cookie
       BasicClientCookie cookie = new BasicClientCookie("SESSION",
           JSESSIONID);
       cookie.setVersion(0);//cookie的版本
       cookie.setDomain("test-mobile.mandofin.com");//使用该cookie的host
       cookie.setPath("/");//使用该cookie的路径
       
       cookiestore.addCookie(cookie);//把新创建的cookie放到cookie管理器中
	   return cookiestore;
       
     }

}
