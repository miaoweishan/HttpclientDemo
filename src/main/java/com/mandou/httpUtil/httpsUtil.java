package com.mandou.httpUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import httpTest.TestDemo;

@SuppressWarnings("deprecation")
public class httpsUtil {
	 private static Logger log=Logger.getLogger(TestDemo.class);
	 private static PoolingHttpClientConnectionManager poolConnManager=null;
	 public static HttpClient httpclient;
	 //public static CookieStore cookiestore = null;
	 
	 static int maxTotalPool=200;
	 static int maxConPerRoute=200;
	 static int socketTimeout=600;
	 static int connectionRequestTimeout=600;
	 static int connectTimeout=600;//设置网络连接超时
	
	public void init()  
    {  
         try {  
        	 System.out.println("+++++++++++++++++++++++++++++++++");
        	 //实现SSL协议
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,  
                            new TrustSelfSignedStrategy())  
                    .build();  
            //关闭host验证，允许和所有的host建立SSL通信;HostnameVerifier-此类是用于主机名验证的基接口
            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;  
            
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                    sslcontext,hostnameVerifier);
            
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())  
                    .register("https", sslsf)  
                    .build();  
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
            
			// Increase max total connection to 200  
            poolConnManager.setMaxTotal(maxTotalPool);  
            // Increase default max connection per route to 20  
            poolConnManager.setDefaultMaxPerRoute(maxConPerRoute);  
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();  
            poolConnManager.setDefaultSocketConfig(socketConfig);  
        } catch (Exception e) {  
            log.error("InterfacePhpUtilManager init Exception"+e.toString());  
        }  
    }  
    public static CloseableHttpClient getConnection()  
    {   //创建一个连接池，并对请求超时进行设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)  
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();  
        CloseableHttpClient httpClient = HttpClients.custom()  
                    .setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig)
                    .setDefaultCookieStore(cookieUtil.cookiestore).disableAutomaticRetries().build();  
        
        if(poolConnManager!=null&&poolConnManager.getTotalStats()!=null)  
        {  
            log.info("now client pool "+poolConnManager.getTotalStats().toString());  
            
        }  
      
        return httpClient;  
    }  
    
  //发起get请求
  	public static String getHttpsGet(String url){
      	String responseStr=null;
      	//实例化httpclient      	
      	httpclient = getConnection();      	
      	HttpGet httpget=new HttpGet(url);
      	try {
      		
  			//获取post请求的响应数据
  			HttpResponse response=httpclient.execute(httpget);
  			
  			//获取该响应的cookies
  			if(url.contains("/login.json")) {
  				cookieUtil.setCookieStore(response);
  			}
  						
  			//获取从响应数据中的消息主题
  			HttpEntity entity=response.getEntity();
  			//把消息主体转换成String，便于断言时使用
  			responseStr=EntityUtils.toString(entity);
  			//System.out.println("----------"+cookie.httpCookie());
  			//System.out.println(responseStr);
  			
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
      	
      	
  		return responseStr;
      }
  
  	//发起post请求
  	public static String getHttpsPost(String url,Map<String, Object> params){
      	
      	String responseStr=null;
      	httpclient = getConnection();//实例化httpclient
      	HttpPost httppost=new HttpPost(url);//向服务器发起post请求
      	log.info("请求的url： "+url);
      	//把post请求的参数放入list集合中
      	List<NameValuePair> pairlist=new ArrayList<NameValuePair>(params.size());
      	for(Map.Entry<String, Object> entry:params.entrySet()) {
      		//System.out.println("   "+entry.getKey()+"      "+entry.getValue().toString());
      		//把这些参数转换成符合post请求键值对格式
      		NameValuePair pair=new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
      		pairlist.add(pair);
      	}
      	
      	try {
      		//把请求的参数也发给服务器
  			httppost.setEntity(new UrlEncodedFormEntity(pairlist,Charset.forName("utf-8")));
  			//获取post请求的响应数据
  			HttpResponse response=httpclient.execute(httppost);
  			//获取该响应的cookies
  			if(url.contains("/login.json")) {
  				cookieUtil.setCookieStore(response);
  			}
  			//获取从响应数据中的消息主题
  			HttpEntity entity=response.getEntity();
  			if(entity == null) {
  				System.out.println("=====该接口返回值为空=====");
  			}else {
  				//把消息主体转换成String，便于断言时使用
  				responseStr=EntityUtils.toString(entity);
  				//System.out.println("----------"+cookiestore);
  				//System.out.println(responseStr);
  			}
  			
  			httppost.clone();
  			
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
  		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	
  		return responseStr;
      }
    
}
