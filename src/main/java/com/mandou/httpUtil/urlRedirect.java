package com.mandou.httpUtil;

import java.net.HttpURLConnection;
import java.net.URL;

public class urlRedirect {
	/** 
     * 获取重定向地址 
     * @param path 
     * @return 
     * @throws Exception 
     */  
    public static String getRedirectUrl(String path) throws Exception {  
        HttpURLConnection conn = (HttpURLConnection) new URL(path)  
                .openConnection();  
        conn.setInstanceFollowRedirects(false);  
        conn.setConnectTimeout(5000);  
        return conn.getHeaderField("Location");  
    }  
}
