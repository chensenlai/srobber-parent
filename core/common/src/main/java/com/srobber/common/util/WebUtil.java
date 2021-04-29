package com.srobber.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Web相关工具类
 *
 * @author chensenlai
 */
@Slf4j
public class WebUtil {

	/**
	 * 响应文本类型, UTF-8编码
	 * @param resp
	 * @param text
	 * @throws IOException
	 */
	public static void respText(HttpServletResponse resp, String text) throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(text.getBytes());
		outputStream.flush();
	}

	/**
	 * 响应html, UTF-8编码
	 * @param resp
	 * @param html
	 * @throws IOException
	 */
	public static void respHtml(HttpServletResponse resp, String html) throws IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(html.getBytes());
		outputStream.flush();
	}

	/**
	 * 响应json类型, UTF-8编码
	 * @param resp
	 * @param json
	 * @throws IOException
	 */
	public static void respJson(HttpServletResponse resp, String json) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(json.getBytes());
		outputStream.flush();
	}

	/**
	 * 响应json类型, UTF-8编码
	 * @param resp
	 * @param obj
	 * @throws IOException
	 */
	public static void respJsonObject(HttpServletResponse resp, Object obj) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		String jsonValue = JsonUtil.toStr(obj);
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(jsonValue.getBytes());
		outputStream.flush();
	}

	/**
	 * 响应XML类型, UTF-8编码
	 * @param resp
	 * @param xml
	 * @throws IOException
	 */
	public static void respXml(HttpServletResponse resp, String xml) throws IOException {
		resp.setContentType("application/xml");
		resp.setCharacterEncoding("UTF-8");
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(xml.getBytes());
		outputStream.flush();
	}

	/**
	 * 字符串url encode
	 * @param str
	 * @param charset
	 * @return
	 */
	public static String urlEncode(String str, String charset) {
		String ret = "";
		try {
			ret = URLEncoder.encode(str, charset);
		} catch (UnsupportedEncodingException e) {
			log.warn("urlEncode unSupport {}", charset, e);
			ret = str;
		}
		return ret;
	}

	/**
	 * 字符串url decode
	 * @param str
	 * @param charset
	 * @return
	 */
	public static String urlDecode(String str, String charset) {
		String ret = "";
		try {
			ret = URLDecoder.decode(str, charset);
		} catch (UnsupportedEncodingException e) {
			log.warn("urlDecode unSupport {}", charset, e);
			ret = str;
		}
		return ret;
	}

	/**
	 * 获取请求的客户端IP
	 * @param request 请求
	 * @return IP地址
	 */
	public static String getClientIp(HttpServletRequest request) {
		String clientIP = "";
		String strClientIPList; 
		strClientIPList = request.getHeader("X-Forwarded-For");
		if (StringUtil.isBlank(strClientIPList)) {
			strClientIPList = request.getHeader("X-Real-IP");
		}
		if(StringUtil.isNotBlank(strClientIPList)) {
			if(strClientIPList.indexOf(",")>-1) {
				clientIP = strClientIPList.substring(0, strClientIPList.indexOf(","));
			} else {
				clientIP = strClientIPList;
			}
		}
		
		if(StringUtil.isBlank(clientIP)) {
			strClientIPList = request.getHeader("PROXY_FORWARDED_FOR");
			if(StringUtil.isBlank(strClientIPList)) {
				clientIP = request.getRemoteAddr();
			} else {
				if(strClientIPList.contains(",")) {
					clientIP = strClientIPList.substring(0, strClientIPList.indexOf(","));
				} else {
					clientIP = strClientIPList;
				}
			}
		}
		if(!IpUtil.ipCheck(clientIP)) {
			clientIP = request.getRemoteAddr();
		}
		return clientIP;
	}

	/**
	 * 获取请求参数Map
	 * key值相同请求参数存在多个,用逗号分割
	 * @param request http请求
	 * @return map请求参数
	 */
	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		return params;
	}

	/**
	 * map请求参数转string,多个请求参数&分隔
	 * @param requestParams map请求参数
	 * @return string请求参数
	 */
	public static String getParameterStr(Map<String, String> requestParams) {
		if(requestParams==null || requestParams.size()<1) {
			return "";
		}
		StringBuilder paramStrBuf = new StringBuilder(requestParams.size()*(10+20));
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = requestParams.get(name);
			if(value == null) {
				continue;
			}
			paramStrBuf.append(name).append("=").append(value).append("&");
		}
		paramStrBuf.deleteCharAt(paramStrBuf.length()-1);
		return paramStrBuf.toString();
	}
	
	/**
	 * 获取请求body参数
	 * @param request http请求
	 * @return http请求body参数
	 */
	public static String getParamFromStream(HttpServletRequest request) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			String line = null;
			InputStream inputStream = request.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while((line = bufferedReader.readLine()) != null){
				stringBuilder.append(line);
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			log.error("getParamFromStream error.", e);
			return null;
		}
	}

	/**
	 * Http响应成功
	 * @param statusCode 响应码
	 * @return 是否成功
	 */
	public static boolean isHttpSuccessful(int statusCode) {
		return statusCode>=200 && statusCode<=299;
	}

	/**
	 * 通过user-agent判断请求客户端是否来自iOS
	 * @param request http请求
	 * @return 是否iOS请求
	 */
	public static boolean isIOS(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent.startsWith("iOS")) {
			return true;
		}
		return false;
    }

	/**
	 * 通过header判断是否Ajax请求
	 * @param request http请求
	 * @return 是否Ajax请求
	 */
	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("x-requested-with"));
	}

	/**
	 * 判断servlet是否基于http实现
	 * @param request 请求
	 * @param response 响应
	 * @return 是否http实现
	 */
	public static boolean isHttp(ServletRequest request, ServletResponse response) {
		if(request instanceof HttpServletRequest
			&& response instanceof HttpServletResponse) {
			return true;
		}
		return false;
	}
	
	private WebUtil() {}
}
