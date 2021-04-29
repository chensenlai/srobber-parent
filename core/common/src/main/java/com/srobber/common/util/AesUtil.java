package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

/**
 * AES对称加密工具类
 *
 * @author chensenlai
 */
@Slf4j
public class AesUtil {
	
	private static final String KEY_AES = "AES";
	
    static {
    	Security.addProvider(new BouncyCastleProvider());
    }
    
    public static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) {  
        try {  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");  
            Key sKeySpec = new SecretKeySpec(keyByte, KEY_AES);  
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");  
            params.init(new IvParameterSpec(ivByte));  
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);
            return cipher.doFinal(content);  
        } catch (Exception e) {
        	log.error("aes decrypt error. {}",  e.getMessage());
        	throw new WrapException(e);
        }
    }  
    
    public static String encrypt(String data, String key)  {
    	 try {  
			byte[] raw = key.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
			Cipher cipher = Cipher.getInstance(KEY_AES);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(data.getBytes());
			return StringUtil.byte2hex(encrypted);
    	 } catch (Exception e) {
			 log.error("aes encrypt error. {}",  e.getMessage());
    		 throw new WrapException(e);
         }
	}
 
	public static String decrypt(String data, String key)  {
		try {  
			byte[] raw = key.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
			Cipher cipher = Cipher.getInstance(KEY_AES);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted = StringUtil.hex2byte(data);
			byte[] original = cipher.doFinal(encrypted);
			return new String(original);
		} catch (Exception e) {
			log.error("aes decrypt error. {}",  e.getMessage());
			throw new WrapException(e);
         }
	}
}
