package com.srobber.common.util;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * DES对称加密工具类
 *
 * @author chensenlai
 */
@Slf4j
public class DesUtil {

	static{
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
	}
	/**
	 * 密钥算法 <br>
	 * Java 6 只支持56bit密钥 <br>
	 * Bouncy Castle 支持64bit密钥
	 */
	public static final String KEY_ALGORITHM = "DES";
 
	/**
	 * 加密/解密算法 / 工作模式 / 填充方式
	 */
	public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5PADDING";
 
	/**
	 * 转换密钥
	 * 
	 * @param key 二进制密钥
	 * @return Key 密钥
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		// 实例化DES密钥材料
		DESKeySpec dks = new DESKeySpec(key);
		// 实例化秘密密钥工厂
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(KEY_ALGORITHM);
		// 生成秘密密钥
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}
 
	/**
	 * DES解密
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decrypt(byte[] data, byte[] key) {
		try {
			Key k = toKey(key);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("DES decrypt error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
 
	/**
	 * DES加密
	 * @param data 待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encrypt(byte[] data, byte[] key) {
		try {
			Key k = toKey(key);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("DES encrypt error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
 
	/**
	 * 生成密钥 <br>
	 * Java 6 只支持56bit密钥 <br>
	 * Bouncy Castle 支持64bit密钥 <br>
	 * @return byte[] 二进制密钥
	 */
	public static byte[] initKey() throws Exception {
		//实例化密钥生成器
		//若要使用64bit密钥注意替换 将下述代码中的KeyGenerator.getInstance(CIPHER_ALGORITHM);
		//替换为KeyGenerator.getInstance(CIPHER_ALGORITHM, "BC");
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		// 初始化密钥生成器 若要使用64bit密钥注意替换 将下述代码kg.init(56); 替换为kg.init(64);
		kg.init(56, new SecureRandom());
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}
	
	public static byte[] initKey(String seed) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		SecureRandom secureRandom = new SecureRandom(Base64Util.decode(seed));  
		kg.init(secureRandom);
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

}
