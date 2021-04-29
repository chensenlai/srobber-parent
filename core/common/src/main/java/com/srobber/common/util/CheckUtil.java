package com.srobber.common.util;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author chensenlai
 */
public class CheckUtil {

	/**
	 * 身份证合法性校验
	 * @param idCardStr 身份证号码
	 * @return 身份证号码是否校验通过
	 */
	public static boolean checkIdCard(String idCardStr) {
		if(idCardStr == null) {
			return false;
		}
		boolean result = false;
		String reg = "^[1-9]\\d{5}[1-9]\\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$";
		try {
			String idCard = idCardStr.trim();
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(idCard);
			boolean flag = matcher.matches();
			if (flag) {
				int[] factors = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
				int sum = 0;
				for (int i = 0; i < factors.length; i++) {
					int c = Integer.parseInt(String.valueOf(idCard.charAt(i)));
					sum += c * factors[i];
				}
				int remainder = sum % 11;
				/*获取身份证最后一位*/
				Character last = idCard.charAt(idCard.length() - 1);
				char[] lasts = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
				if (remainder == 2 && Character.toUpperCase(last) == 'X') {
					return true;
				}
				if (last == lasts[remainder]) {
					result = true;
				}
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 检查生日格式(yyyy-MM-dd)
	 * xxxx-xx-xx => false
	 * 2020-13-01 => false
	 * 2020-12-01 = > true
	 *
	 * @param birthDateStr 生日字符串
	 * @return 是否格式正确
	 */
	public static boolean checkBirthDate(String birthDateStr) {
		if(birthDateStr == null) {
			return false;
		}
		try {
			Date birthDate = DateFormatUtil.parse(birthDateStr, DateFormatUtil.Pattern.YYYY_MM_DD);
			if(Objects.equals(DateFormatUtil.format(birthDate, DateFormatUtil.Pattern.YYYY_MM_DD), birthDateStr)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 邮箱合法性校验
	 * @param emailStr 邮箱地址
	 * @return 邮箱地址是否合法
	 */
	public static boolean checkEmail(String emailStr) {
		boolean result = false;
		String reg = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
		try {
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(emailStr.trim());
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 手机号码合法性校验
	 * @param phoneStr 手机号码
	 * @return 手机号码是否合法
	 */
	public static boolean checkPhone(String phoneStr) {
		boolean result = false;
		String reg = "^0?(1)[1-9]{1}[0-9]{9}$";
		try {
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(phoneStr.trim());
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 固话合法性校验
	 * @param telStr 固话号码
	 * @return 固话号码是否合法
	 */
	public static boolean checkTel(String telStr) {
		boolean result = false;
		String reg = "^[0-9\\-()（）]{7,18}$";
		try {
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(telStr.trim());
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * QQ号码合法性校验
	 * @param qqStr QQ号码
	 * @return QQ号码是否合法
	 */
	public static boolean checkQQ(String qqStr) {
		boolean result = false;
		String reg = "^[1-9]*[1-9][0-9]*$";
		try {
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(qqStr.trim());
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 名称合法性校验
	 * @param nameStr 名称
	 * @return 名称是否合法
	 */
	public static boolean checkName(String nameStr) {
		boolean result = false;
		String reg = "^[A-Za-z\\u4e00-\\u9fa5]+$";
		try {
			Pattern regex = Pattern.compile(reg);
			Matcher matcher = regex.matcher(nameStr.trim());
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	private CheckUtil(){}
}
