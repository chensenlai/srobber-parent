package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * IP地址转化工具类
 * 建议数据库存储用int存储IP, 到java内部转成点分十进制String
 *
 * @author chensenlai
 */
@Slf4j
public class IpUtil {

	/**
	 * IP地址int类型转成点分十进制字符串
	 * @param ipInt 整型ip
	 * @return 点分十进制字符串
	 */
	public static String ipInt2Str(int ipInt) {
	     final StringBuilder sb = new StringBuilder();
	     sb.append(String.valueOf(ipInt>>>24)).append(".");
	     sb.append(String.valueOf((ipInt&0xFFFFFF)>>>16 )).append(".");
	     sb.append(String.valueOf((ipInt&0xFFFF)>>>8 )).append(".");
	     sb.append(String.valueOf(ipInt&0xFF));
	     return sb.toString();
	}

	/**
	 * IP地址点分十进制String转成int类型
	 * @param ipStr 点分十进制字符串
	 * @return 整型ip
	 */
	public static int ipStr2int(String ipStr) {
		try {
			byte[] bytes = InetAddress.getByName(ipStr).getAddress();
			int addr = bytes[3] & 0xFF;
		    addr |= ((bytes[2] << 8) & 0xFF00);
		    addr |= ((bytes[1] << 16) & 0xFF0000);
		    addr |= ((bytes[0] << 24) & 0xFF000000);
		    return addr;
		} catch (UnknownHostException e) {
			log.error("{} ipStr2int error. {}", ipStr, e.getMessage());
			throw new WrapException(e);
		}
	}

	/**
	 * 检查IP地址合法性
	 * @param text ip地址
	 * @return 是否合法IP地址
	 */
	public static boolean ipCheck(String text) {
		if (text != null && !text.isEmpty()) {
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			if (text.matches(regex)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 解析IP地址
	 * @param ipStr ip地址
	 * @return 解析不出来返回空
	 */
	public static ProvinceCity ipAddress(String ipStr) {
		String url = "http://ip.ws.126.net/ipquery?ip="+ipStr;
		try {
			Map<String, String> headerMap = new HashMap<>(4);
			headerMap.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			HttpClient.HttpResult<String> result = HttpClient.get(url, headerMap, Charset.defaultCharset());
			if(result == null) {
				log.warn("ipAddress {} {} no response", ipStr, url);
				return null;
			}
			if(result.getStatusCode() != 200) {
				log.warn("ipAddress {} {} not 200", ipStr, url);
				return null;
			}
			//var lo="浙江省", lc="杭州市"; var localAddress={city:"杭州市", province:"浙江省"}
			String content = result.getContent();
			int start = content.indexOf("{");
			int end = content.indexOf("}");
			content = content.substring(start, end+1);
			ProvinceCity pc = JsonUtil.toObject(content, ProvinceCity.class);
			return pc;
		} catch (Exception e) {
			log.error("ipAddress {} {} error", ipStr, url, e);
			return null;
		}
	}

	@Data
	public static class ProvinceCity {
		/**
		 * 省份
		 */
		private String province;
		/**
		 * 城市
		 */
		private String city;
	}
	
	private IpUtil() {}
}
