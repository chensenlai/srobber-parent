package com.srobber.common.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.srobber.common.exeption.WrapException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * http请求客户端
 *
 * @author chensenlai
 */
@Slf4j
public class HttpClient {

	/**
	 * Thread safe
	 */
	private static final CloseableHttpClient CLIENT = HttpClients.createDefault();
	/**
	 * http client默认配置
	 */
	private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
			.setConnectionRequestTimeout(1000)
			.setConnectTimeout(5000)
			.setSocketTimeout(10000)
			.build();

	private static final Map<String, String> DEFAULT_HEADER = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			this.put(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
			this.put(CONNECTION, DEFAULT_CONNECTION);
		}
	};

	/**
	 * 系统字符集枚举
	 */
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	/**
	 * 系统Content-Type枚举
	 */
	public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";
	public static final String APPLICATION_JSON = "application/json; charset=utf-8";
	public static final String APPLICATION_XML = "application/xml; charset=utf-8";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data; charset=utf-8";
	public static final String TEXT_HTML = "text/html; charset=utf-8";
	public static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	public static final String TEXT_XML = "text/xml; charset=utf-8";
	/**
	 * 系统Connection枚举
	 */
	public static final String CLOSE = "Close";
	public static final String KEEP_ALIVE = "Keep-Alive";

	public static final Charset DEFAULT_CHARSET = CHARSET_UTF8;

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String DEFAULT_CONTENT_TYPE = APPLICATION_FORM_URLENCODED;

	public static final String CONNECTION = "Connection";
	public static final String DEFAULT_CONNECTION = CLOSE;

	public static HttpResult<String> post(String url, String body) {
		return doPost(url, null, body, null);
	}

	public static HttpResult<String> post(String url, String body, Charset charset) {
		return doPost(url, null, body, charset);
	}

	public static HttpResult<String> post(String url, Map<String, String> headerMap, String body, Charset charset) {
		return doPost(url, headerMap, body, charset);
	}

	/**
	 * 发送http POST请求
	 * @param url	请求地址
	 * @param headerMap	请求头部，同名会覆盖默认的
	 * @param body		请求body部分数据，会根据header的Content-Type指定编码进行编码
	 * @param charset 响应内容字符集
	 * @return 响应结果
	 */
	private static HttpResult<String> doPost(String url, Map<String, String> headerMap,
											 String body, Charset charset) {
		HttpPost post = new HttpPost(url);
		post.setConfig(REQUEST_CONFIG);
		for(Map.Entry<String, String> header : DEFAULT_HEADER.entrySet()) {
			post.setHeader(header.getKey(), header.getValue());
		}
		if(headerMap != null) {
			for(Map.Entry<String, String> header : headerMap.entrySet()) {
				post.setHeader(header.getKey(), header.getValue());
			}
		}
		//请求内容类型
		Header contentTypeHeader = post.getLastHeader(CONTENT_TYPE);
		if(contentTypeHeader == null) {
			contentTypeHeader = new BasicHeader(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
			post.addHeader(contentTypeHeader);
		}
		//响应内容字符集
		if(charset == null) {
			charset = DEFAULT_CHARSET;
		}
		StringEntity entity = new StringEntity(body, ContentType.parse(contentTypeHeader.getValue()));
		post.setEntity(entity);
		try {
			HttpResponse httpResponse = CLIENT.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = EntityUtils.toString(httpResponse.getEntity(), charset);
			if(statusCode != 200) {
				log.warn("not 200: {}", httpResponse.getStatusLine());
			}
			return new HttpResult<String>(statusCode, content);
		} catch (Exception e) {
			log.warn("http POST {} {} error. {}", url, body, e.getMessage());
			throw new WrapException(e);
		} finally {
			post.releaseConnection();
		}
	}

	public static HttpResult<String> get(String url) {
		return doGet(url, null, null);
	}

	public static HttpResult<String> get(String url, Charset charset) {
		return doGet(url, null, charset);
	}

	public static HttpResult<String> get(String url, Map<String, String> headerMap, Charset charset) {
		return doGet(url, headerMap, charset);
	}

	/**
	 * 发送http GET请求
	 * @param url	请求地址
	 * @param headerMap	请求头部，同名会覆盖默认的
	 * @param charset 响应内容字符集
	 * @return 响应结果
	 */
	private static HttpResult<String> doGet(String url, Map<String, String> headerMap,
											Charset charset) {
		HttpGet get = new HttpGet(url);
		get.setConfig(REQUEST_CONFIG);
		for(Map.Entry<String, String> header : DEFAULT_HEADER.entrySet()) {
			get.setHeader(header.getKey(), header.getValue());
		}
		if(headerMap != null) {
			for(Map.Entry<String, String> header : headerMap.entrySet()) {
				get.setHeader(header.getKey(), header.getValue());
			}
		}
		//请求内容类型
		Header contentTypeHeader = get.getLastHeader(CONTENT_TYPE);
		if(contentTypeHeader == null) {
			contentTypeHeader = new BasicHeader(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
			get.addHeader(contentTypeHeader);
		}
		//响应内容字符集
		if(charset == null) {
			charset = DEFAULT_CHARSET;
		}
		try {
			HttpResponse httpResponse = CLIENT.execute(get);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = EntityUtils.toString(httpResponse.getEntity(), charset);
			if(statusCode != 200) {
				log.warn("not 200: {}", httpResponse.getStatusLine());
			}
			return new HttpResult<String>(statusCode, content);
		} catch (Exception e) {
			log.error("http GET {} error. {}", url, e.getMessage());
			throw new WrapException(e);
		} finally {
			get.releaseConnection();
		}
	}

	public static HttpResult<byte[]> getFile(String url) throws IOException {
		return doGetFile(url, null);
	}

	/**
	 * 发送GET请求文件
	 * @param url		请求地址
	 * @param headerMap	请求头部，同名会覆盖默认的
	 * @return 响应结果
	 */
	private static HttpResult<byte[]> doGetFile(String url, Map<String, String> headerMap) {
		HttpGet get = new HttpGet(url);
		get.setConfig(REQUEST_CONFIG);
		for(Map.Entry<String, String> header : DEFAULT_HEADER.entrySet()) {
			get.setHeader(header.getKey(), header.getValue());
		}
		if(headerMap != null) {
			for(Map.Entry<String, String> header : headerMap.entrySet()) {
				get.setHeader(header.getKey(), header.getValue());
			}
		}
		//请求内容类型
		Header contentTypeHeader = get.getLastHeader(CONTENT_TYPE);
		if(contentTypeHeader == null) {
			contentTypeHeader = new BasicHeader(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
			get.addHeader(contentTypeHeader);
		}
		try {
			HttpResponse httpResponse = CLIENT.execute(get);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			byte[] content = EntityUtils.toByteArray(httpResponse.getEntity());
			if(statusCode != 200) {
				log.warn("not 200: {}", httpResponse.getStatusLine());
			}
			return new HttpResult<byte[]>(statusCode, content);
		} catch (Exception e) {
			log.error("http GET File {} error. {}", url, e.getMessage());
			throw new WrapException(e);
		} finally {
			get.releaseConnection();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class HttpResult<T> {
		/**
		 * 响应码
		 */
		private int statusCode;
		/**
		 * 响应内容
		 */
		private T content;
	}

	public HttpClient() {}
}
