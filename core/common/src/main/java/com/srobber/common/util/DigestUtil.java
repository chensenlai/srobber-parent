package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 签名算法工具类
 * MD5/SHA
 * 
 * @author chensenlai
 */
public class DigestUtil {

    /**
     * MD5单向散列签名
     * @param input
     * @return
     */
    public static byte[] md5(final byte[] input) {
        try {
            final MessageDigest md5Algorithm = MessageDigest.getInstance("MD5");
            return md5Algorithm.digest(input);
        } catch (final NoSuchAlgorithmException e) {
            throw new WrapException(e);
        }
    }

    public static byte[] md5(final String input) {
        return md5(input.getBytes(Charset.forName("UTF-8")));
    }

    public static String md5Hex(final byte[] input) {
        return StringUtil.byte2hex(md5(input));
    }

    public static String md5Hex(final String input) {
        return StringUtil.byte2hex(md5(input));
    }

    /**
     * SHA256单向散列签名
     * @param input
     * @return
     */
    public static byte[] sha256(final byte[] input) {
        try {
            final MessageDigest sha256Algorithm = MessageDigest.getInstance("SHA-256");
            return sha256Algorithm.digest(input);
        } catch (final NoSuchAlgorithmException e) {
            throw new WrapException(e);
        }
    }

    public static byte[] sha256(final String input) {
        return sha256(input.getBytes(Charset.forName("UTF-8")));
    }

    public static String sha256Hex(final byte[] input) {
        return StringUtil.byte2hex(sha256(input));
    }

    public static String sha256Hex(final String input) {
        return StringUtil.byte2hex(sha256(input));
    }

    /**
     * SHA1单向散列签名
     * @param input
     * @return
     */
    public static byte[] sha1(final byte[] input) {
        try {
            final MessageDigest sha1Algorithm = MessageDigest.getInstance("SHA-1");
            return sha1Algorithm.digest(input);
        } catch (final NoSuchAlgorithmException e) {
            throw new WrapException(e);
        }
    }

    public static byte[] sha1(final String input) {
        return sha1(input.getBytes(Charset.forName("UTF-8")));
    }

    public static String sha1Hex(final byte[] input) {
        return StringUtil.byte2hex(sha1(input));
    }

    public static String sha1Hex(final String input) {
        return StringUtil.byte2hex(sha1(input));
    }
}
