package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * Base64编码工具类
 *
 * @author chensenlai
 */
@Slf4j
public class Base64Util {
	
	private static final String CHARSET = "UTF-8";
	
	public static byte[] encode(byte[] bs) {
		try {
			return Base64.getEncoder().encode(bs);
		} catch (Exception e) {
			log.error("Base64 encode error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
	
	public static String encodeAsString(byte[] bs) {
		try {
			byte[] b = encode(bs);
			return new String(b, CHARSET);
		} catch (Exception e) {
			log.error("Base64 encode error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
 
	public static byte[] decode(String text)  {
		try {
			return Base64.getDecoder().decode(text.getBytes(CHARSET));
		} catch (Exception e) {
			log.error("Base64 decode error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	public static String decodeAsString(String text)  {
		try {
			byte[] b = Base64.getDecoder().decode(text.getBytes(CHARSET));
			return new String(b, CHARSET);
		} catch (Exception e) {
			log.error("Base64 decode error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	private Base64Util() {}
}
