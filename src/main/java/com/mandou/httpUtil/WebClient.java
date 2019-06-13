package com.mandou.httpUtil;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * Http客户端封装类
 * 采用 CloseableHttpClient 里的方法
 * @author chenyanqing
 *
 */
public class WebClient implements Serializable {

	private static final long serialVersionUID = 1L;

	private Log logger = LogFactory.getLog(getClass());

	public static final String AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB6.6; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)";

	public static final int MAX_TOTAL_CONNETIONS = 100; // 最大连接.

	public static final String SCHEME_HTTP = "http";

	public static final String SCHEME_HTTPS = "https";
	
	private static final String ERROR_INVALID_URL = "无效的URL或未配置主机信息";

	private HttpHost host = null;

	private List<Header> headers = new ArrayList<Header>();

	private Header[] responseHeaders = null;

	private String charset = "GBK";

	private String dir = "/";

	private Map<String, Object> customParams = new TreeMap<String, Object>();

	private String userAgent = AGENT;

	private CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLSocketFactory()).build();

	private HttpClientContext context = HttpClientContext.create();

	private boolean autoRedirect = true;

	public WebClient() {
		this.host = null;
	}

	public WebClient(HttpHost host) {

		this.host = host;
	}

	public WebClient(String hostname, int port, String scheme) {

		init(hostname, port, scheme);

	}

	/**
	 * URL构造器
	 * 
	 * @param baseUrl
	 */
	public WebClient(URI baseUrl) {

		init(baseUrl.getHost(), baseUrl.getPort(), baseUrl.getScheme());

	}

	/**
	 * 设置日志对象
	 * 
	 * @param logger
	 *            the logger to set
	 */
	public void setLogger(Log logger) {
		this.logger = logger;
	}

	private void init(String hostname, int port, String scheme) {

		if (port == 80) {
			this.host = new HttpHost(hostname);
		} else {
			this.host = new HttpHost(hostname, port, scheme);
		}

		mountHttpContext();
	}

	private void mountHttpContext() {

		Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
				.register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider()).build();
		context.setCookieSpecRegistry(registry);
	}

	/**
	 * 挂载上下文对象
	 * 
	 * @param context
	 */
	public void mountHttpContext(HttpClientContext context) {

		this.context = context;
		mountHttpContext();
	}
	
	public void restoreCookie(CookieStore cookieStore) {
		context.setCookieStore(cookieStore);
	}
	
	/**
	 * 设置代理服务器
	 * 
	 * <p> 这里重新设置了RequestConfig 
	 * @param host	代理服务器主机名
	 * @param port	代理服务器端口号
	 */
	public void setProxyServer(String host, int port) {
		
		RequestConfig config = RequestConfig.custom().setProxy(new HttpHost(host, port)).build();
		getContext().setRequestConfig(config);
	}

	public void setAutoRedirect(boolean autoRedirect) {
		this.autoRedirect = autoRedirect;
	}

	/**
	 * 
	 * @param hostname
	 * @param port
	 * @param scheme
	 * @param dir
	 *            子目录需以 '/'开头并以 '/' 结尾的路径 如 '/' , '/sale/'
	 */
	public WebClient(String hostname, int port, String scheme, String dir) {

		if (port == 80) {
			this.host = new HttpHost(hostname);
		} else {
			this.host = new HttpHost(hostname, port, scheme);
		}
		this.dir = dir;

	}

	private SSLConnectionSocketFactory createSSLSocketFactory() {

		SSLContext sslcontext;
		try {
			sslcontext = SSLContext.getInstance("TLSv1");

			sslcontext.init(null, new TrustManager[] { truseAllManager }, null);

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());

			return sslsf;

		} catch (NoSuchAlgorithmException e) {
			logger.error("注册SSL失败", e);
		} catch (KeyManagementException e) {
			logger.error("注册SSL失败", e);
		}
		return null;
	}

	/**
	 * 获取http上下文对象
	 * 
	 * @return the context
	 */
	public HttpClientContext getContext() {
		return context;
	}

	/**
	 * 设置请求问
	 * 
	 * @param name
	 * @param value
	 */
	public void setHeader(String name, String value) {
		headers.add(new BasicHeader(name, value));
	}

	private void clearHeaders() {
		headers.clear();
	}

	/**
	 * 获取响应头
	 * 
	 * @return
	 */
	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public Header getResponseHeader(String name) {

		for (Header h : getResponseHeaders()) {
			if (name.equals(h.getName())) {
				return h;
			}
		}
		return null;
	}

	/**
	 * 返回编 码
	 * 
	 * @return
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * 设置编码
	 * 
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * 获取自定义参数
	 * 
	 * @return
	 */
	public Map<String, Object> getCustomParams() {
		return customParams;
	}

	/**
	 * 用GET请求HTTP服务器
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数， 可为NULL
	 * @return
	 */
	public byte[] doGet(String url) {

		logger.info("get " + url);

		HttpGet get = new HttpGet(url);

		get.setHeader(HttpHeaders.USER_AGENT, userAgent);

		if (url.startsWith("http")) {
			get.setHeader(HttpHeaders.REFERER, url);
		} else {
			get.setHeader(HttpHeaders.REFERER, host.toURI());
		}

		for (Header h : headers) {
			get.setHeader(h);
		}

		try {
			HttpResponse response = null;

			if (url.startsWith("http")) {
				response = httpClient.execute(get, context);
			} else {
				response = httpClient.execute(host, get, context);
			}
			if (response.getStatusLine().getStatusCode() >= 500) {
				throw new RuntimeException(response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			byte[] body = null;

			if (entity != null) {
				body = EntityUtils.toByteArray(entity);
			}

			responseHeaders = response.getAllHeaders();

			get.abort();
			return body;

		} catch (Exception e) {

			String msg = String.format("发送GET请求异常, url[%s]", url);

			throw new RuntimeException(msg, e);
		} finally {
			get.abort();
			clearHeaders();
		}

	}

	/**
	 * POST请求, 使用WebClient的默认编码, 可以通过调用 setCharset 改变默认编码
	 * 
	 * @param url
	 * @param postData
	 * @return
	 */
	public byte[] doPost(String url, List<NameValuePair> postData) {

		return doPost(url, postData, null);

	}

	/**
	 * Post请求， encoding为null时使用默认编码与不传凝视encoding等效， 否则使用encoding为编码
	 * 
	 * @param url
	 * @param postData
	 * @param encoding
	 * @return
	 */
	public byte[] doPost(String url, List<NameValuePair> postData, String encoding) {

		return doPost(url, postData, encoding, null);
	}

	/**
	 * Post请求， encoding为null时使用默认编码与不传凝视encoding等效， 否则使用encoding为编码
	 * 
	 * @param url
	 *            请求url
	 * @param postData
	 *            请求数据
	 * @param encoding
	 *            编码
	 * @param config
	 *            请求参数配置
	 * @return
	 */
	public byte[] doPost(String url, List<NameValuePair> postData, String encoding, RequestConfig config) {
		HttpPost post = new HttpPost(url);

		post.setHeader(HttpHeaders.USER_AGENT, userAgent);

		if (config != null) {
			post.setConfig(config);
		}

		// 处理自定义请求头
		for (Header h : headers) {
			post.setHeader(h);
		}

		try {

			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postData,
					encoding == null ? getCharset() : encoding);
			post.setEntity(formEntity);

			HttpResponse response = null;
			if (url.startsWith("http")) {
				response = httpClient.execute(post, context);
			} else if (host != null) {
				response = httpClient.execute(host, post, context);
			} else {
				throw new RuntimeException(ERROR_INVALID_URL);
			}

			byte[] body = null;
			int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode >= 500) {
				throw new RuntimeException(response.getStatusLine().toString());
			}
			if (statuscode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					body = EntityUtils.toByteArray(entity);
				}

			} else if ( // 自动处理转发
			(statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				post.abort();
				body = autoRedirect(response);
				
			}

			responseHeaders = response.getAllHeaders();
			return body;

		} catch (Exception e) {
			
			String msg = String.format("请求URL{%s}异常", url);
			
			throw new RuntimeException(msg, e);
		} finally {
			post.abort();
			clearHeaders();
		}
	}

	/**
	 * post请求json Map<String, Object> 格式 转json
	 * 
	 * @param url
	 * @param postData
	 * @param encoding
	 * @return
	 */
	public byte[] doPostJsonObj(String url, Map<String, Object> postData, String encoding) {
		RequestConfig config = RequestConfig.custom().build();

		String jsonStr = postData.toString();

		return doPostByJson(url, jsonStr, encoding, config);
	}

	/**
	 * post请求json Map<String, String> 格式 转json
	 * 
	 * @param url
	 * @param postData
	 * @param encoding
	 * @return
	 */
	public byte[] doPostJson(String url, Map<String, String> postData, String encoding) {
		RequestConfig config = RequestConfig.custom().build();

		String jsonStr = postData.toString();
		
		return doPostByJson(url, jsonStr, encoding, config);
	}

	/**
	 * post请求json JSONObject jsonObject
	 * 
	 * @param url
	 * @param jsonObject
	 * @param encoding
	 * @param config
	 * @return
	 */
	public byte[] doPostByJson(String url, String jsonstr, String encoding, RequestConfig config) {
		HttpPost post = new HttpPost(url);

		post.setHeader(HttpHeaders.USER_AGENT, userAgent);

		// 处理自定义请求头
		for (Header h : headers) {
			post.setHeader(h);
		}

		if (config != null) {
			post.setConfig(config);
		}
		try {
			StringEntity str = new StringEntity(jsonstr, encoding);
			str.setContentType("application/json; charset=utf-8");// 发送json数据需要设置contentType
			post.setEntity(str);
			
			HttpResponse response = null;
			if (url.startsWith("http")) {
				response = httpClient.execute(post, context);
			} else if (host != null) {
				response = httpClient.execute(host, post, context);
			} else {
				throw new RuntimeException(ERROR_INVALID_URL);
			}

			byte[] body = null;
			int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode >= 500) {
				throw new RuntimeException(response.getStatusLine().toString());
			}
			if (statuscode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					body = EntityUtils.toByteArray(entity);
				}

			} else if ( // 自动处理转发
			(statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {

				post.abort();
				body = autoRedirect(response);	//自动转发处理

			}

			responseHeaders = response.getAllHeaders();
			return body;

		} catch (Exception e) {
			throw new RuntimeException(String.format("请求 URL=%s 异常", url), e);
		} finally {
			post.abort();
			clearHeaders(); // 清空自定义请求问
		}

	}

	/**
	 * 自动转发处理
	 * @param response	http响应对象
	 * @return
	 */
	private byte[] autoRedirect(HttpResponse response) {

		Header[] locations = response.getHeaders("location");
		String location = null;

		if (locations != null && locations.length != 0) {
			// 多个转发地址处理
			location = locations[0].getValue();
		}
		if (location == null || location.equals("")) {
			logger.warn("无效的转发地址!");
			location = "/";
		}

		if (!location.startsWith("http") && !location.startsWith("/")) {
			// 相对路径处理

			location = dir + location;

		}

		logger.info("Redirect target: " + location);
		if (autoRedirect) {
			return doGet(location);
		} else {
			return location.getBytes();
		}
		
	}

	public byte[] doPostBytes(String url, byte[] data,RequestConfig config) {

		HttpPost post = new HttpPost(url);

		try {
			
			post.setHeader(HttpHeaders.USER_AGENT, userAgent);
			if (url.startsWith("http")) {
				post.setHeader(HttpHeaders.REFERER, url);
			} else {
				post.setHeader(HttpHeaders.REFERER, host.toURI());
			}
			
			if(config != null){
			post.setConfig(config);	
			}

			ByteArrayEntity byteEntity = new ByteArrayEntity(data);

			post.setEntity(byteEntity);

			HttpResponse response = null;
			if (url.startsWith("http")) {
				response = httpClient.execute(post, context);
			} else if (host != null) {
				response = httpClient.execute(host, post, context);
			} else {
				throw new RuntimeException(ERROR_INVALID_URL);
			}

			byte[] body = null;
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 500) {
				throw new RuntimeException(response.getStatusLine().toString());
			}
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					body = EntityUtils.toByteArray(entity);
				}
			} else if ( // 自动处理转发
			(statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {

				post.abort();
				
				body = autoRedirect(response);

			}

			responseHeaders = response.getAllHeaders();
			return body;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			clearHeaders();
			post.abort();

		}

	}

	public byte[] doPostFile(String url, Map<String, ContentBody> parts, String encoding) {

		HttpPost post = new HttpPost(url);

		try {

			post.setHeader(HttpHeaders.USER_AGENT, userAgent);

			if (url.startsWith("http")) {
				post.setHeader(HttpHeaders.REFERER, url);
			} else {
				post.setHeader(HttpHeaders.REFERER, host.toURI());
			}

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			for (Map.Entry<String, ContentBody> set : parts.entrySet()) {
				entityBuilder.addPart(set.getKey(), set.getValue());
			}

			post.setEntity(entityBuilder.build());

			HttpResponse response = null;
			if (url.startsWith("http")) {
				response = httpClient.execute(post, context);
			} else if (host != null) {
				response = httpClient.execute(host, post, context);
			} else {
				throw new RuntimeException(ERROR_INVALID_URL);
			}

			byte[] body = null;
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 500) {
				throw new RuntimeException(response.getStatusLine().toString());
			}
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					body = EntityUtils.toByteArray(entity);
				}
			} else if ( // 自动处理转发
			(statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {

				post.abort();
				
				body = autoRedirect(response);

			}

			responseHeaders = response.getAllHeaders();
			return body;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			clearHeaders();
			post.abort();

		}
	}

	public byte[] doPost(String url, Map<String, String> postData) {

		return doPost(url, postData, "UTF-8");

	}

	public byte[] doPostObj(String url, Map<String, Object> postData) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (postData != null) {
			for (Map.Entry<String, Object> entry : postData.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}

			logger.info("url:" + url + "\r\n Post参数:" + params.toString());
		}

		return doPost(url, params, "UTF-8");

	}
	
	public byte[] doPost(String url, Map<String, String> postData, String encoding) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (postData != null) {
			for (Map.Entry<String, String> entry : postData.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			logger.info("url:" + url + "\r\n Post参数:" + params.toString());
		}

		return doPost(url, params, encoding);

	}
	
	public  byte[] uploadFile(String url, String filePath, RequestConfig config) {
		return uploadFile(url, new File(filePath), config);
	}
	
	public  byte[] uploadFile(String url, File file, RequestConfig config) {
		
		HttpPost httpPost = new HttpPost(url);
		
		if (config != null) {
			httpPost.setConfig(config);
		}
		logger.info("file请求地址："+url);
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		
		multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));

		// 当设置了setSocketTimeout参数后，以下代码上传PDF不能成功，将setSocketTimeout参数去掉后此可以上传成功。上传图片则没有个限制
		// multipartEntityBuilder.addBinaryBody("file",file,ContentType.create("application/octet-stream"),"abd.pdf");
		multipartEntityBuilder.addBinaryBody("file", file);
		HttpEntity httpEntity = multipartEntityBuilder.build();
		httpPost.setEntity(httpEntity);

		byte [] bytes = null;
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity responseEntity = httpResponse.getEntity();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == 200 && responseEntity != null) {
				bytes = EntityUtils.toByteArray(responseEntity);
			}
			httpClient.close();
			if (httpResponse != null) {
				httpResponse.close();
			}
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// SSL handler (ignore untrusted hosts)对不信任的证书站点处理
	private static TrustManager truseAllManager = new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null; // 简单的返回null表示验证通过
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	};

	/**
	 * MAP请求对象转换为URL，
	 * 
	 * @param url
	 *            页面
	 * @param req_data
	 *            请求MAP数据
	 * @param urlencoding
	 *            URL编码时所使用的字符串编码，null时不使用URL编码
	 * @return
	 */
	public static String reqData2Url(String url, Map<String, String> req_data, String urlencoding) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : req_data.entrySet()) {
			if (urlencoding != null) {
				try {
					sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), urlencoding) + "&");
				} catch (UnsupportedEncodingException e) {
				}
			} else {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}

		String data = sb.toString();
		if (data.endsWith("&")) {
			data = data.substring(0, data.length() - 1);
		}

		return url + "?" + data;
	}

	/**
	 * 将URL串参数转换为MAP表示
	 * 
	 * @param keyvalues
	 * @param urlEncoding
	 * @return
	 */
	public static Map<String, String> url2map(String keyvalues) {

		Map<String, String> map = new HashMap<String, String>();

		String[] keyvalueArray = keyvalues.split("\\&");
		for (String keyvalue : keyvalueArray) {
			String[] s = StringUtils.split(keyvalue, "\\=");
			if (s == null || s.length == 0) {
				continue;
			} else if (s.length >= 2) {
				map.put(s[0], s[1]);
			} else if (s.length == 1) {
				map.put(s[0], null);
			}
		}
		return map;
	}

	/**
	 * Map请求参数集合转化为QueryString 表示形式
	 * 
	 * @param req_data
	 * @param urlencoding
	 *            参数值需要经过URL编码， 如果不需要则传递null
	 * @return
	 */
	public static String map2QueryString(Map<String, String> req_data, String urlencoding) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : req_data.entrySet()) {
			if (urlencoding != null) {
				try {
					sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), urlencoding) + "&");
				} catch (UnsupportedEncodingException e) {
				}
			} else {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}

		String data = sb.toString();
		if (data.endsWith("&")) {
			data = data.substring(0, data.length() - 1);
		}

		return data;
	}

	/**
	 * Map请求参数集合转化为QueryString 表示形式
	 * 
	 * @param req_data
	 * @return
	 */
	public static String map2QueryString(Map<String, String> req_data) {
		return map2QueryString(req_data, null);
	}
	
	public static void main(String[] args) {
		String url = "https://test-mobile.mandofin.com/upload/baseSave.json";
		String filePath = "E:\\mvn\\新123.jpg";
		WebClient client = new WebClient();
		byte[] uploadFile = client.uploadFile(url, filePath, null);
		System.out.println(new String(uploadFile));
	}

}
