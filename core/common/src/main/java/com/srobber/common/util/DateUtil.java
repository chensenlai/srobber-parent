package com.srobber.common.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作工具类
 *
 * @author chensenlaii
 */
public class DateUtil {

	/**
	 * 获取当前日期
	 * @return 日期
	 */
	public static Date now() {
		return new Date(DateTime.now().getMillis());
	}

	/**
	 * 获取当前日期
	 * @return 日期
	 */
	public static Timestamp nowTimestamp(){
		return new Timestamp(DateTime.now().getMillis());
	}

	/**
	 * 获取当前毫秒数
	 * @return 毫秒数
	 */
	public static long nowMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取加上plusMillis毫秒后的日期
	 * @param date 日期
	 * @param plusMillis 加上毫秒数
	 * @return 加上plusMillis毫秒后的日期
	 */
	public static Date plusMillis(Date date, int plusMillis) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusMillis(plusMillis).toDate();
	}
		
	/**
	 * 获取加上plusSeconds秒后的日期
	 * @param date 日期
	 * @param plusSeconds 加上秒数
	 * @return 加上plusSeconds秒后的日期
	 */
	public static Date plusSeconds(Date date, int plusSeconds) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusSeconds(plusSeconds).toDate();
	}

	/**
	 * 获取加上plusMinutes分钟后的日期
	 * @param date 日期
	 * @param plusMinutes 加上分钟
	 * @return 加上plusMinutes分钟的日期
	 */
	public static Date plusMinutes(Date date, int plusMinutes) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusMinutes(plusMinutes).toDate();
	}

	/**
	 * 获取加上plusHours小时后的日期
	 * @param date 日期
	 * @param plusHours 加上小时数
	 * @return 加上plusHours小时后的日期
	 */
	public static Date plusHours(Date date, int plusHours) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusHours(plusHours).toDate();
	}

	/**
	 * 获取加上plusDays日后的日期
	 * @param date 日期
	 * @param plusDays 加上日数
	 * @return 加上plusDays日后的日期
	 */
	public static Date plusDays(Date date, int plusDays) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusDays(plusDays).toDate();
	}
	
	/**
	 * 获取加上plusMonths月后的日期
	 * @param date 日期
	 * @param plusMonths 加上月数
	 * @return 加上plusMonths月后的日期
	 */
	public static Date plusMonths(Date date, int plusMonths) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusMonths(plusMonths).toDate();
	}

	/**
	 * 获取加上plusYears年后的日期
	 * @param date 日期
	 * @param plusYears 加上年数
	 * @return 加上plusYears年后的日期
	 */
	public static Date plusYears(Date date, int plusYears) {
		DateTime dateTime = new DateTime(date);
		return dateTime.plusYears(plusYears).toDate();
	}

	/**
	 * 两个日期年份数差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 年份差值
	 */
	public static int yearsCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.years());
		return p.getYears();
	}

	/**
	 * 两个日期月份数差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 月份数差值
	 */
	public static int monthsCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.months());
		return p.getMonths();
	}

	/**
	 * 两个日期天数差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 天数差值
	 */
	public static int daysCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.days());
		return p.getDays();
	}

	/**
	 * 两个日期小时差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 小时差值
	 */
	public static int hoursCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.hours());
		return p.getHours();
	}

	/**
	 * 两个日期分差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 分差值
	 */
	public static int minutesCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.minutes());
		return p.getMinutes();
	}

	/**
	 * 两个日期秒差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 秒差值
	 */
	public static int secondsCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.seconds());
		return p.getSeconds();
	}

	/**
	 * 两个日期毫秒差值 (endDate-beginDate)
	 * @param beginDate 开始日期
	 * @param endDate 截止日期
	 * @return 毫秒差值
	 */
	public static int millisCompare(Date beginDate,Date endDate){
		DateTime bDateTime = new DateTime(beginDate);
		DateTime eDateTime = new DateTime(endDate);
		Period p = new Period(bDateTime,eDateTime,PeriodType.millis());
		return p.getMillis();
	}

	/**
	 * 获取给定日期当天开始时间
	 * @param date 日期
	 * @return 当天开始时间
	 */
	public static Date getStartOfDate(Date date){
		DateTime dateTime = new DateTime(date);
		return dateTime.withTimeAtStartOfDay().toDate();
	}

	/**
	 * 获取给定日期当天最后时间
	 * @param date 日期
	 * @return 当天最后时间
	 */
	public static Date getEndOfDate(Date date){
		DateTime dateTime = new DateTime(date);
		return dateTime.millisOfDay().withMaximumValue().toDate();
	}

	/**
	 * 获取一个月的第一天(时分秒跟传入得日期一样)
	 * @param date 日期
	 * @return 月份第一天
	 */
	public static Date getFirstDateOfMonth(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.dayOfMonth().withMinimumValue().toDate();
	}

	/**
	 * 获取一个月的最后一天(时分秒跟传入得日期一样)
	 * @param date 日期
	 * @return 月份最后一天
	 */
	public static Date getLastDateOfMonth(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.dayOfMonth().withMaximumValue().toDate();
	}

	/**
	 * 获取日期在周的第几天
	 * 星期一:1
	 * 星期天:7
	 * @param date  日期
	 * @return 周的第几天
	 */
	public static int getDayOfWeek(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.dayOfWeek().get();
	}

	/**
	 * 获取日期当天已过的分钟数
	 * @param date  日期
	 * @return 分钟数
	 */
	public static int getHourOfDay(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.getHourOfDay();
	}

	/**
	 * 获取日期当天已过的分钟数
	 * @param date  日期
	 * @return 分钟数
	 */
	public static int getMinuteOfDay(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.getMinuteOfDay();
	}

	/**
	 * 获取日期当天已过的秒数
	 * @param date  日期
	 * @return 秒数
	 */
	public static int getSecondOfDay(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.getSecondOfDay();
	}

	/**
	 * 获取日期当天已过的毫秒数
	 * @param date  日期
	 * @return 毫秒数
	 */
	public static int getMillisOfDay(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.getMillisOfDay();
	}

	/**
	 * 判断日期是否当天
	 * @param date 日期
	 * @return true/false
	 */
	public static boolean isToday(Date date) {
		DateTime dt = new DateTime(date);
		if (DateTime.now().toString("yyyyMMdd").equals(dt.toString("yyyyMMdd"))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断日期是否plusDays天后的日期
	 * @param date 日期
	 * @param plusDays 加天数(可为负数)
	 * @return true/false
	 */
	public static boolean isTodayPlusDays(Date date, int plusDays) {
		DateTime dt = new DateTime(date);
		if (DateTime.now().plusDays(plusDays).toString("yyyyMMdd").equals(dt.toString("yyyyMMdd"))) {
			return true;
		}
		return false;
	}

	/**
	 * 计算年龄
	 * 周岁: 按出生日期, 实际日期-出生日期满一年算1岁
	 * @param birthDate 生日
	 * @return 年龄
	 */
	public static int calcAge(Date birthDate) {
		Calendar cur = Calendar.getInstance();
		int cYear = cur.get(Calendar.YEAR);
		int cMonth = cur.get(Calendar.MONTH);
		int cDay = cur.get(Calendar.DAY_OF_MONTH);

		Calendar birth = Calendar.getInstance();
		birth.setTime(birthDate);
		int bYear = birth.get(Calendar.YEAR);
		int bMonth = birth.get(Calendar.MONTH);
		int bDay = birth.get(Calendar.DAY_OF_MONTH);

		int age = cYear-bYear;
		if((cMonth<bMonth) || (cMonth==bMonth && cDay<bDay)) {
			age--;
		}
		return age>=0 ? age : 0;
	}
	
	private DateUtil(){}
}
