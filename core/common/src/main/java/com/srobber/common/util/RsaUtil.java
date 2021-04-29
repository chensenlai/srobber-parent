package com.srobber.common.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * RSA非对称密钥工具类
 * 公钥加密,私钥解密
 * 私钥签名,公钥验证签名
 *
 * @author chensenlai
 */
@Slf4j
public class RsaUtil {
	
	private static final String KEY_ALGORITHM_RSA = "RSA";
	/**
	 * RSA密钥长度，默认1024位，密钥长度必须是64的倍数，范围在512至65536位之间。
	 */
	private static final int KEY_SIZE = 1024;
	
	public static final String RSA_PUBLIC_KEY = "RSAPublicKey";
	public static final String RSA_PRIVATE_KEY = "RSAPrivateKey";
	
	static{
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
	}
	
	/**
	 * 私钥解密
	 * @param data 待解密数据
	 * @param key 私钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key)  {
		try {
			// 生成私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	 
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int blockSize = cipher.getBlockSize();
			if(blockSize>0){
				ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
				int j = 0;
				while (data.length - j * blockSize > 0) {
					bout.write(cipher.doFinal(data, j * blockSize, blockSize));
					j++;
				}
				return bout.toByteArray();
			}
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("RSA decryptByPrivateKey error", e);
			throw new WrapException(e);
		}
	}
 
	/**
	 * 公钥解密
	 * @param data 待解密数据
	 * @param key 公钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPublicKey(byte[] data, byte[] key) {
		try {
			// 生成公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	 
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("RSA decryptByPublicKey error", e);
			throw new WrapException(e);
		}
	}
 
	/**
	 * 公钥加密
	 * @param data 待加密数据
	 * @param key 公钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key) {
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	 
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int blockSize = cipher.getBlockSize();
			if(blockSize>0){
				int outputSize = cipher.getOutputSize(data.length);
				int leavedSize = data.length % blockSize;
				int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
						: data.length / blockSize;
				byte[] raw = new byte[outputSize * blocksSize];
				int i = 0,remainSize=0;
				while ((remainSize = data.length - i * blockSize) > 0) {
					int inputLen = remainSize > blockSize?blockSize:remainSize;
					cipher.doFinal(data, i * blockSize, inputLen, raw, i * outputSize);
					i++;
				}
				return raw;
			}
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("RSA encryptByPublicKey error", e);
			throw new WrapException(e);
		}
	}
 
	/**
	 * 私钥加密
	 * @param data 待加密数据
	 * @param key 私钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key) {
		try {
			// 生成私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			int blockSize = cipher.getBlockSize();
			if(blockSize>0){
				int outputSize = cipher.getOutputSize(data.length);
				int leavedSize = data.length % blockSize;
				int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
						: data.length / blockSize;
				byte[] raw = new byte[outputSize * blocksSize];
				int i = 0,remainSize=0;
				while ((remainSize = data.length - i * blockSize) > 0) {
					int inputLen = remainSize > blockSize?blockSize:remainSize;
					cipher.doFinal(data, i * blockSize, inputLen, raw, i * outputSize);
					i++;
				}
				return raw;
			}
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error("RSA encryptByPrivateKey error", e);
			throw new WrapException(e);
		}
	}

	/**
	 * 初始化密钥
	 * @param seed 种子
	 * @return 密钥Map
	 * @throws Exception
	 */
	public static Map<String,Key> initKey(byte[] seed) {
		Map<String, Key> keyMap = new HashMap<String, Key>(4);
		try {
			// 实例化密钥对生成器
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
			// 初始化密钥对生成器
			keyPairGen.initialize(KEY_SIZE,	new SecureRandom(seed) );
	 
			// 生成密钥对
			KeyPair keyPair = keyPairGen.generateKeyPair();
			// 公钥
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			// 私钥
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	 
			// 封装密钥
			keyMap.put(RSA_PUBLIC_KEY, publicKey);
			keyMap.put(RSA_PRIVATE_KEY, privateKey);
			return keyMap;
		} catch (Exception e) {
			log.error("RSA initKey error", e);
			throw new WrapException(e);
		}
	}
}
