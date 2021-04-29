package com.srobber.common.util;

import org.springframework.util.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 字符串相关工具类
 *
 * @author chensenlai
 */
public class StringUtil {

    /**
     * 过滤字符
     * 保留中文,小写字符,大写字符, 过滤其他未知字符
     * @param str 原字符串
     * @return 过滤后字符串
     */
    public static String filterChar(String str) {
        StringBuilder buffer = new StringBuilder(32);
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (isChineseChar(chars[i])
                    || isLowerChar(chars[i])
                    || isUpperChar(chars[i])) {
                buffer.append(chars[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 判断字符是否是中文字符
     * @param ch 字符
     * @return 是否中文字符
     */
    public static boolean isChineseChar(char ch) {
        return ch >= 19968 && ch <= 40869;
    }

    /**
     * 判断字符是否是小写英文字符
     * @param ch 字符
     * @return 是否英文小写字符
     */
    public static boolean isLowerChar(char ch) {
        return ch >= 97 && ch <= 122;
    }

    /**
     * 判断字符是否是大写英文字符
     * @param ch 字符
     * @return 判断是否英文大写字符
     */
    public static boolean isUpperChar(char ch) {
        return ch >= 65 && ch <= 90;
    }

    /**
     * 长度为偶数的字符串转十六进制
     * 0a   => [10]
     * 0a0b => [10, 11]
     * a    => null
     * 0k   => NumberFormatException
     * @param hex 十六进制字符串(长度为偶数)
     * @return 十六进制对应字节数组
     */
    public static byte[] hex2byte(String hex) {
        if (hex == null) {
            return null;
        }
        int l = hex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            String subHex = hex.substring(i * 2, i * 2 + 2);
            b[i] = (byte) Integer.parseInt(subHex, 16);
        }
        return b;
    }

    /**
     * 字节数组转十六进制字符串
     * [10] => 0A
     * [10, 11] => 0A0B
     * @param b 十六进制对应数组
     * @return 十六进制字符串
     */
    public static String byte2hex(byte[] b) {
        if(b == null) {
            return null;
        }
        StringBuilder hexBuf = new StringBuilder(2*b.length);
        for (int n = 0; n < b.length; n++) {
            String subHex = (Integer.toHexString(b[n] & 0XFF));
            if (subHex.length() == 1) {
                hexBuf.append("0");
            }
            hexBuf.append(subHex);
        }
        return hexBuf.toString().toUpperCase();
    }


    /**
     * 获取32位UUID
     * @return 32位字符串
     */
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    /**
     * 返回非null字符串
     * 如果为null，则返回""（空白字符串）
     * @param str 原字符串
     * @return 非null字符串
     */
    public static String getNotNullStr(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 返回非null字符串
     * 如果为null，则返回${defaultVal}字符串
     * @param str 原字符串
     * @return 非null字符串
     */
    public static String getNotNullStr(String str, String defaultVal) {
        if (str == null) {
            return defaultVal;
        }
        return str;
    }

    /**
     * 判断是否是空白字符串
     * \n\t     都算空白字符
     * @param str 原来字符串
     * @return 是否空白字符串
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否不是空白字符串
     * @param str 原字符串
     * @return 是否不是空白字符串
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断两个字符串是否相等（支持null）
     * null,null => true
     * null,hello => false
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否等于
     */
    public static boolean isEqual(String str1, String str2) {
        if(str1 == str2) {
            return true;
        }
        return isNotNullAndEqual(str1, str2);
    }

    /**
     * 判断两个字符串是否相等（有一个字符串为null就返回false）
     * null,null => false
     * null,hello => false
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否非空且等于
     */
    public static boolean isNotNullAndEqual(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 左补齐字符${ch}到原来字符串${str},总长度为${totalLength}
     * 如果原字符串${str}长度超过${totalLength}，则不会进行补齐，直接返回原来字符串${str}
     * aa, b, 5     => bbbaa
     * aaa, b, 2    => aaa
     * @param str 原字符串，可为null
     * @param ch 补齐字符
     * @param totalLength 补期后字符串总长度
     * @return 补齐后字符串
     */
    public static String leftAppend(String str, char ch, int totalLength) {
        if (str == null) {
            str = "";
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(Math.max(length, totalLength));
        while (length < totalLength) {
            sb.append(ch);
            length++;
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 右齐字符${ch}到原来字符串${str},总长度为${totalLength}
     * 如果原字符串${str}长度超过${totalLength}，则不会进行补齐，直接返回原来字符串${str}
     * aa, b, 5     => aabbb
     * aaa, b, 2    => aaa
     * @param str 原字符串，可为null
     * @param ch 补齐字符
     * @param totalLength 补期后字符串总长度
     * @return 补齐后字符串
     */
    public static String rightAppend(String str, char ch, int totalLength) {
        if (str == null) {
            str = "";
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(Math.max(length, totalLength));
        sb.append(str);
        while (length < totalLength) {
            sb.append(ch);
            length++;
        }
        return sb.toString();
    }

    /**
     * 字符串左边去空格, 包括英文空格/中文空格/tab
     * @param str 字符串
     * @return 左边去掉空格字符串
     */
    public static String leftTrim(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("^[　 	]+", "");
        }
    }

    /**
     * 字符串右边去空格, 包括英文空格/中文空格/tab
     * @param str 字符串
     * @return 右边去掉空格字符串
     */
    public static String rightTrim(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("[　 	]+$", "");
        }
    }

    /**
     * 字符串分隔成对应数值列表
     * 1,2,3 => [1, 2, 3]
     * @param text 字符串
     * @param spilt 分割符
     * @param targetClass 转化目标类型
     * @param <T> 泛型
     * @return 目标类型列表
     */
    public static <T extends Number> List<T> spilt(String text, String spilt, Class<T> targetClass) {
        if(StringUtil.isBlank(text)) {
            return Collections.emptyList();
        }
        String[] strArr = text.split(spilt);
        List<T> targetList = new ArrayList<>(strArr.length);
        for(String str : strArr) {
            if(StringUtil.isBlank(str)) {
                continue;
            }
            T target = NumberUtils.parseNumber(str, targetClass);
            targetList.add(target);
        }
        return targetList;
    }

    /**
     * 将${array}数组里面每个字符串，
     * 左右补上${wrapper}字符串，
     * 然后再用${separator}连接起来
     * eg: ["aa", "bb"], "'", "," => 'aa','bb'
     * @param array 原字符数组，为null时返回null
     * @param wrapper 左右包裹的字符串, 为null时默认""
     * @param separator 连接符, 为null时默认""
     * @return 数组拼接后字符串
     */
    public static String join(final String[] array, String wrapper, String separator) {
        return join(array, wrapper, wrapper, separator);
    }

    /**
     * 将${array}数组里面每个字符串，
     * 左补上${leftWrapper}字符串，
     * 右补上${rightWrapper}字符串
     * 然后再用${separator}连接起来
     * eg: ["aa", "bb"], "'", "'", "," => 'aa','bb'
     */
    public static String join(final String[] array, String leftWrapper, String rightWrapper, String separator) {
        if (array == null) {
            return null;
        }
        if(leftWrapper == null) {
            leftWrapper = "";
        }
        if(rightWrapper == null) {
            rightWrapper = "";
        }
        if (separator == null) {
            separator = "";
        }
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (leftWrapper != null) {
                buf.append(leftWrapper);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
            if (rightWrapper != null) {
                buf.append(rightWrapper);
            }
        }
        return buf.toString();
    }

    /**
     * 字符串${str}重复${count}返回
     * @param str 原始字符串
     * @param count 重复次数
     * @return 重复的字符串
     */
    public static String repeat(String str, int count) {
        if(str == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(str.length() * count);
        while (count > 0) {
            buf.append(str);
            count--;
        }
        return buf.toString();
    }

    /**
     * 字符串转int, 把检查异常包装成运行时异常
     * @param str 整型字符串
     * @return 整型
     */
    public static Integer str2Integer(String str) {
        Integer i;
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    /**
     * 字符串转int, 转化异常则返回默认值
     * @param str 整型字符串
     * @param defaultVal 默认整型
     * @return 整型
     */
    public static Integer str2Integer(String str, Integer defaultVal) {
        Integer i;
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
            i = defaultVal;
        }
        return i;
    }

    /**
     * 字符串转long, 把检查异常包装成运行时异常
     * @param str 长整型字符串
     * @return 长整型
     */
    public static Long str2Long(String str) {
        Long l;
        try {
            l = Long.parseLong(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return l;
    }

    /**
     * 字符串转long, 转化异常则返回默认值
     * @param str 长整型字符串
     * @param defaultVal 默认值
     * @return 长整型
     */
    public static Long str2Long(String str, Long defaultVal) {
        Long l;
        try {
            l = Long.parseLong(str);
        } catch (Exception e) {
            l = defaultVal;
        }
        return l;
    }

    /**
     * 截取子串, 限制可截区子串最大长度
     * @param str 原字符串
     * @param beginIndex 开始下标(0开始)
     * @param endIndex 截止下标(不包含)
     * @param maxLen 最大子串长度
     * @return 子字符串
     */
    public static String substring(String str, int beginIndex, int endIndex, int maxLen) {
        int subLen = endIndex - beginIndex;
        if(subLen > maxLen) {
            endIndex = beginIndex + maxLen;
        }
        return str.substring(beginIndex, endIndex);
    }

    private StringUtil() {
    }
}
