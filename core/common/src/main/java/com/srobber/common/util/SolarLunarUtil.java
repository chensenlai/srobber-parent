package com.srobber.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * FIXME 待确认代码的正确性
 * 国历农历佛历工具类
 */
public class SolarLunarUtil {
	
	//------------------------------------- 中文 --------------------------------------------
	private static String[] CN_YEAR = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	private static String[] CN_MONTH = new String[] { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊" };
	private static String[] CN_DAY = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

	
	public static String getCnyear(int year) {
		StringBuilder buf = new StringBuilder(4);
		while(year != 0) {
			buf.append(CN_YEAR[year%10]);
			year/=10;
		}
		return buf.reverse().toString();
	}
	
	public static String getCnmonth(int month) {
		return CN_MONTH[(month-1)%12];
	}

	public static String getCnday(int day) {
		if(day < 10) {
			return "初"+CN_DAY[day-1];
		} else if(day == 10) {
			return "初十";
		} else if(day < 20) {
			day -= 10;
			return "十"+CN_DAY[day-1];
		} else if(day == 20) {
			return "廿十";
		} else if(day < 30) {
			day -= 20;
			return "廿"+CN_DAY[day-1];
		} else if(day == 30) {
			return "三十";
		} else {
			day -= 30;
			return "三"+CN_DAY[day-1];
		}
	}
	
	//-------------------------------------干支-------------------------------------------
	static class LunarInfo {
		
		private Calendar solar;
		private int lunarYear;
		private int lunarMonth;
		private int lunarDay;
		private boolean isLeapYear;
		private boolean isLeap;
		private int maxDayInMonth = 29;
		private final static int[] lunarInfo = { 0x4bd8, 0x4ae0, 0xa570, 0x54d5,
				0xd260, 0xd950, 0x5554, 0x56af, 0x9ad0, 0x55d2, 0x4ae0, 0xa5b6,
				0xa4d0, 0xd250, 0xd295, 0xb54f, 0xd6a0, 0xada2, 0x95b0, 0x4977,
				0x497f, 0xa4b0, 0xb4b5, 0x6a50, 0x6d40, 0xab54, 0x2b6f, 0x9570,
				0x52f2, 0x4970, 0x6566, 0xd4a0, 0xea50, 0x6a95, 0x5adf, 0x2b60,
				0x86e3, 0x92ef, 0xc8d7, 0xc95f, 0xd4a0, 0xd8a6, 0xb55f, 0x56a0,
				0xa5b4, 0x25df, 0x92d0, 0xd2b2, 0xa950, 0xb557, 0x6ca0, 0xb550,
				0x5355, 0x4daf, 0xa5b0, 0x4573, 0x52bf, 0xa9a8, 0xe950, 0x6aa0,
				0xaea6, 0xab50, 0x4b60, 0xaae4, 0xa570, 0x5260, 0xf263, 0xd950,
				0x5b57, 0x56a0, 0x96d0, 0x4dd5, 0x4ad0, 0xa4d0, 0xd4d4, 0xd250,
				0xd558, 0xb540, 0xb6a0, 0x95a6, 0x95bf, 0x49b0, 0xa974, 0xa4b0,
				0xb27a, 0x6a50, 0x6d40, 0xaf46, 0xab60, 0x9570, 0x4af5, 0x4970,
				0x64b0, 0x74a3, 0xea50, 0x6b58, 0x5ac0, 0xab60, 0x96d5, 0x92e0,
				0xc960, 0xd954, 0xd4a0, 0xda50, 0x7552, 0x56a0, 0xabb7, 0x25d0,
				0x92d0, 0xcab5, 0xa950, 0xb4a0, 0xbaa4, 0xad50, 0x55d9, 0x4ba0,
				0xa5b0, 0x5176, 0x52bf, 0xa930, 0x7954, 0x6aa0, 0xad50, 0x5b52,
				0x4b60, 0xa6e6, 0xa4e0, 0xd260, 0xea65, 0xd530, 0x5aa0, 0x76a3,
				0x96d0, 0x4afb, 0x4ad0, 0xa4d0, 0xd0b6, 0xd25f, 0xd520, 0xdd45,
				0xb5a0, 0x56d0, 0x55b2, 0x49b0, 0xa577, 0xa4b0, 0xaa50, 0xb255,
				0x6d2f, 0xada0, 0x4b63, 0x937f, 0x49f8, 0x4970, 0x64b0, 0x68a6,
				0xea5f, 0x6b20, 0xa6c4, 0xaaef, 0x92e0, 0xd2e3, 0xc960, 0xd557,
				0xd4a0, 0xda50, 0x5d55, 0x56a0, 0xa6d0, 0x55d4, 0x52d0, 0xa9b8,
				0xa950, 0xb4a0, 0xb6a6, 0xad50, 0x55a0, 0xaba4, 0xa5b0, 0x52b0,
				0xb273, 0x6930, 0x7337, 0x6aa0, 0xad50, 0x4b55, 0x4b6f, 0xa570,
				0x54e4, 0xd260, 0xe968, 0xd520, 0xdaa0, 0x6aa6, 0x56df, 0x4ae0,
				0xa9d4, 0xa4d0, 0xd150, 0xf252, 0xd520 };

		private int solarYear;
		private int solarMonth;
		private int solarDay;
		private int cyclicalYear = 0;
		private int cyclicalMonth = 0;
		private int cyclicalDay = 0;

		private final static int[] solarTermInfo = { 0, 21208, 42467, 63836, 85337,
				107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
				285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
				462224, 483532, 504758 };

		private static GregorianCalendar utcCal = null;

		public final static String[] Tianan = { "甲", "乙", "丙", "丁", "戊", "己", "庚",
				"辛", "壬", "癸" };
		public final static String[] Deqi = { "子", "丑", "寅", "卯", "辰", "巳", "午",
				"未", "申", "酉", "戌", "亥" };

		/**
		 * 通过 Date 对象构建农历信息
		 * 
		 * @param date
		 *            指定日期对象
		 */
		public LunarInfo(Date date) {
			if (date == null) {
				date = new Date();
			}
			this.init(date.getTime());
		}

		/**
		 * 通过 TimeInMillis 构建农历信息
		 * 
		 * @param TimeInMillis
		 */
		public LunarInfo(long TimeInMillis) {
			this.init(TimeInMillis);
		}

		private void init(long TimeInMillis) {
			this.solar = Calendar.getInstance();
			this.solar.setTimeInMillis(TimeInMillis);
			Calendar baseDate = new GregorianCalendar(1900, 0, 31);
			long offset = (TimeInMillis - baseDate.getTimeInMillis()) / 86400000;
			// 按农历年递减每年的农历天数，确定农历年份
			this.lunarYear = 1900;
			int daysInLunarYear = LunarInfo.getLunarYearDays(this.lunarYear);
			while (this.lunarYear < 2100 && offset >= daysInLunarYear) {
				offset -= daysInLunarYear;
				daysInLunarYear = LunarInfo.getLunarYearDays(++this.lunarYear);
			}
			// 农历年数字

			// 按农历月递减每月的农历天数，确定农历月份
			int lunarMonth = 1;
			// 所在农历年闰哪个月,若没有返回0
			int leapMonth = LunarInfo.getLunarLeapMonth(this.lunarYear);
			// 是否闰年
			this.isLeapYear = leapMonth > 0;
			// 闰月是否递减
			boolean leapDec = false;
			boolean isLeap = false;
			int daysInLunarMonth = 0;
			while (lunarMonth < 13 && offset > 0) {
				if (isLeap && leapDec) { // 如果是闰年,并且是闰月
					// 所在农历年闰月的天数
					daysInLunarMonth = LunarInfo.getLunarLeapDays(this.lunarYear);
					leapDec = false;
				} else {
					// 所在农历年指定月的天数
					daysInLunarMonth = LunarInfo.getLunarMonthDays(this.lunarYear,
							lunarMonth);
				}
				if (offset < daysInLunarMonth) {
					break;
				}
				offset -= daysInLunarMonth;

				if (leapMonth == lunarMonth && isLeap == false) {
					// 下个月是闰月
					leapDec = true;
					isLeap = true;
				} else {
					// 月份递增
					lunarMonth++;
				}
			}
			this.maxDayInMonth = daysInLunarMonth;
			// 农历月数字
			this.lunarMonth = lunarMonth;
			// 是否闰月
			this.isLeap = (lunarMonth == leapMonth && isLeap);
			// 农历日数字
			this.lunarDay = (int) offset + 1;
			// 取得干支历
			this.getCyclicalData();
		}

		/**
		 * 返回农历年的总天数 114-162 目的是求农历的总天数
		 * 
		 * @param lunarYear
		 *            指定农历年份(数字)
		 * @return 该农历年的总天数(数字)
		 */
		private static int getLunarYearDays(int lunarYear) {
			// 按小月计算,农历年最少有12 * 29 = 348天
			int daysInLunarYear = 348;
			// 数据表中,每个农历年用16bit来表示,
			// 前12bit分别表示12个月份的大小月,最后4bit表示闰月
			// 每个大月累加一天
			for (int i = 0x8000; i > 0x8; i >>= 1) {
				daysInLunarYear += ((LunarInfo.lunarInfo[lunarYear - 1900] & i) != 0) ? 1
						: 0;
			}
			// 加上闰月天数
			daysInLunarYear += LunarInfo.getLunarLeapDays(lunarYear);

			return daysInLunarYear;
		}

		/**
		 * 返回农历年闰月的天数
		 * 
		 * @param lunarYear
		 *            指定农历年份(数字)
		 * @return 该农历年闰月的天数(数字)
		 */
		private static int getLunarLeapDays(int lunarYear) {
			// 下一年最后4bit为1111,返回30(大月)
			// 下一年最后4bit不为1111,返回29(小月)
			// 若该年没有闰月,返回0
			return LunarInfo.getLunarLeapMonth(lunarYear) > 0 ? ((LunarInfo.lunarInfo[lunarYear - 1899] & 0xf) == 0xf ? 30
					: 29)
					: 0;
		}

		/**
		 * 返回农历年闰月月份
		 * 
		 * @param lunarYear
		 *            指定农历年份(数字)
		 * @return 该农历年闰月的月份(数字,没闰返回0)
		 */
		private static int getLunarLeapMonth(int lunarYear) {
			// 数据表中,每个农历年用16bit来表示,
			// 前12bit分别表示12个月份的大小月,最后4bit表示闰月
			// 若4bit全为1或全为0,表示没闰, 否则4bit的值为闰月月份
			int leapMonth = LunarInfo.lunarInfo[lunarYear - 1900] & 0xf;
			leapMonth = (leapMonth == 0xf ? 0 : leapMonth);
			return leapMonth;
		}

		/**
		 * 返回农历年正常月份的总天数
		 * 
		 * @param lunarYear
		 *            指定农历年份(数字)
		 * @param lunarMonth
		 *            指定农历月份(数字)
		 * @return 该农历年闰月的月份(数字,没闰返回0)
		 */
		private static int getLunarMonthDays(int lunarYear, int lunarMonth) {
			// 数据表中,每个农历年用16bit来表示,
			// 前12bit分别表示12个月份的大小月,最后4bit表示闰月
			int daysInLunarMonth = ((LunarInfo.lunarInfo[lunarYear - 1900] & (0x10000 >> lunarMonth)) != 0) ? 30
					: 29;
			return daysInLunarMonth;
		}

		/**
		 * 取干支历 不是历年，历月干支，而是中国的从立春节气开始的节月，是中国的太阳十二宫，阳历的。
		 * 
		 * @param cncaData
		 *            日历对象(Tcnca)
		 */
		private void getCyclicalData() {
			this.solarYear = this.solar.get(Calendar.YEAR);
			this.solarMonth = this.solar.get(Calendar.MONTH);
			this.solarDay = this.solar.get(Calendar.DAY_OF_MONTH);
			// 干支历
			int cyclicalYear = 0;
			int cyclicalMonth = 0;
			int cyclicalDay = 0;
			int cyclicalHour = 0;
			// 干支年 1900年立春後为庚子年(60进制36)
			int term2 = LunarInfo.getSolarTermDay(solarYear, 2); // 立春日期
			// 依节气调整二月分的年柱, 以立春为界
			if (solarMonth < 1 || (solarMonth == 1 && solarDay < term2)) {
				cyclicalYear = (solarYear - 1900 + 36 - 1) % 60;
			} else {
				cyclicalYear = (solarYear - 1900 + 36) % 60;
			}

			// 干支月 1900年1月小寒以前为 丙子月(60进制12)
			int firstNode = LunarInfo.getSolarTermDay(solarYear, solarMonth * 2); // 传回当月「节」为几日开始
			// 依节气月柱, 以「节」为界
			if (solarDay < firstNode) {
				cyclicalMonth = ((solarYear - 1900) * 12 + solarMonth + 12) % 60;
			} else {
				cyclicalMonth = ((solarYear - 1900) * 12 + solarMonth + 13) % 60;
			}

			// 当月一日与 1900/1/1 相差天数
			// 1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
			cyclicalDay = (int) (LunarInfo.UTC(solarYear, solarMonth, solarDay, 0, 0,
					0) / 86400000 + 25567 + 10) % 60;
			this.cyclicalYear = cyclicalYear;
			this.cyclicalMonth = cyclicalMonth;
			this.cyclicalDay = cyclicalDay;
		}

		/**
		 * 返回公历年节气的日期
		 * 
		 * @param solarYear
		 *            指定公历年份(数字)
		 * @param index
		 *            指定节气序号(数字,0从小寒算起)
		 * @return 日期(数字,所在月份的第几天)
		 */
		private static int getSolarTermDay(int solarYear, int index) {
			Date date = getSolarTermCalendar(solarYear, index);
			return LunarInfo.getUTCDay(date);
		}

		/**
		 * 返回公历年节气的日期
		 * 
		 * @param solarYear
		 *            指定公历年份(数字)
		 * @param index
		 *            指定节气序号(数字,0从小寒算起)
		 * @return 日期(数字,所在月份的第几天)
		 */
		public static Date getSolarTermCalendar(int solarYear, int index) {
			long l = (long) 31556925974.7 * (solarYear - 1900)
					+ solarTermInfo[index] * 60000L;
			l = l + LunarInfo.UTC(1900, 0, 6, 2, 5, 0);
			return new Date(l);
		}

		 public static synchronized int getUTCDay(Date date) {  
		        LunarInfo.makeUTCCalendar();  
		        synchronized (utcCal) {  
		            utcCal.clear();  
		            utcCal.setTimeInMillis(date.getTime());  
		            return utcCal.get(Calendar.DAY_OF_MONTH);  
		        }  
		    }  
		 
		/**
		 * 返回全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数。
		 * 
		 * @param y
		 *            指定年份
		 * @param m
		 *            指定月份
		 * @param d
		 *            指定日期
		 * @param h
		 *            指定小时
		 * @param min
		 *            指定分钟
		 * @param sec
		 *            指定秒数
		 * @return 全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数
		 */
		public static synchronized long UTC(int y, int m, int d, int h, int min,
				int sec) {
			LunarInfo.makeUTCCalendar();
			synchronized (utcCal) {
				utcCal.clear();
				utcCal.set(y, m, d, h, min, sec);
				return utcCal.getTimeInMillis();
			}
		}

		private static synchronized void makeUTCCalendar() {
			if (LunarInfo.utcCal == null) {
				LunarInfo.utcCal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			}
		}

		// ----------------------------------------------------干支历
		/**
		 * 取得干支历字符串
		 * 
		 * @return 干支历字符串(例:甲子年甲子月甲子日)
		 */
		public String getCyclicalDateString() {
			this.getCyclicaMonth();
			String s = "巳";
			String hours = null;
			// 判断时辰
			String hour = getCyclicaDay().substring(0, 1);
			if (hour.equals("甲") || hour.equals("己")) {
				String[] hor = { "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未",
						"壬申", "癸酉", "甲戌", "乙亥" };
				for (String a : hor) {
					if (a.substring(1, 2).equals(s)) {
						hours = a;
					}
				}
			} else if (hour.equals("乙") || hour.equals("庚")) {
				String[] hor = { "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
						"甲申", "乙酉", "丙戌", "丁亥" };
				for (String a : hor) {
					if (a.substring(1, 2).equals(s)) {
						hours = a;
					}
				}
			} else if (hour.equals("丙") || hour.equals("辛")) {
				String[] hor = { "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳", "甲午", "乙未",
						"丙申", "丁酉", "戊戌", "己亥" };
				for (String a : hor) {
					if (a.substring(1, 2).equals(s)) {
						hours = a;
					}
				}
			} else if (hour.equals("丁") || hour.equals("壬")) {
				String[] hor = { "庚子", "辛丑", "壬寅", "癸卯", "甲辰", "乙巳", "丙午", "丁未",
						"戊申", "己酉", "庚戌", "辛亥" };
				for (String a : hor) {
					if (a.substring(1, 2).equals(s)) {
						hours = a;
					}
				}
			} else {
				String[] hor = { "壬子", "癸丑", "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未",
						"庚申", "辛酉", "壬戌", "癸亥" };
				for (String a : hor) {
					if (a.substring(1, 2).equals(s)) {
						hours = a;
					}
				}
			}
			return this.getCyclicaYear() + "年" + this.getCyclicaMonth() + "月"
					+ this.getCyclicaDay() + "日" + hours + "时";
		}

		/**
		 * 取得干支月字符串
		 * 
		 * @return 干支月字符串
		 */
		public String getCyclicaMonth() {
			return LunarInfo.getCyclicalString(this.cyclicalMonth);
		}

		/**
		 * 干支字符串
		 * 
		 * @param cyclicalNumber
		 *            指定干支位置(数字,0为甲子)
		 * @return 干支字符串
		 */
		private static String getCyclicalString(int cyclicalNumber) {
			return LunarInfo.Tianan[LunarInfo.getTianan(cyclicalNumber)]
					+ LunarInfo.Deqi[LunarInfo.getDeqi(cyclicalNumber)];
		}

		/**
		 * 获得天干
		 * 
		 * @param cyclicalNumber
		 * @return 天干 (数字)
		 */
		private static int getTianan(int cyclicalNumber) {
			return cyclicalNumber % 10;
		}

		/**
		 * 获得地支
		 * 
		 * @param cyclicalNumber
		 * @return 地支 (数字)
		 */
		private static int getDeqi(int cyclicalNumber) {
			return cyclicalNumber % 12;
		}

		/**
		 * 取得干支年字符串
		 * 
		 * @return 干支年字符串
		 */
		public String getCyclicaYear() {
			return LunarInfo.getCyclicalString(this.cyclicalYear);
		}

		/**
		 * 取得干支日字符串
		 * 
		 * @return 干支日字符串
		 */
		public String getCyclicaDay() {
			return LunarInfo.getCyclicalString(this.cyclicalDay);
		}
	}
	
	/**
	 * 干支日历
	 * @param dateStr (国历 yyyy-MM-dd)
	 * @return
	 */
	public static String[] getGanZhi(String dateStr) {
		String arr[] = new String[3];
		try {
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Date date = fmt.parse(dateStr);
			LunarInfo lunar = new LunarInfo(date);
			arr[0] = lunar.getCyclicaYear();
			arr[1] = lunar.getCyclicaMonth();
			arr[2] = lunar.getCyclicaDay();
		} catch (ParseException e) {
		}
		return arr;
	}
			
	//--------------------------------------闰月大小月----------------------------------
	static class SolarTerm {
		// ========角度变换===============
		private static final double rad = 180 * 3600 / Math.PI; // 每弧度的角秒数
		private static final double RAD = 180 / Math.PI; // 每弧度的角度数
		// ================日历计算===============
		private static final double J2000 = 2451545; // 2000年前儒略日数(2000-1-1
		// 12:00:00格林威治平时)

		// =========黄赤交角及黄赤坐标变换===========
		private static final double hcjjB[] = { 84381.448, -46.8150, -0.00059,
				0.001813 };// 黄赤交角系数表
		private static final double preceB[] = { 0, 50287.92262, 111.24406,
				0.07699, -0.23479, -0.00178, 0.00018, 0.00001 };// Date黄道上的岁差p

		private double Y = 2000;
		private double M = 1;
		private double D = 1;
		private double h = 12;
		private double m = 0;
		private double s = 0;

		private static final double[] dts = {
				// 世界时与原子时之差计算表
				-4000, 108371.7, -13036.80, 392.000, 0.0000, -500, 17201.0,
				-627.82, 16.170, -0.3413, -150, 12200.6, -346.41, 5.403, -0.1593,
				150, 9113.8, -328.13, -1.647, 0.0377, 500, 5707.5, -391.41, 0.915,
				0.3145, 900, 2203.4, -283.45, 13.034, -0.1778, 1300, 490.1, -57.35,
				2.085, -0.0072, 1600, 120.0, -9.81, -1.532, 0.1403, 1700, 10.2,
				-0.91, 0.510, -0.0370, 1800, 13.4, -0.72, 0.202, -0.0193, 1830,
				7.8, -1.81, 0.416, -0.0247, 1860, 8.3, -0.13, -0.406, 0.0292, 1880,
				-5.4, 0.32, -0.183, 0.0173, 1900, -2.3, 2.06, 0.169, -0.0135, 1920,
				21.2, 1.69, -0.304, 0.0167, 1940, 24.2, 1.22, -0.064, 0.0031, 1960,
				33.2, 0.51, 0.231, -0.0109, 1980, 51.0, 1.29, -0.026, 0.0032, 2000,
				64.7, -1.66, 5.224, -0.2905, 2150, 279.4, 732.95, 429.579, 0.0158,
				6000 };

		// 取整数部分
		public static double int2(double v) {
			v = Math.floor(v);
			if (v < 0)
				return v + 1;

			return v;
		}

		// 对超过0-2PI的角度转为0-2PI
		public static double rad2mrad(double v) {
			v = v % (2 * Math.PI);
			if (v < 0)
				return v + 2 * Math.PI;

			return v;
		}

		// 计算世界时与原子时之差,传入年
		public double deltatT(double y) {
			int i = 0;
			for (i = 0; i < 100; i += 5)
				if (y < dts[i + 5] || i == 95)
					break;

			double t1 = (y - dts[i]) / (dts[i + 5] - dts[i]) * 10;
			double t2 = t1 * t1;
			double t3 = t2 * t1;
			return dts[i + 1] + dts[i + 2] * t1 + dts[i + 3] * t2 + dts[i + 4] * t3;
		}

		// 传入儒略日(J2000起算),计算UTC与原子时的差(单位:日)
		public double deltatT2(double jd) {
			return this.deltatT(jd / 365.2425 + 2000) / 86400.0;
		}

		// 公历转儒略日,UTC=1表示原日期是UTC
		public double toJD(boolean UTC) {
			double y = this.Y; // 取出年月
			double m = this.M;
			double n = 0;

			if (m <= 2) {
				m += 12;
				y--;
			}

			if (this.Y * 372 + this.M * 31 + this.D >= 588829) {
				// 判断是否为格里高利历日1582*372+10*31+15
				n = int2(y / 100);
				n = 2 - n + int2(n / 4);// 加百年闰
			}

			n += int2(365.2500001 * (y + 4716)); // 加上年引起的偏移日数
			n += int2(30.6 * (m + 1)) + this.D; // 加上月引起的偏移日数及日偏移数
			n += ((this.s / 60 + this.m) / 60 + this.h) / 24 - 1524.5;
			if (UTC)
				return n + this.deltatT2(n - J2000);

			return n;
		}

		// 儒略日数转公历,UTC=1表示目标公历是UTC
		public void setFromJD(double jd, boolean UTC) {
			if (UTC)
				jd -= this.deltatT2(jd - J2000);

			jd += 0.5;

			// 取得日数的整数部份A及小数部分F
			double A = int2(jd);
			double F = jd - A;
			double D;

			if (A > 2299161) {
				D = int2((A - 1867216.25) / 36524.25);
				A += 1 + D - int2(D / 4);
			}
			A += 1524; // 向前移4年零2个月
			this.Y = int2((A - 122.1) / 365.25);// 年
			D = A - int2(365.25 * this.Y); // 去除整年日数后余下日数
			this.M = int2(D / 30.6001); // 月数
			this.D = D - int2(this.M * 30.6001);// 去除整月日数后余下日数
			this.Y -= 4716;
			this.M--;
			if (this.M > 12) {
				this.M -= 12;
			}
			if (this.M <= 2) {
				this.Y++;
			}
			// 日的小数转为时分秒
			F *= 24;
			this.h = int2(F);
			F -= this.h;
			F *= 60;
			this.m = int2(F);
			F -= this.m;
			F *= 60;
			this.s = F;
		}

		// 设置时间,参数例:"20000101 120000"或"20000101"
		public void setFromStr(String s) {
			this.Y = Double.parseDouble(s.substring(0, 4));
			this.M = Double.parseDouble(s.substring(4, 2));
			this.D = Double.parseDouble(s.substring(6, 2));
			this.h = Double.parseDouble(s.substring(9, 2));
			this.m = Double.parseDouble(s.substring(11, 2));
			this.s = Double.parseDouble(s.substring(13, 2)); /* 将5改为了2 */
		}

		// 日期转为串
		public String toStr() {
			String Y = "     " + (int) this.Y;
			String M = "0" + (int) this.M;
			String D = "0" + (int) this.D;

			double h = this.h, m = this.m, s = Math.floor(this.s + .5);
			if (s >= 60) {
				s -= 60;
				m++;
			}
			if (m >= 60) {
				m -= 60;
				h++;
			}
			String sh = "0" + (int) h, sm = "0" + (int) m, ss = "0" + (int) s;
			Y = Y.substring(Y.length() - 5, Y.length());
			M = M.substring(M.length() - 2, M.length());
			D = D.substring(D.length() - 2, D.length());
			sh = sh.substring(sh.length() - 2, sh.length());
			sm = sm.substring(sm.length() - 2, sm.length());
			ss = ss.substring(ss.length() - 2, ss.length());
			return Y + "-" + M + "-" + D + " " + sh + ":" + sm + ":" + ss;
		}

		// 算出:jd转到当地UTC后,UTC日数的整数部分或小数部分
		// 基于J2000力学时jd的起算点是12:00:00时,所以跳日时刻发生在12:00:00,这与日历计算发生矛盾
		// 把jd改正为00:00:00起算,这样儒略日的跳日动作就与日期的跳日同步
		// 改正方法为jd=jd+0.5-deltatT+shiqu/24.0
		// 把儒略日的起点移动-0.5(即前移12小时)
		// 式中shiqu是时区,北京的起算点是-8小时,shiqu取8
		public double Dint_dec(double jd, int shiqu, boolean dec) {
			double u = jd + 0.5 - this.deltatT2(jd) + shiqu / 24.0;
			if (dec)
				return Math.floor(u); // 返回整数部分
			else
				return u - Math.floor(u); // 返回小数部分
		}

		// 计算两个日期的相差的天数,输入字串格式日期,如:"20080101"
		double d1_d2(String d1, String d2) {
			double Y = this.Y, M = this.M, D = this.D, h = this.h, m = this.m, s = this.s; // 备份原来的数据

			this.setFromStr(d1.substring(0, 8) + " 120000");
			double jd1 = this.toJD(false);
			this.setFromStr(d2.substring(0, 8) + " 120000");
			double jd2 = this.toJD(false);

			this.Y = Y;
			this.M = M;
			this.D = D;
			this.h = h;
			this.m = m;
			this.s = s; // 还原
			if (jd1 > jd2)
				return Math.floor(jd1 - jd2 + .0001);
			else
				return -Math.floor(jd2 - jd1 + .0001);
		}

		// 返回黄赤交角(常规精度),短期精度很高
		public static double hcjj1(double t) {
			double t1 = t / 36525;
			double t2 = t1 * t1;
			double t3 = t2 * t1;
			return (hcjjB[0] + hcjjB[1] * t1 + hcjjB[2] * t2 + hcjjB[3] * t3) / rad;
		}

		// 黄赤转换(黄赤坐标旋转)
		public static void HCconv(double[] JW, double E) {
			// 黄道赤道坐标变换,赤到黄E取负
			double HJ = rad2mrad(JW[0]), HW = JW[1];
			double sinE = Math.sin(E), cosE = Math.cos(E);
			double sinW = cosE * Math.sin(HW) + sinE * Math.cos(HW) * Math.sin(HJ);
			double J = Math.atan2(Math.sin(HJ) * cosE - Math.tan(HW) * sinE,
					Math.cos(HJ));
			JW[0] = rad2mrad(J);
			JW[1] = Math.asin(sinW);
		}

		// 补岁差
		public static void addPrece(double jd, double[] zb) {
			int i;
			double t = 1, v = 0, t1 = jd / 365250;
			for (i = 1; i < 8; i++) {
				t *= t1;
				v += preceB[i] * t;
			}
			zb[0] = rad2mrad(zb[0] + (v + 2.9965 * t1) / rad);
		}

		// ===============光行差==================
		private static final double GXC_e[] = { 0.016708634, -0.000042037,
				-0.0000001267 }; // 离心率
		private static final double GXC_p[] = { 102.93735 / RAD, 1.71946 / RAD,
				0.00046 / RAD }; // 近点
		private static final double GXC_l[] = { 280.4664567 / RAD,
				36000.76982779 / RAD, 0.0003032028 / RAD, 1 / 49931000 / RAD,
				-1 / 153000000 / RAD }; // 太平黄经
		private static final double GXC_k = 20.49552 / rad; // 光行差常数

		// 恒星周年光行差计算(黄道坐标中)
		public static void addGxc(double t, double[] zb) {
			double t1 = t / 36525;
			double t2 = t1 * t1;
			double t3 = t2 * t1;
			double t4 = t3 * t1;
			double L = GXC_l[0] + GXC_l[1] * t1 + GXC_l[2] * t2 + GXC_l[3] * t3
					+ GXC_l[4] * t4;
			double p = GXC_p[0] + GXC_p[1] * t1 + GXC_p[2] * t2;
			double e = GXC_e[0] + GXC_e[1] * t1 + GXC_e[2] * t2;
			double dL = L - zb[0], dP = p - zb[0];
			zb[0] -= GXC_k * (Math.cos(dL) - e * Math.cos(dP)) / Math.cos(zb[1]);
			zb[1] -= GXC_k * Math.sin(zb[1]) * (Math.sin(dL) - e * Math.sin(dP));
			zb[0] = rad2mrad(zb[0]);
		}

		// ===============章动计算==================
		private static final double nutB[] = {// 章动表
		2.1824391966, -33.757045954, 0.0000362262, 3.7340E-08, -2.8793E-10,
				-171996, -1742, 92025, 89, 3.5069406862, 1256.663930738,
				0.0000105845, 6.9813E-10, -2.2815E-10, -13187, -16, 5736, -31,
				1.3375032491, 16799.418221925, -0.0000511866, 6.4626E-08,
				-5.3543E-10, -2274, -2, 977, -5, 4.3648783932, -67.514091907,
				0.0000724525, 7.4681E-08, -5.7586E-10, 2062, 2, -895, 5,
				0.0431251803, -628.301955171, 0.0000026820, 6.5935E-10, 5.5705E-11,
				-1426, 34, 54, -1, 2.3555557435, 8328.691425719, 0.0001545547,
				2.5033E-07, -1.1863E-09, 712, 1, -7, 0, 3.4638155059,
				1884.965885909, 0.0000079025, 3.8785E-11, -2.8386E-10, -517, 12,
				224, -6, 5.4382493597, 16833.175267879, -0.0000874129, 2.7285E-08,
				-2.4750E-10, -386, -4, 200, 0, 3.6930589926, 25128.109647645,
				0.0001033681, 3.1496E-07, -1.7218E-09, -301, 0, 129, -1,
				3.5500658664, 628.361975567, 0.0000132664, 1.3575E-09, -1.7245E-10,
				217, -5, -95, 3 };

		public static class ZD {
			public double Lon;
			public double Obl;
		}

		// 计算黄经章动及交角章动
		public static ZD nutation(double t) {
			ZD d = new ZD();
			d.Lon = 0;
			d.Obl = 0;
			t /= 36525;
			double c, t1 = t, t2 = t1 * t1, t3 = t2 * t1, t4 = t3 * t1;// t5=t4*t1;
			for (int i = 0; i < nutB.length; i += 9) {
				c = nutB[i] + nutB[i + 1] * t1 + nutB[i + 2] * t2 + nutB[i + 3]
						* t3 + nutB[i + 4] * t4;
				d.Lon += (nutB[i + 5] + nutB[i + 6] * t / 10) * Math.sin(c); // 黄经章动
				d.Obl += (nutB[i + 7] + nutB[i + 8] * t / 10) * Math.cos(c); // 交角章动
			}
			d.Lon /= rad * 10000; // 黄经章动
			d.Obl /= rad * 10000; // 交角章动
			return d;
		}

		// 本函数计算赤经章动及赤纬章动
		public static void nutationRaDec(double t, double[] zb) {
			double Ra = zb[0], Dec = zb[1];
			double E = hcjj1(t), sinE = Math.sin(E), cosE = Math.cos(E); // 计算黄赤交角
			ZD d = nutation(t); // 计算黄经章动及交角章动
			double cosRa = Math.cos(Ra), sinRa = Math.sin(Ra);
			double tanDec = Math.tan(Dec);
			zb[0] += (cosE + sinE * sinRa * tanDec) * d.Lon - cosRa * tanDec
					* d.Obl; // 赤经章动
			zb[1] += sinE * cosRa * d.Lon + sinRa * d.Obl; // 赤纬章动
			zb[0] = rad2mrad(zb[0]);
		}

		// =================以下是月球及地球运动参数表===================
		/***************************************
		 * 如果用记事本查看此代码,请在"格式"菜单中去除"自动换行" E10是关于地球的,格式如下:
		 * 它是一个数组,每3个数看作一条记录,每条记录的3个数记为A,B,C rec=A*cos(B+C*t); 式中t是J2000起算的儒略千年数
		 * 每条记录的计算结果(即rec)取和即得地球的日心黄经的周期量L0 E11格式如下: rec = A*cos*(B+C*t) *t,
		 * 取和后得泊松量L1 E12格式如下: rec = A*cos*(B+C*t) *t*t, 取和后得泊松量L2 E13格式如下: rec =
		 * A*cos*(B+C*t) *t*t*t, 取和后得泊松量L3 最后地球的地心黄经:L = L0+L1+L2+L3+...
		 * E20,E21,E22,E23...用于计算黄纬 M10,M11等是关于月球的,参数的用法请阅读Mnn()函数
		 *****************************************/
		// 地球运动VSOP87参数
		private static final double E10[] = { // 黄经周期项
		1.75347045673, 0.00000000000, 0.0000000000, 0.03341656456, 4.66925680417,
				6283.0758499914, 0.00034894275, 4.62610241759, 12566.1516999828,
				0.00003417571, 2.82886579606, 3.5231183490, 0.00003497056,
				2.74411800971, 5753.3848848968, 0.00003135896, 3.62767041758,
				77713.7714681205, 0.00002676218, 4.41808351397, 7860.4193924392,
				0.00002342687, 6.13516237631, 3930.2096962196, 0.00001273166,
				2.03709655772, 529.6909650946, 0.00001324292, 0.74246356352,
				11506.7697697936, 0.00000901855, 2.04505443513, 26.2983197998,
				0.00001199167, 1.10962944315, 1577.3435424478, 0.00000857223,
				3.50849156957, 398.1490034082, 0.00000779786, 1.17882652114,
				5223.6939198022, 0.00000990250, 5.23268129594, 5884.9268465832,
				0.00000753141, 2.53339053818, 5507.5532386674, 0.00000505264,
				4.58292563052, 18849.2275499742, 0.00000492379, 4.20506639861,
				775.5226113240, 0.00000356655, 2.91954116867, 0.0673103028,
				0.00000284125, 1.89869034186, 796.2980068164, 0.00000242810,
				0.34481140906, 5486.7778431750, 0.00000317087, 5.84901952218,
				11790.6290886588, 0.00000271039, 0.31488607649, 10977.0788046990,
				0.00000206160, 4.80646606059, 2544.3144198834, 0.00000205385,
				1.86947813692, 5573.1428014331, 0.00000202261, 2.45767795458,
				6069.7767545534, 0.00000126184, 1.08302630210, 20.7753954924,
				0.00000155516, 0.83306073807, 213.2990954380, 0.00000115132,
				0.64544911683, 0.9803210682, 0.00000102851, 0.63599846727,
				4694.0029547076, 0.00000101724, 4.26679821365, 7.1135470008,
				0.00000099206, 6.20992940258, 2146.1654164752, 0.00000132212,
				3.41118275555, 2942.4634232916, 0.00000097607, 0.68101272270,
				155.4203994342, 0.00000085128, 1.29870743025, 6275.9623029906,
				0.00000074651, 1.75508916159, 5088.6288397668, 0.00000101895,
				0.97569221824, 15720.8387848784, 0.00000084711, 3.67080093025,
				71430.6956181291, 0.00000073547, 4.67926565481, 801.8209311238,
				0.00000073874, 3.50319443167, 3154.6870848956, 0.00000078756,
				3.03698313141, 12036.4607348882, 0.00000079637, 1.80791330700,
				17260.1546546904, 0.00000085803, 5.98322631256, 161000.6857376741,
				0.00000056963, 2.78430398043, 6286.5989683404, 0.00000061148,
				1.81839811024, 7084.8967811152, 0.00000069627, 0.83297596966,
				9437.7629348870, 0.00000056116, 4.38694880779, 14143.4952424306,
				0.00000062449, 3.97763880587, 8827.3902698748, 0.00000051145,
				0.28306864501, 5856.4776591154, 0.00000055577, 3.47006009062,
				6279.5527316424, 0.00000041036, 5.36817351402, 8429.2412664666,
				0.00000051605, 1.33282746983, 1748.0164130670, 0.00000051992,
				0.18914945834, 12139.5535091068, 0.00000049000, 0.48735065033,
				1194.4470102246, 0.00000039200, 6.16832995016, 10447.3878396044,
				0.00000035566, 1.77597314691, 6812.7668150860, 0.00000036770,
				6.04133859347, 10213.2855462110, 0.00000036596, 2.56955238628,
				1059.3819301892, 0.00000033291, 0.59309499459, 17789.8456197850,
				0.00000035954, 1.70876111898, 2352.8661537718 };
		private static final double E11[] = { // 黄经泊松1项
		6283.31966747491, 0.00000000000, 0.0000000000, 0.00206058863,
				2.67823455584, 6283.0758499914, 0.00004303430, 2.63512650414,
				12566.1516999828, 0.00000425264, 1.59046980729, 3.5231183490,
				0.00000108977, 2.96618001993, 1577.3435424478, 0.00000093478,
				2.59212835365, 18849.2275499742, 0.00000119261, 5.79557487799,
				26.2983197998, 0.00000072122, 1.13846158196, 529.6909650946,
				0.00000067768, 1.87472304791, 398.1490034082, 0.00000067327,
				4.40918235168, 5507.5532386674, 0.00000059027, 2.88797038460,
				5223.6939198022, 0.00000055976, 2.17471680261, 155.4203994342,
				0.00000045407, 0.39803079805, 796.2980068164, 0.00000036369,
				0.46624739835, 775.5226113240, 0.00000028958, 2.64707383882,
				7.1135470008, 0.00000019097, 1.84628332577, 5486.7778431750,
				0.00000020844, 5.34138275149, 0.9803210682, 0.00000018508,
				4.96855124577, 213.2990954380, 0.00000016233, 0.03216483047,
				2544.3144198834, 0.00000017293, 2.99116864949, 6275.9623029906 };
		private static final double E12[] = { // 黄经泊松2项
		0.00052918870, 0.00000000000, 0.0000000000, 0.00008719837, 1.07209665242,
				6283.0758499914, 0.00000309125, 0.86728818832, 12566.1516999828,
				0.00000027339, 0.05297871691, 3.5231183490, 0.00000016334,
				5.18826691036, 26.2983197998, 0.00000015752, 3.68457889430,
				155.4203994342, 0.00000009541, 0.75742297675, 18849.2275499742,
				0.00000008937, 2.05705419118, 77713.7714681205, 0.00000006952,
				0.82673305410, 775.5226113240, 0.00000005064, 4.66284525271,
				1577.3435424478 };
		private static final double E13[] = { 0.00000289226, 5.84384198723,
				6283.0758499914, 0.00000034955, 0.00000000000, 0.0000000000,
				0.00000016819, 5.48766912348, 12566.1516999828 };
		private static final double E14[] = { 0.00000114084, 3.14159265359,
				0.0000000000, 0.00000007717, 4.13446589358, 6283.0758499914,
				0.00000000765, 3.83803776214, 12566.1516999828 };
		private static final double E15[] = { 0.00000000878, 3.14159265359,
				0.0000000000 };
		private static final double E20[] = { // 黄纬周期项
		0.00000279620, 3.19870156017, 84334.6615813083, 0.00000101643,
				5.42248619256, 5507.5532386674, 0.00000080445, 3.88013204458,
				5223.6939198022, 0.00000043806, 3.70444689758, 2352.8661537718,
				0.00000031933, 4.00026369781, 1577.3435424478, 0.00000022724,
				3.98473831560, 1047.7473117547, 0.00000016392, 3.56456119782,
				5856.4776591154, 0.00000018141, 4.98367470263, 6283.0758499914,
				0.00000014443, 3.70275614914, 9437.7629348870, 0.00000014304,
				3.41117857525, 10213.2855462110 };
		private static final double E21[] = { 0.00000009030, 3.89729061890,
				5507.5532386674, 0.00000006177, 1.73038850355, 5223.6939198022 };
		private static final double E30[] = { // 距离周期项
		1.00013988799, 0.00000000000, 0.0000000000, 0.01670699626, 3.09846350771,
				6283.0758499914, 0.00013956023, 3.05524609620, 12566.1516999828,
				0.00003083720, 5.19846674381, 77713.7714681205, 0.00001628461,
				1.17387749012, 5753.3848848968, 0.00001575568, 2.84685245825,
				7860.4193924392, 0.00000924799, 5.45292234084, 11506.7697697936,
				0.00000542444, 4.56409149777, 3930.2096962196 };
		private static final double E31[] = { 0.00103018608, 1.10748969588,
				6283.0758499914, 0.00001721238, 1.06442301418, 12566.1516999828,
				0.00000702215, 3.14159265359, 0.0000000000 };
		private static final double E32[] = { 0.00004359385, 5.78455133738,
				6283.0758499914 };
		private static final double E33[] = { 0.00000144595, 4.27319435148,
				6283.0758499914 };
		// 月球运动参数
		private static final double M10[] = { 22639.5858800, 2.3555545723,
				8328.6914247251, 1.5231275E-04, 2.5041111E-07, -1.1863391E-09,
				4586.4383203, 8.0413790709, 7214.0628654588, -2.1850087E-04,
				-1.8646419E-07, 8.7760973E-10, 2369.9139357, 10.3969336431,
				15542.7542901840, -6.6188121E-05, 6.3946925E-08, -3.0872935E-10,
				769.0257187, 4.7111091445, 16657.3828494503, 3.0462550E-04,
				5.0082223E-07, -2.3726782E-09, -666.4175399, -0.0431256817,
				628.3019552485, -2.6638815E-06, 6.1639211E-10, -5.4439728E-11,
				-411.5957339, 3.2558104895, 16866.9323152810, -1.2804259E-04,
				-9.8998954E-09, 4.0433461E-11, 211.6555524, 5.6858244986,
				-1114.6285592663, -3.7081362E-04, -4.3687530E-07, 2.0639488E-09,
				205.4359530, 8.0845047526, 6585.7609102104, -2.1583699E-04,
				-1.8708058E-07, 9.3204945E-10, 191.9561973, 12.7524882154,
				23871.4457149091, 8.6124629E-05, 3.1435804E-07, -1.4950684E-09,
				164.7286185, 10.4400593249, 14914.4523349355, -6.3524240E-05,
				6.3330532E-08, -2.5428962E-10, -147.3213842, -2.3986802540,
				-7700.3894694766, -1.5497663E-04, -2.4979472E-07, 1.1318993E-09,
				-124.9881185, 5.1984668216, 7771.3771450920, -3.3094061E-05,
				3.1973462E-08, -1.5436468E-10, -109.3803637, 2.3124288905,
				8956.9933799736, 1.4964887E-04, 2.5102751E-07, -1.2407788E-09,
				55.1770578, 7.1411231536, -1324.1780250970, 6.1854469E-05,
				7.3846820E-08, -3.4916281E-10, -45.0996092, 5.6113650618,
				25195.6237400061, 2.4270161E-05, 2.4051122E-07, -1.1459056E-09,
				39.5333010, -0.9002559173, -8538.2408905558, 2.8035534E-04,
				2.6031101E-07, -1.2267725E-09, 38.4298346, 18.4383127140,
				22756.8171556428, -2.8468899E-04, -1.2251727E-07, 5.6888037E-10,
				36.1238141, 7.0666637168, 24986.0742741754, 4.5693825E-04,
				7.5123334E-07, -3.5590172E-09, 30.7725751, 16.0827581417,
				14428.1257309177, -4.3700174E-04, -3.7292838E-07, 1.7552195E-09,
				-28.3971008, 7.9982533891, 7842.3648207073, -2.2116475E-04,
				-1.8584780E-07, 8.2317000E-10, -24.3582283, 10.3538079614,
				16171.0562454324, -6.8852003E-05, 6.4563317E-08, -3.6316908E-10,
				-18.5847068, 2.8429122493, -557.3142796331, -1.8540681E-04,
				-2.1843765E-07, 1.0319744E-09, 17.9544674, 5.1553411398,
				8399.6791003405, -3.5757942E-05, 3.2589854E-08, -2.0880440E-10,
				14.5302779, 12.7956138971, 23243.1437596606, 8.8788511E-05,
				3.1374165E-07, -1.4406287E-09, 14.3796974, 15.1080427876,
				32200.1371396342, 2.3843738E-04, 5.6476915E-07, -2.6814075E-09,
				14.2514576, -24.0810366320, -2.3011998397, 1.5231275E-04,
				2.5041111E-07, -1.1863391E-09, 13.8990596, 20.7938672862,
				31085.5085803679, -1.3237624E-04, 1.2789385E-07, -6.1745870E-10,
				13.1940636, 3.3302699264, -9443.3199839914, -5.2312637E-04,
				-6.8728642E-07, 3.2502879E-09, -9.6790568, -4.7542348263,
				-16029.0808942018, -3.0728938E-04, -5.0020584E-07, 2.3182384E-09,
				-9.3658635, 11.2971895604, 24080.9951807398, -3.4654346E-04,
				-1.9636409E-07, 9.1804319E-10, 8.6055318, 5.7289501804,
				-1742.9305145148, -3.6814974E-04, -4.3749170E-07, 2.1183885E-09,
				-8.4530982, 7.5540213938, 16100.0685698171, 1.1921869E-04,
				2.8238458E-07, -1.3407038E-09, 8.0501724, 10.4831850066,
				14286.1503796870, -6.0860358E-05, 6.2714140E-08, -1.9984990E-10,
				-7.6301553, 4.6679834628, 17285.6848046987, 3.0196162E-04,
				5.0143862E-07, -2.4271179E-09, -7.4474952, -0.0862513635,
				1256.6039104970, -5.3277630E-06, 1.2327842E-09, -1.0887946E-10,
				7.3712011, 8.1276304344, 5957.4589549619, -2.1317311E-04,
				-1.8769697E-07, 9.8648918E-10, 7.0629900, 0.9591375719,
				33.7570471374, -3.0829302E-05, -3.6967043E-08, 1.7385419E-10,
				-6.3831491, 9.4966777258, 7004.5133996281, 2.1416722E-04,
				3.2425793E-07, -1.5355019E-09, -5.7416071, 13.6527441326,
				32409.6866054649, -1.9423071E-04, 5.4047029E-08, -2.6829589E-10,
				4.3740095, 18.4814383957, 22128.5152003943, -2.8202511E-04,
				-1.2313366E-07, 6.2332010E-10, -3.9976134, 7.9669196340,
				33524.3151647312, 1.7658291E-04, 4.9092233E-07, -2.3322447E-09,
				-3.2096876, 13.2398458924, 14985.4400105508, -2.5159493E-04,
				-1.5449073E-07, 7.2324505E-10, -2.9145404, 12.7093625336,
				24499.7476701576, 8.3460748E-05, 3.1497443E-07, -1.5495082E-09,
				2.7318890, 16.1258838235, 13799.8237756692, -4.3433786E-04,
				-3.7354477E-07, 1.8096592E-09, -2.5679459, -2.4418059357,
				-7072.0875142282, -1.5764051E-04, -2.4917833E-07, 1.0774596E-09,
				-2.5211990, 7.9551277074, 8470.6667759558, -2.2382863E-04,
				-1.8523141E-07, 7.6873027E-10, 2.4888871, 5.6426988169,
				-486.3266040178, -3.7347750E-04, -4.3625891E-07, 2.0095091E-09,
				2.1460741, 7.1842488353, -1952.4799803455, 6.4518350E-05,
				7.3230428E-08, -2.9472308E-10, 1.9777270, 23.1494218585,
				39414.2000050930, 1.9936508E-05, 3.7830496E-07, -1.8037978E-09,
				1.9336825, 9.4222182890, 33314.7656989005, 6.0925100E-04,
				1.0016445E-06, -4.7453563E-09, 1.8707647, 20.8369929680,
				30457.2066251194, -1.2971236E-04, 1.2727746E-07, -5.6301898E-10,
				-1.7529659, 0.4873576771, -8886.0057043583, -3.3771956E-04,
				-4.6884877E-07, 2.2183135E-09, -1.4371624, 7.0979974718,
				-695.8760698485, 5.9190587E-05, 7.4463212E-08, -4.0360254E-10,
				-1.3725701, 1.4552986550, -209.5494658307, 4.3266809E-04,
				5.1072212E-07, -2.4131116E-09, 1.2618162, 7.5108957121,
				16728.3705250656, 1.1655481E-04, 2.8300097E-07, -1.3951435E-09 };
		private static final double M11[] = { 1.6768000, -0.0431256817,
				628.3019552485, -2.6638815E-06, 6.1639211E-10, -5.4439728E-11,
				0.5164200, 11.2260974062, 6585.7609102104, -2.1583699E-04,
				-1.8708058E-07, 9.3204945E-10, 0.4138300, 13.5816519784,
				14914.4523349355, -6.3524240E-05, 6.3330532E-08, -2.5428962E-10,
				0.3711500, 5.5402729076, 7700.3894694766, 1.5497663E-04,
				2.4979472E-07, -1.1318993E-09, 0.2756000, 2.3124288905,
				8956.9933799736, 1.4964887E-04, 2.5102751E-07, -1.2407788E-09,
				0.2459863, -25.6198212459, -2.3011998397, 1.5231275E-04,
				2.5041111E-07, -1.1863391E-09, 0.0711800, 7.9982533891,
				7842.3648207073, -2.2116475E-04, -1.8584780E-07, 8.2317000E-10,
				0.0612800, 10.3538079614, 16171.0562454324, -6.8852003E-05,
				6.4563317E-08, -3.6316908E-10 };
		private static final double M12[] = { 0.0048700, -0.0431256817,
				628.3019552485, -2.6638815E-06, 6.1639211E-10, -5.4439728E-11,
				0.0022800, -27.1705318325, -2.3011998397, 1.5231275E-04,
				2.5041111E-07, -1.1863391E-09, 0.0015000, 11.2260974062,
				6585.7609102104, -2.1583699E-04, -1.8708058E-07, 9.3204945E-10 };
		private static final double M20[] = { 18461.2400600, 1.6279052448,
				8433.4661576405, -6.4021295E-05, -4.9499477E-09, 2.0216731E-11,
				1010.1671484, 3.9834598170, 16762.1575823656, 8.8291456E-05,
				2.4546117E-07, -1.1661223E-09, 999.6936555, 0.7276493275,
				-104.7747329154, 2.1633405E-04, 2.5536106E-07, -1.2065558E-09,
				623.6524746, 8.7690283983, 7109.2881325435, -2.1668263E-06,
				6.8896872E-08, -3.2894608E-10, 199.4837596, 9.6692843156,
				15647.5290230993, -2.8252217E-04, -1.9141414E-07, 8.9782646E-10,
				166.5741153, 6.4134738261, -1219.4032921817, -1.5447958E-04,
				-1.8151424E-07, 8.5739300E-10, 117.2606951, 12.0248388879,
				23976.2204478244, -1.3020942E-04, 5.8996977E-08, -2.8851262E-10,
				61.9119504, 6.3390143893, 25090.8490070907, 2.4060421E-04,
				4.9587228E-07, -2.3524614E-09, 33.3572027, 11.1245829706,
				15437.9795572686, 1.5014592E-04, 3.1930799E-07, -1.5152852E-09,
				31.7596709, 3.0832038997, 8223.9166918098, 3.6864680E-04,
				5.0577218E-07, -2.3928949E-09, 29.5766003, 8.8121540801,
				6480.9861772950, 4.9705523E-07, 6.8280480E-08, -2.7450635E-10,
				15.5662654, 4.0579192538, -9548.0947169068, -3.0679233E-04,
				-4.3192536E-07, 2.0437321E-09, 15.1215543, 14.3803934601,
				32304.9118725496, 2.2103334E-05, 3.0940809E-07, -1.4748517E-09,
				-12.0941511, 8.7259027166, 7737.5900877920, -4.8307078E-06,
				6.9513264E-08, -3.8338581E-10, 8.8681426, 9.7124099974,
				15019.2270678508, -2.7985829E-04, -1.9203053E-07, 9.5226618E-10,
				8.0450400, 0.6687636586, 8399.7091105030, -3.3191993E-05,
				3.2017096E-08, -1.5363746E-10, 7.9585542, 12.0679645696,
				23347.9184925760, -1.2754553E-04, 5.8380585E-08, -2.3407289E-10,
				7.4345550, 6.4565995078, -1847.7052474301, -1.5181570E-04,
				-1.8213063E-07, 9.1183272E-10, -6.7314363, -4.0265854988,
				-16133.8556271171, -9.0955337E-05, -2.4484477E-07, 1.1116826E-09,
				6.5795750, 16.8104074692, 14323.3509980023, -2.2066770E-04,
				-1.1756732E-07, 5.4866364E-10, -6.4600721, 1.5847795630,
				9061.7681128890, -6.6685176E-05, -4.3335556E-09, -3.4222998E-11,
				-6.2964773, 4.8837157343, 25300.3984729215, -1.9206388E-04,
				-1.4849843E-08, 6.0650192E-11, -5.6323538, -0.7707750092,
				733.0766881638, -2.1899793E-04, -2.5474467E-07, 1.1521161E-09,
				-5.3683961, 6.8263720663, 16204.8433027325, -9.7115356E-05,
				2.7023515E-08, -1.3414795E-10, -5.3112784, 3.9403341353,
				17390.4595376141, 8.5627574E-05, 2.4607756E-07, -1.2205621E-09,
				-5.0759179, 0.6845236457, 523.5272223331, 2.1367016E-04,
				2.5597745E-07, -1.2609955E-09, -4.8396143, -1.6710309265,
				-7805.1642023920, 6.1357413E-05, 5.5663398E-09, -7.4656459E-11,
				-4.8057401, 3.5705615768, -662.0890125485, 3.0927234E-05,
				3.6923410E-08, -1.7458141E-10, 3.9840545, 8.6945689615,
				33419.5404318159, 3.9291696E-04, 7.4628340E-07, -3.5388005E-09,
				3.6744619, 19.1659620415, 22652.0424227274, -6.8354947E-05,
				1.3284380E-07, -6.3767543E-10, 2.9984815, 20.0662179587,
				31190.2833132833, -3.4871029E-04, -1.2746721E-07, 5.8909710E-10,
				2.7986413, -2.5281611620, -16971.7070481963, 3.4437664E-04,
				2.6526096E-07, -1.2469893E-09, 2.4138774, 17.7106633865,
				22861.5918885581, -5.0102304E-04, -3.7787833E-07, 1.7754362E-09,
				2.1863132, 5.5132179088, -9757.6441827375, 1.2587576E-04,
				7.8796768E-08, -3.6937954E-10, 2.1461692, 13.4801375428,
				23766.6709819937, 3.0245868E-04, 5.6971910E-07, -2.7016242E-09,
				1.7659832, 11.1677086523, 14809.6776020201, 1.5280981E-04,
				3.1869159E-07, -1.4608454E-09, -1.6244212, 7.3137297434,
				7318.8375983742, -4.3483492E-04, -4.4182525E-07, 2.0841655E-09,
				1.5813036, 5.4387584720, 16552.6081165349, 5.2095955E-04,
				7.5618329E-07, -3.5792340E-09, 1.5197528, 16.7359480324,
				40633.6032972747, 1.7441609E-04, 5.5981921E-07, -2.6611908E-09,
				1.5156341, 1.7023646816, -17876.7861416319, -4.5910508E-04,
				-6.8233647E-07, 3.2300712E-09, 1.5102092, 5.4977296450,
				8399.6847301375, -3.3094061E-05, 3.1973462E-08, -1.5436468E-10,
				-1.3178223, 9.6261586339, 16275.8309783478, -2.8518605E-04,
				-1.9079775E-07, 8.4338673E-10, -1.2642739, 11.9817132061,
				24604.5224030729, -1.3287330E-04, 5.9613369E-08, -3.4295235E-10,
				1.1918723, 22.4217725310, 39518.9747380084, -1.9639754E-04,
				1.2294390E-07, -5.9724197E-10, 1.1346110, 14.4235191419,
				31676.6099173011, 2.4767216E-05, 3.0879170E-07, -1.4204120E-09,
				1.0857810, 8.8552797618, 5852.6842220465, 3.1609367E-06,
				6.7664088E-08, -2.2006663E-10, -1.0193852, 7.2392703065,
				33629.0898976466, -3.9751134E-05, 2.3556127E-07, -1.1256889E-09,
				-0.8227141, 11.0814572888, 16066.2815125171, 1.4748204E-04,
				3.1992438E-07, -1.5697249E-09, 0.8042238, 3.5274358950,
				-33.7870573000, 2.8263353E-05, 3.7539802E-08, -2.2902113E-10,
				0.8025939, 6.7832463846, 16833.1452579809, -9.9779237E-05,
				2.7639907E-08, -1.8858767E-10, -0.7931866, -6.3821400710,
				-24462.5470518423, -2.4326809E-04, -4.9525589E-07, 2.2980217E-09,
				-0.7910153, 6.3703481443, -591.1013369332, -1.5714346E-04,
				-1.8089785E-07, 8.0295327E-10, -0.6674056, 9.1819266386,
				24533.5347274576, 5.5197395E-05, 2.7743463E-07, -1.3204870E-09,
				0.6502226, 4.1010449356, -10176.3966721553, -3.0412845E-04,
				-4.3254175E-07, 2.0981718E-09, -0.6388131, 6.2958887075,
				25719.1509623392, 2.3794032E-04, 4.9648867E-07, -2.4069012E-09 };
		private static final double M21[] = { 0.0743000, 11.9537467337,
				6480.9861772950, 4.9705523E-07, 6.8280480E-08, -2.7450635E-10,
				0.0304300, 8.7259027166, 7737.5900877920, -4.8307078E-06,
				6.9513264E-08, -3.8338581E-10, 0.0222900, 12.8540026510,
				15019.2270678508, -2.7985829E-04, -1.9203053E-07, 9.5226618E-10,
				0.0199900, 15.2095572232, 23347.9184925760, -1.2754553E-04,
				5.8380585E-08, -2.3407289E-10, 0.0186900, 9.5981921614,
				-1847.7052474301, -1.5181570E-04, -1.8213063E-07, 9.1183272E-10,
				0.0169600, 7.1681781524, 16133.8556271171, 9.0955337E-05,
				2.4484477E-07, -1.1116826E-09, 0.0162300, 1.5847795630,
				9061.7681128890, -6.6685176E-05, -4.3335556E-09, -3.4222998E-11,
				0.0141900, -0.7707750092, 733.0766881638, -2.1899793E-04,
				-2.5474467E-07, 1.1521161E-09 };
		private static final double M30[] = { 385000.5290396, 1.5707963268,
				0.0000000000, 0.0000000E+00, 0.0000000E+00, 0.0000000E+00,
				-20905.3551378, 3.9263508990, 8328.6914247251, 1.5231275E-04,
				2.5041111E-07, -1.1863391E-09, -3699.1109330, 9.6121753977,
				7214.0628654588, -2.1850087E-04, -1.8646419E-07, 8.7760973E-10,
				-2955.9675626, 11.9677299699, 15542.7542901840, -6.6188121E-05,
				6.3946925E-08, -3.0872935E-10, -569.9251264, 6.2819054713,
				16657.3828494503, 3.0462550E-04, 5.0082223E-07, -2.3726782E-09,
				246.1584797, 7.2566208254, -1114.6285592663, -3.7081362E-04,
				-4.3687530E-07, 2.0639488E-09, -204.5861179, 12.0108556517,
				14914.4523349355, -6.3524240E-05, 6.3330532E-08, -2.5428962E-10,
				-170.7330791, 14.3232845422, 23871.4457149091, 8.6124629E-05,
				3.1435804E-07, -1.4950684E-09, -152.1378118, 9.6553010794,
				6585.7609102104, -2.1583699E-04, -1.8708058E-07, 9.3204945E-10,
				-129.6202242, -0.8278839272, -7700.3894694766, -1.5497663E-04,
				-2.4979472E-07, 1.1318993E-09, 108.7427014, 6.7692631483,
				7771.3771450920, -3.3094061E-05, 3.1973462E-08, -1.5436468E-10,
				104.7552944, 3.8832252173, 8956.9933799736, 1.4964887E-04,
				2.5102751E-07, -1.2407788E-09, 79.6605685, 0.6705404095,
				-8538.2408905558, 2.8035534E-04, 2.6031101E-07, -1.2267725E-09,
				48.8883284, 1.5276706450, 628.3019552485, -2.6638815E-06,
				6.1639211E-10, -5.4439728E-11, -34.7825237, 20.0091090408,
				22756.8171556428, -2.8468899E-04, -1.2251727E-07, 5.6888037E-10,
				30.8238599, 11.9246042882, 16171.0562454324, -6.8852003E-05,
				6.4563317E-08, -3.6316908E-10, 24.2084985, 9.5690497159,
				7842.3648207073, -2.2116475E-04, -1.8584780E-07, 8.2317000E-10,
				-23.2104305, 8.6374600436, 24986.0742741754, 4.5693825E-04,
				7.5123334E-07, -3.5590172E-09, -21.6363439, 17.6535544685,
				14428.1257309177, -4.3700174E-04, -3.7292838E-07, 1.7552195E-09,
				-16.6747239, 6.7261374666, 8399.6791003405, -3.5757942E-05,
				3.2589854E-08, -2.0880440E-10, 14.4026890, 4.9010662531,
				-9443.3199839914, -5.2312637E-04, -6.8728642E-07, 3.2502879E-09,
				-12.8314035, 14.3664102239, 23243.1437596606, 8.8788511E-05,
				3.1374165E-07, -1.4406287E-09, -11.6499478, 22.3646636130,
				31085.5085803679, -1.3237624E-04, 1.2789385E-07, -6.1745870E-10,
				-10.4447578, 16.6788391144, 32200.1371396342, 2.3843738E-04,
				5.6476915E-07, -2.6814075E-09, 10.3211071, 8.7119194804,
				-1324.1780250970, 6.1854469E-05, 7.3846820E-08, -3.4916281E-10,
				10.0562033, 7.2997465071, -1742.9305145148, -3.6814974E-04,
				-4.3749170E-07, 2.1183885E-09, -9.8844667, 12.0539813334,
				14286.1503796870, -6.0860358E-05, 6.2714140E-08, -1.9984990E-10,
				8.7515625, 6.3563649081, -9652.8694498221, -9.0458282E-05,
				-1.7656429E-07, 8.3717626E-10, -8.3791067, 4.4137085761,
				-557.3142796331, -1.8540681E-04, -2.1843765E-07, 1.0319744E-09,
				-7.0026961, -3.1834384995, -16029.0808942018, -3.0728938E-04,
				-5.0020584E-07, 2.3182384E-09, 6.3220032, 9.1248177206,
				16100.0685698171, 1.1921869E-04, 2.8238458E-07, -1.3407038E-09,
				5.7508579, 6.2387797896, 17285.6848046987, 3.0196162E-04,
				5.0143862E-07, -2.4271179E-09, -4.9501349, 9.6984267611,
				5957.4589549619, -2.1317311E-04, -1.8769697E-07, 9.8648918E-10,
				-4.4211770, 3.0260949818, -209.5494658307, 4.3266809E-04,
				5.1072212E-07, -2.4131116E-09, 4.1311145, 11.0674740526,
				7004.5133996281, 2.1416722E-04, 3.2425793E-07, -1.5355019E-09,
				-3.9579827, 20.0522347225, 22128.5152003943, -2.8202511E-04,
				-1.2313366E-07, 6.2332010E-10, 3.2582371, 14.8106422192,
				14985.4400105508, -2.5159493E-04, -1.5449073E-07, 7.2324505E-10,
				-3.1483020, 4.8266068163, 16866.9323152810, -1.2804259E-04,
				-9.8998954E-09, 4.0433461E-11, 2.6164092, 14.2801588604,
				24499.7476701576, 8.3460748E-05, 3.1497443E-07, -1.5495082E-09,
				2.3536310, 9.5259240342, 8470.6667759558, -2.2382863E-04,
				-1.8523141E-07, 7.6873027E-10, -2.1171283, -0.8710096090,
				-7072.0875142282, -1.5764051E-04, -2.4917833E-07, 1.0774596E-09,
				-1.8970368, 17.6966801503, 13799.8237756692, -4.3433786E-04,
				-3.7354477E-07, 1.8096592E-09, -1.7385258, 2.0581540038,
				-8886.0057043583, -3.3771956E-04, -4.6884877E-07, 2.2183135E-09,
				-1.5713944, 22.4077892948, 30457.2066251194, -1.2971236E-04,
				1.2727746E-07, -5.6301898E-10, -1.4225541, 24.7202181853,
				39414.2000050930, 1.9936508E-05, 3.7830496E-07, -1.8037978E-09,
				-1.4189284, 17.1661967915, 23314.1314352759, -9.9282182E-05,
				9.5920387E-08, -4.6309403E-10, 1.1655364, 3.8400995356,
				9585.2953352221, 1.4698499E-04, 2.5164390E-07, -1.2952185E-09,
				-1.1169371, 10.9930146158, 33314.7656989005, 6.0925100E-04,
				1.0016445E-06, -4.7453563E-09, 1.0656723, 1.4845449633,
				1256.6039104970, -5.3277630E-06, 1.2327842E-09, -1.0887946E-10,
				1.0586190, 11.9220903668, 8364.7398411275, -2.1850087E-04,
				-1.8646419E-07, 8.7760973E-10, -0.9333176, 9.0816920389,
				16728.3705250656, 1.1655481E-04, 2.8300097E-07, -1.3951435E-09,
				0.8624328, 12.4550876470, 6656.7485858257, -4.0390768E-04,
				-4.0490184E-07, 1.9095841E-09, 0.8512404, 4.3705828944,
				70.9876756153, -1.8807069E-04, -2.1782126E-07, 9.7753467E-10,
				-0.8488018, 16.7219647962, 31571.8351843857, 2.4110126E-04,
				5.6415276E-07, -2.6269678E-09, -0.7956264, 3.5134526588,
				-9095.5551701890, 9.4948529E-05, 4.1873358E-08, -1.9479814E-10 };
		private static final double M31[] = { 0.5139500, 12.0108556517,
				14914.4523349355, -6.3524240E-05, 6.3330532E-08, -2.5428962E-10,
				0.3824500, 9.6553010794, 6585.7609102104, -2.1583699E-04,
				-1.8708058E-07, 9.3204945E-10, 0.3265400, 3.9694765808,
				7700.3894694766, 1.5497663E-04, 2.4979472E-07, -1.1318993E-09,
				0.2639600, 0.7416325637, 8956.9933799736, 1.4964887E-04,
				2.5102751E-07, -1.2407788E-09, 0.1230200, -1.6139220085,
				628.3019552485, -2.6638815E-06, 6.1639211E-10, -5.4439728E-11,
				0.0775400, 8.7830116346, 16171.0562454324, -6.8852003E-05,
				6.4563317E-08, -3.6316908E-10, 0.0606800, 6.4274570623,
				7842.3648207073, -2.2116475E-04, -1.8584780E-07, 8.2317000E-10,
				0.0497000, 12.0539813334, 14286.1503796870, -6.0860358E-05,
				6.2714140E-08, -1.9984990E-10 };
		private static final double M1n[] = { 3.81034392032, 8.39968473021E+03,
				-3.31919929753E-05, // 月球平黄经系数
				3.20170955005E-08, -1.53637455544E-10 };

		// ==================日位置计算===================
		private double EnnT = 0; // 调用Enn前先设置EnnT时间变量

		// 计算E10,E11,E20等,即:某一组周期项或泊松项算出,计算前先设置EnnT时间
		public double Enn(double[] F) {
			double v = 0;
			for (int i = 0; i < F.length; i += 3)
				v += F[i] * Math.cos(F[i + 1] + EnnT * F[i + 2]);
			return v;
		}

		// 返回地球位置,日心Date黄道分点坐标
		public double[] earCal(double jd) {
			EnnT = jd / 365250;
			double llr[] = new double[3];
			double t1 = EnnT, t2 = t1 * t1, t3 = t2 * t1, t4 = t3 * t1, t5 = t4
					* t1;
			llr[0] = Enn(E10) + Enn(E11) * t1 + Enn(E12) * t2 + Enn(E13) * t3
					+ Enn(E14) * t4 + Enn(E15) * t5;
			llr[1] = Enn(E20) + Enn(E21) * t1;
			llr[2] = Enn(E30) + Enn(E31) * t1 + Enn(E32) * t2 + Enn(E33) * t3;
			llr[0] = rad2mrad(llr[0]);
			return llr;
		}

		// 传回jd时刻太阳的地心视黄经及黄纬
		public double[] sunCal2(double jd) {
			double[] sun = earCal(jd);
			sun[0] += Math.PI;
			sun[1] = -sun[1]; // 计算太阳真位置
			ZD d = nutation(jd);
			sun[0] = rad2mrad(sun[0] + d.Lon); // 补章动
			addGxc(jd, sun); // 补周年黄经光行差
			return sun; // 返回太阳视位置
		}

		// ==================月位置计算===================
		private double MnnT = 0; // 调用Mnn前先设置MnnT时间变量

		// 计算M10,M11,M20等,计算前先设置MnnT时间
		public double Mnn(double[] F) {
			double v = 0, t1 = MnnT, t2 = t1 * t1, t3 = t2 * t1, t4 = t3 * t1;
			for (int i = 0; i < F.length; i += 6)
				v += F[i]
						* Math.sin(F[i + 1] + t1 * F[i + 2] + t2 * F[i + 3] + t3
								* F[i + 4] + t4 * F[i + 5]);
			return v;
		}

		// 返回月球位置,返回地心Date黄道坐标
		public double[] moonCal(double jd) {
			MnnT = jd / 36525;
			double t1 = MnnT, t2 = t1 * t1, t3 = t2 * t1, t4 = t3 * t1;
			double[] llr = new double[3];
			llr[0] = (Mnn(M10) + Mnn(M11) * t1 + Mnn(M12) * t2) / rad;
			llr[1] = (Mnn(M20) + Mnn(M21) * t1) / rad;
			llr[2] = (Mnn(M30) + Mnn(M31) * t1) * 0.999999949827;
			llr[0] = llr[0] + M1n[0] + M1n[1] * t1 + M1n[2] * t2 + M1n[3] * t3
					+ M1n[4] * t4;
			llr[0] = rad2mrad(llr[0]); // 地心Date黄道原点坐标(不含岁差)
			addPrece(jd, llr); // 补岁差
			return llr;
		}

		// 传回月球的地心视黄经及视黄纬
		public double[] moonCal2(double jd) {
			double[] moon = moonCal(jd);
			ZD d = nutation(jd);
			moon[0] = rad2mrad(moon[0] + d.Lon); // 补章动
			return moon;
		}

		// 传回月球的地心视赤经及视赤纬
		public double[] moonCal3(double jd) {
			double[] moon = moonCal(jd);
			HCconv(moon, hcjj1(jd));
			nutationRaDec(jd, moon); // 补赤经及赤纬章动
			// 如果黄赤转换前补了黄经章动及交章动,就不能再补赤经赤纬章动
			return moon;
		}

		// ==================地心坐标中的日月位置计算===================
		public double jiaoCai(int lx, double t, double jiao) {
			// lx=1时计算t时刻日月角距与jiao的差, lx=0计算t时刻太阳黄经与jiao的差
			double[] sun = earCal(t); // 计算太阳真位置(先算出日心坐标中地球的位置)
			sun[0] += Math.PI;
			sun[1] = -sun[1]; // 转为地心坐标
			addGxc(t, sun); // 补周年光行差
			if (lx == 0) {
				ZD d = nutation(t);
				sun[0] += d.Lon; // 补黄经章动
				return rad2mrad(jiao - sun[0]);
			}
			double[] moon = moonCal(t); // 日月角差与章动无关
			return rad2mrad(jiao - (moon[0] - sun[0]));
		}

		// ==================已知位置反求时间===================
		public double jiaoCal(double t1, double jiao, int lx) {
			// t1是J2000起算儒略日数
			// 已知角度(jiao)求时间(t)
			// lx=0是太阳黄经达某角度的时刻计算(用于节气计算)
			// lx=1是日月角距达某角度的时刻计算(用于定朔望等)
			// 传入的t1是指定角度对应真时刻t的前一些天
			// 对于节气计算,应满足t在t1到t1+360天之间,对于Y年第n个节气(n=0是春分),t1可取值Y*365.2422+n*15.2
			// 对于朔望计算,应满足t在t1到t1+25天之间,在此范围之外,求右边的根
			double t2 = t1, t = 0, v;
			if (lx == 0) {
				t2 += 360; // 在t1到t2范围内求解(范气360天范围),结果置于t
			} else {
				t2 += 25;
			}
			jiao *= Math.PI / 180; // 待搜索目标角
			// 利用截弦法计算
			double v1 = jiaoCai(lx, t1, jiao); // v1,v2为t1,t2时对应的黄经
			double v2 = jiaoCai(lx, t2, jiao);
			if (v1 < v2) {
				v2 -= 2 * Math.PI; // 减2pi作用是将周期性角度转为连续角度
			}
			double k = 1, k2; // k是截弦的斜率
			for (int i = 0; i < 10; i++) { // 快速截弦求根,通常截弦三四次就已达所需精度
				k2 = (v2 - v1) / (t2 - t1); // 算出斜率
				if (Math.abs(k2) > 1e-15) {
					k = k2; // 差商可能为零,应排除
				}
				t = t1 - v1 / k;
				v = jiaoCai(lx, t, jiao);// 直线逼近法求根(直线方程的根)
				if (v > 1) {
					v -= 2 * Math.PI; // 一次逼近后,v1就已接近0,如果很大,则应减1周
				}
				if (Math.abs(v) < 1e-8) {
					break; // 已达精度
				}

				t1 = t2;
				v1 = v2;
				t2 = t;
				v2 = v; // 下一次截弦
			}
			return t;
		}

		// ==================节气计算===================
		public static final String jqB[] = { // 节气表
		"春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露",
				"秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒", "立春", "雨水",
				"惊蛰" };

		public void JQtest(int y) { // 节气使计算范例,y是年分,这是个测试函数
			double jd = 365.2422 * (y - 2000), q;
			String s1, s2;
			for (int i = 0; i < 24; i++) {
				q = jiaoCal(jd + i * 15.2, i * 15, 0);
				q = q + J2000 + (double) 8 / 24; // 计算第i个节气(i=0是春风),结果转为北京时
				setFromJD(q, true);
				s1 = toStr(); // 将儒略日转成世界时
				setFromJD(q, false);
				s2 = toStr(); // 将儒略日转成日期格式(输出日期形式的力学时)
				System.out.println(jqB[i] + " : " + s1 + " " + s2); // 显示
			}
		}

		// =================定朔弦望计算========================
		public void dingSuo(int y, double arc) { // 这是个测试函数
			double jd = 365.2422 * (y - 2000), q;
			String s1, s2;
			System.out.println("月份:世界时  原子时");
			for (int i = 0; i < 12; i++) {
				q = jiaoCal(jd + 29.5 * i, arc, 1) + J2000 + 8 / 24; // 计算第i个节气(i=0是春风),结果转为北京时
				setFromJD(q, true);
				s1 = toStr(); // 将儒略日转成世界时
				setFromJD(q, false);
				s2 = toStr(); // 将儒略日转成日期格式(输出日期形式的力学时)
				System.out.println((i + 1) + "月 : " + s1 + " " + s2); // 显示
			}
		}

		// =================农历计算========================
		/*****
		 * 1.冬至所在的UTC日期保存在A[0],根据"规定1"得知在A[0]之前(含A[0])的那个UTC朔日定为年首日期
		 * 冬至之后的中气分保存在A[1],A[2],A[3]...A[13],其中A[12]又回到了冬至,共计算13次中气
		 * 2.连续计算冬至后14个朔日,即起算时间时A[0]+1 14个朔日编号为0,1...12,保存在C[0],C[1]...C[13]
		 * 这14个朔日表示编号为0月,1月,...12月0月的各月终止日期,但要注意实际终止日是新月初一,不属本月
		 * 这14个朔日同样表示编号为1月,2月...的开始日期
		 * 设某月编号为n,那么开始日期为C[n-1],结束日期为C[n],如果每月都含中气,该月所含的中气为A[n]
		 * 注:为了全总计算出13个月的大小月情况,须算出14个朔日。 3.闰年判断:含有13个月的年份是闰年 当第13月(月编号12月)终止日期大于冬至日,
		 * 即C[12]〉A[12], 那么该月是新年,本年没月12月,本年共12个月
		 * 当第13月(月编号12月)终止日期小等于冬至日,即C[12]≤A[12],那么该月是本年的有效月份,本年共13个月 4.闰年中处理闰月:
		 * 13个月中至少1个月份无中气,首个无中气的月置闰,在n=1...12月中找到闰月,即C[n]≤A[n]
		 * 从农历年首的定义知道,0月一定含有中气冬至,所以不可能是闰月。 首月有时很贪心,除冬至外还可能再吃掉本年或前年的另一个中气
		 * 定出闰月后,该月及以后的月编号减1 5.以上所述的月编号不是日常生活中说的"正月","二月"等月名称:
		 * 如果"建子",0月为首月,如果"建寅",2月的月名"正月",3月是"二月",其余类推
		 *****/
		private static final String yueMing[] = { "正", "二", "三", "四", "五", "六",
				"七", "八", "九", "十", "十一", "十二" };

		public String paiYue(int y) { // 农历排月序计算,可定出农历
			double zq[] = new double[20];
			double jq[] = new double[20];
			double hs[] = new double[20];

			// var zq=new Array(),jq=new Array(), hs=new Array(); //中气表,节气表,日月合朔表

			// 从冬至开始,连续计算14个中气时刻
			int i;
			double t1 = 365.2422 * (y - 2000) - 50; // 农历年首始于前一年的冬至,为了节气中气一起算,取前年大雪之前
			for (i = 0; i < 14; i++) { // 计算节气(从冬至开始),注意:返回的是力学时
				zq[i] = jiaoCal(t1 + i * 30.4, i * 30 - 90, 0); // 中气计算,冬至的太阳黄经是270度(或-90度)
				jq[i] = jiaoCal(t1 + i * 30.4, i * 30 - 105, 0); // 顺便计算节气,它不是农历定朔计算所必需的
			}
			// 在冬至过后,连续计算14个日月合朔时刻
			double dongZhiJia1 = zq[0] + 1 - Dint_dec(zq[0], 8, false); // 冬至过后的第一天0点的儒略日数
			hs[0] = jiaoCal(dongZhiJia1, 0, 1); // 首月结束的日月合朔时刻
			for (i = 1; i < 14; i++)
				hs[i] = jiaoCal(hs[i - 1] + 25, 0, 1);
			// 算出中气及合朔时刻的日数(不含小数的日数计数,以便计算日期之间的差值)
			double A[] = new double[20];
			double B[] = new double[20];
			double C[] = new double[20];
			// var A=new Array(), B=new Array(), C=new Array();
			for (i = 0; i < 14; i++) { // 取当地UTC日数的整数部分
				A[i] = Dint_dec(zq[i], 8, true);
				B[i] = Dint_dec(jq[i], 8, true);
				C[i] = Dint_dec(hs[i], 8, true);
			}
			// 闰月及大小月分析
			int tot = 12, nun = -1;
			int yn[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 0 }; // 月编号
			if (C[12] <= A[12]) { // 闰月分析
				yn[12] = 12;
				tot = 13; // 编号为12的月是本年的有效月份,本年总月数13个
				for (i = 1; i < 13; i++)
					if (C[i] <= A[i])
						break;
				for (nun = i - 1; i < 13; i++)
					yn[i - 1]--; // 注意yn中不含农历首月(所以取i-1),在公历中农历首月总是去年的所以不多做计算
			}
			String syn[] = new String[20];
			for (i = 0; i < tot; i++) { // 转为建寅月名,并做大小月分析
				syn[i] = yueMing[(yn[i] + 10) % 12]; // 转建寅月名
				if (i == nun) {
					syn[i] += "月闰";
				} else {
					syn[i] += "月"; // 标记是否闰月
				}
				if (C[i + 1] - C[i] > 29) {
					syn[i] += "大";
				} else {
					syn[i] += "小"; // 标记大小月
				}
			}
			// 显示
			String out = "";
			for (i = 0; i < tot; i++) {
				int zm = (i * 2 + 18) % 24;
				int jm = (i * 2 + 17) % 24; // 中气名节气名
				setFromJD(jq[i] + J2000 + (double) 8 / 24, true);
				out += jqB[jm] + ":" + toStr() + "\t"; // 显示节气
				setFromJD(zq[i] + J2000 + (double) 8 / 24, true);
				out += jqB[zm] + ":" + toStr() + "\t"; // 显示中气
				setFromJD(hs[i] + J2000 + (double) 8 / 24, true);
				out += syn[i] + ":" + toStr() + "\r\n"; // 显示日月合朔
			}
			return out;
		}
	}
	
	/**
	 * 判断一个月是不是闰月
	 * @param year 年份(国历)
	 * @param cnLunarMonth 月份(农历)
	 * @return
	 */
	public static boolean isLeapMonth(int year, String cnLunarMonth) {
		boolean leap = false;
		SolarTerm st = new SolarTerm();
		String s = st.paiYue(year);
		String[] arr = s.split("\r\n");
		for(int i=0, len=arr.length; i<len; i++) {
			String str = arr[i].split("\t")[2].split(":")[0];
			if(cnLunarMonth.contains("冬")) {
				cnLunarMonth = "十一月";
			} else if(cnLunarMonth.contains("腊")) {
				cnLunarMonth = "十二月";
			}
			cnLunarMonth.replace("月", "");
			if(str.contains(cnLunarMonth)) {
				if(str.contains("闰")) {
					leap = true;
				}
				break;
			}
		}
		return leap;
	}
	
	/**
	 * 判断一个月的大小月
	 * @param year 年份(国历)
	 * @param cnLunarMonth 月份(农历)
	 * @return
	 */
	public static boolean isBigMonth(int year, String cnLunarMonth) {
		boolean bigMonth = false;
		SolarTerm st = new SolarTerm();
		String s = st.paiYue(year);
		String[] arr = s.split("\r\n");
		for(int i=0, len=arr.length; i<len; i++) {
			String str = arr[i].split("\t")[2].split(":")[0];
			if(cnLunarMonth.contains("冬")) {
				cnLunarMonth = "十一月";
			} else if(cnLunarMonth.contains("腊")) {
				cnLunarMonth = "十二月";
			}
			cnLunarMonth.replace("月", "");
			if(str.contains(cnLunarMonth)) {
				if(str.contains("大")) {
					bigMonth = true;
				}
				break;
			}
		}
		return bigMonth;
	}
	
	//-------------------------------------- 生肖 -------------------------------------------
	private static String[] ANIMALS = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
	public static String getAnimal(int year) {
        if (year < 1900) {
            return "未知";
        }
        int start = 1900;
        return ANIMALS[(year - start) % ANIMALS.length];
    }
	
	//---------------------------------------------农历国历互转----------------------------------------------
	//农历
		public static class  Lunar {  
	        public boolean isleap;  
	        public int lunarDay;  
	        public int lunarMonth;  
	        public int lunarYear;  
	    }  
	  
		//公历
		public static class Solar {  
	        public int solarDay;  
	        public int solarMonth;  
	        public int solarYear;  
	    }  
	    /* 
	     * |----4位闰月|-------------13位1为30天，0为29天| 
	     */  
	  
	    public static int[] lunar_month_days = { 1887, 0x1694, 0x16aa, 0x4ad5,  
	            0xab6, 0xc4b7, 0x4ae, 0xa56, 0xb52a, 0x1d2a, 0xd54, 0x75aa, 0x156a,  
	            0x1096d, 0x95c, 0x14ae, 0xaa4d, 0x1a4c, 0x1b2a, 0x8d55, 0xad4,  
	            0x135a, 0x495d, 0x95c, 0xd49b, 0x149a, 0x1a4a, 0xbaa5, 0x16a8,  
	            0x1ad4, 0x52da, 0x12b6, 0xe937, 0x92e, 0x1496, 0xb64b, 0xd4a,  
	            0xda8, 0x95b5, 0x56c, 0x12ae, 0x492f, 0x92e, 0xcc96, 0x1a94,  
	            0x1d4a, 0xada9, 0xb5a, 0x56c, 0x726e, 0x125c, 0xf92d, 0x192a,  
	            0x1a94, 0xdb4a, 0x16aa, 0xad4, 0x955b, 0x4ba, 0x125a, 0x592b,  
	            0x152a, 0xf695, 0xd94, 0x16aa, 0xaab5, 0x9b4, 0x14b6, 0x6a57,  
	            0xa56, 0x1152a, 0x1d2a, 0xd54, 0xd5aa, 0x156a, 0x96c, 0x94ae,  
	            0x14ae, 0xa4c, 0x7d26, 0x1b2a, 0xeb55, 0xad4, 0x12da, 0xa95d,  
	            0x95a, 0x149a, 0x9a4d, 0x1a4a, 0x11aa5, 0x16a8, 0x16d4, 0xd2da,  
	            0x12b6, 0x936, 0x9497, 0x1496, 0x1564b, 0xd4a, 0xda8, 0xd5b4,  
	            0x156c, 0x12ae, 0xa92f, 0x92e, 0xc96, 0x6d4a, 0x1d4a, 0x10d65,  
	            0xb58, 0x156c, 0xb26d, 0x125c, 0x192c, 0x9a95, 0x1a94, 0x1b4a,  
	            0x4b55, 0xad4, 0xf55b, 0x4ba, 0x125a, 0xb92b, 0x152a, 0x1694,  
	            0x96aa, 0x15aa, 0x12ab5, 0x974, 0x14b6, 0xca57, 0xa56, 0x1526,  
	            0x8e95, 0xd54, 0x15aa, 0x49b5, 0x96c, 0xd4ae, 0x149c, 0x1a4c,  
	            0xbd26, 0x1aa6, 0xb54, 0x6d6a, 0x12da, 0x1695d, 0x95a, 0x149a,  
	            0xda4b, 0x1a4a, 0x1aa4, 0xbb54, 0x16b4, 0xada, 0x495b, 0x936,  
	            0xf497, 0x1496, 0x154a, 0xb6a5, 0xda4, 0x15b4, 0x6ab6, 0x126e,  
	            0x1092f, 0x92e, 0xc96, 0xcd4a, 0x1d4a, 0xd64, 0x956c, 0x155c,  
	            0x125c, 0x792e, 0x192c, 0xfa95, 0x1a94, 0x1b4a, 0xab55, 0xad4,  
	            0x14da, 0x8a5d, 0xa5a, 0x1152b, 0x152a, 0x1694, 0xd6aa, 0x15aa,  
	            0xab4, 0x94ba, 0x14b6, 0xa56, 0x7527, 0xd26, 0xee53, 0xd54, 0x15aa,  
	            0xa9b5, 0x96c, 0x14ae, 0x8a4e, 0x1a4c, 0x11d26, 0x1aa4, 0x1b54,  
	            0xcd6a, 0xada, 0x95c, 0x949d, 0x149a, 0x1a2a, 0x5b25, 0x1aa4,  
	            0xfb52, 0x16b4, 0xaba, 0xa95b, 0x936, 0x1496, 0x9a4b, 0x154a,  
	            0x136a5, 0xda4, 0x15ac };  
	  
	    public static int[] solar_1_1 = { 1887, 0xec04c, 0xec23f, 0xec435, 0xec649,  
	            0xec83e, 0xeca51, 0xecc46, 0xece3a, 0xed04d, 0xed242, 0xed436,  
	            0xed64a, 0xed83f, 0xeda53, 0xedc48, 0xede3d, 0xee050, 0xee244,  
	            0xee439, 0xee64d, 0xee842, 0xeea36, 0xeec4a, 0xeee3e, 0xef052,  
	            0xef246, 0xef43a, 0xef64e, 0xef843, 0xefa37, 0xefc4b, 0xefe41,  
	            0xf0054, 0xf0248, 0xf043c, 0xf0650, 0xf0845, 0xf0a38, 0xf0c4d,  
	            0xf0e42, 0xf1037, 0xf124a, 0xf143e, 0xf1651, 0xf1846, 0xf1a3a,  
	            0xf1c4e, 0xf1e44, 0xf2038, 0xf224b, 0xf243f, 0xf2653, 0xf2848,  
	            0xf2a3b, 0xf2c4f, 0xf2e45, 0xf3039, 0xf324d, 0xf3442, 0xf3636,  
	            0xf384a, 0xf3a3d, 0xf3c51, 0xf3e46, 0xf403b, 0xf424e, 0xf4443,  
	            0xf4638, 0xf484c, 0xf4a3f, 0xf4c52, 0xf4e48, 0xf503c, 0xf524f,  
	            0xf5445, 0xf5639, 0xf584d, 0xf5a42, 0xf5c35, 0xf5e49, 0xf603e,  
	            0xf6251, 0xf6446, 0xf663b, 0xf684f, 0xf6a43, 0xf6c37, 0xf6e4b,  
	            0xf703f, 0xf7252, 0xf7447, 0xf763c, 0xf7850, 0xf7a45, 0xf7c39,  
	            0xf7e4d, 0xf8042, 0xf8254, 0xf8449, 0xf863d, 0xf8851, 0xf8a46,  
	            0xf8c3b, 0xf8e4f, 0xf9044, 0xf9237, 0xf944a, 0xf963f, 0xf9853,  
	            0xf9a47, 0xf9c3c, 0xf9e50, 0xfa045, 0xfa238, 0xfa44c, 0xfa641,  
	            0xfa836, 0xfaa49, 0xfac3d, 0xfae52, 0xfb047, 0xfb23a, 0xfb44e,  
	            0xfb643, 0xfb837, 0xfba4a, 0xfbc3f, 0xfbe53, 0xfc048, 0xfc23c,  
	            0xfc450, 0xfc645, 0xfc839, 0xfca4c, 0xfcc41, 0xfce36, 0xfd04a,  
	            0xfd23d, 0xfd451, 0xfd646, 0xfd83a, 0xfda4d, 0xfdc43, 0xfde37,  
	            0xfe04b, 0xfe23f, 0xfe453, 0xfe648, 0xfe83c, 0xfea4f, 0xfec44,  
	            0xfee38, 0xff04c, 0xff241, 0xff436, 0xff64a, 0xff83e, 0xffa51,  
	            0xffc46, 0xffe3a, 0x10004e, 0x100242, 0x100437, 0x10064b, 0x100841,  
	            0x100a53, 0x100c48, 0x100e3c, 0x10104f, 0x101244, 0x101438,  
	            0x10164c, 0x101842, 0x101a35, 0x101c49, 0x101e3d, 0x102051,  
	            0x102245, 0x10243a, 0x10264e, 0x102843, 0x102a37, 0x102c4b,  
	            0x102e3f, 0x103053, 0x103247, 0x10343b, 0x10364f, 0x103845,  
	            0x103a38, 0x103c4c, 0x103e42, 0x104036, 0x104249, 0x10443d,  
	            0x104651, 0x104846, 0x104a3a, 0x104c4e, 0x104e43, 0x105038,  
	            0x10524a, 0x10543e, 0x105652, 0x105847, 0x105a3b, 0x105c4f,  
	            0x105e45, 0x106039, 0x10624c, 0x106441, 0x106635, 0x106849,  
	            0x106a3d, 0x106c51, 0x106e47, 0x10703c, 0x10724f, 0x107444,  
	            0x107638, 0x10784c, 0x107a3f, 0x107c53, 0x107e48 };  
	  
	    private static int getBitInt(int data, int length, int shift) {  
	        return (data & (((1 << length) - 1) << shift)) >> shift;  
	    }  
	  
	    // WARNING: Dates before Oct. 1582 are inaccurate  
	    private static long solarToInt(int y, int m, int d) {  
	        m = (m + 9) % 12;  
	        y = y - m / 10;  
	        return 365 * y + y / 4 - y / 100 + y / 400 + (m * 306 + 5) / 10  
	                + (d - 1);  
	    }  
	      
	    private static Solar solarFromInt(long g) {  
	        long y = (10000 * g + 14780) / 3652425;  
	        long ddd = g - (365 * y + y / 4 - y / 100 + y / 400);  
	        if (ddd < 0) {  
	            y--;  
	            ddd = g - (365 * y + y / 4 - y / 100 + y / 400);  
	        }  
	        long mi = (100 * ddd + 52) / 3060;  
	        long mm = (mi + 2) % 12 + 1;  
	        y = y + (mi + 2) / 12;  
	        long dd = ddd - (mi * 306 + 5) / 10 + 1;  
	        Solar solar = new Solar();  
	        solar.solarYear = (int) y;  
	        solar.solarMonth = (int) mm;  
	        solar.solarDay = (int) dd;  
	        return solar;  
	    }  
	  
	    public static Solar lunarToSolar(Lunar lunar) {  
	        int days = lunar_month_days[lunar.lunarYear - lunar_month_days[0]];  
	        int leap = getBitInt(days, 4, 13);  
	        int offset = 0;  
	        int loopend = leap;  
	        if (!lunar.isleap) {  
	            if (lunar.lunarMonth <= leap || leap == 0) {  
	                loopend = lunar.lunarMonth - 1;  
	            } else {  
	                loopend = lunar.lunarMonth;  
	            }  
	        }  
	        for (int i = 0; i < loopend; i++) {  
	            offset += getBitInt(days, 1, 12 - i) == 1 ? 30 : 29;  
	        }  
	        offset += lunar.lunarDay;  
	  
	        int solar11 = solar_1_1[lunar.lunarYear - solar_1_1[0]];  
	  
	        int y = getBitInt(solar11, 12, 9);  
	        int m = getBitInt(solar11, 4, 5);  
	        int d = getBitInt(solar11, 5, 0);  
	  
	        return solarFromInt(solarToInt(y, m, d) + offset - 1);  
	    }  
	  
	    public static Lunar solarToLunar(Solar solar) {  
	        Lunar lunar = new Lunar();  
	        int index = solar.solarYear - solar_1_1[0];  
	        int data = (solar.solarYear << 9) | (solar.solarMonth << 5)  
	                | (solar.solarDay);  
	        int solar11 = 0;  
	        if (solar_1_1[index] > data) {  
	            index--;  
	        }  
	        solar11 = solar_1_1[index];  
	        int y = getBitInt(solar11, 12, 9);  
	        int m = getBitInt(solar11, 4, 5);  
	        int d = getBitInt(solar11, 5, 0);  
	        long offset = solarToInt(solar.solarYear, solar.solarMonth,  
	                solar.solarDay) - solarToInt(y, m, d);  
	  
	        int days = lunar_month_days[index];  
	        int leap = getBitInt(days, 4, 13);  
	  
	        int lunarY = index + solar_1_1[0];  
	        int lunarM = 1;  
	        int lunarD = 1;  
	        offset += 1;  
	  
	        for (int i = 0; i < 13; i++) {  
	            int dm = getBitInt(days, 1, 12 - i) == 1 ? 30 : 29;  
	            if (offset > dm) {  
	                lunarM++;  
	                offset -= dm;  
	            } else {  
	                break;  
	            }  
	        }  
	        lunarD = (int) (offset);  
	        lunar.lunarYear = lunarY;  
	        lunar.lunarMonth = lunarM;  
	        lunar.isleap = false;  
	        if (leap != 0 && lunarM > leap) {  
	            lunar.lunarMonth = lunarM - 1;  
	            if (lunarM == leap + 1) {  
	                lunar.isleap = true;  
	            }  
	        }  
	        lunar.lunarDay = lunarD;  
	        return lunar;  
	    }  
	    
	    /**
	     * 1989年正月初一
	     */
	    private static Map<String, Integer> MONTH_MAP = new HashMap<String, Integer>(){
			private static final long serialVersionUID = 1L;
			{
	    		put("正", 1);
	    		put("二", 2);
	    		put("三", 3);
	    		put("四", 4);
	    		put("五", 5);
	    		put("六", 6);
	    		put("七", 7);
	    		put("八", 8);
	    		put("九", 9);
	    		put("十", 10);
	    		put("冬", 11);
	    		put("腊", 12);
	    	}
	    };
	    private static Map<String, Integer> DAY_MAP = new HashMap<String, Integer>(){
			private static final long serialVersionUID = 1L;
			{
	    		put("初一", 1);
	    		put("初二", 2);
	    		put("初三", 3);
	    		put("初四", 4);
	    		put("初五", 5);
	    		put("初六", 6);
	    		put("初七", 7);
	    		put("初八", 8);
	    		put("初九", 9);
	    		put("初十", 10);
	    		put("十一", 11);
	    		put("十二", 12);
	    		put("十三", 13);
	    		put("十四", 14);
	    		put("十五", 15);
	    		put("十六", 16);
	    		put("十七", 17);
	    		put("十八", 18);
	    		put("十九", 19);
	    		put("廿十", 20);
	    		put("廿一", 21);
	    		put("廿二", 22);
	    		put("廿三", 23);
	    		put("廿四", 24);
	    		put("廿五", 25);
	    		put("廿六", 26);
	    		put("廿七", 27);
	    		put("廿八", 28);
	    		put("廿九", 29);
	    		put("三十", 30);
	    	}
	    };
	    
	    public static Date parseLunar(String lunarStr) {
	    	try {
	    		boolean isleap = false;
	        	int year = 0;
	        	int month = 0;
	        	int day = 0;
	        	StringBuilder buf = new StringBuilder();
	        	char[] chs = lunarStr.toCharArray();
	        	for(int i=0,len=chs.length; i<len; i++) {
	        		char ch = chs[i];
	        		if(ch == '年') {
	        			year = Integer.parseInt(buf.toString());
	        			buf.setLength(0);
	        		} else if(ch == '闰') {
	        			isleap = true;
	        			buf.setLength(0);
	        		} else if(ch == '月') {
	        			month = MONTH_MAP.get(buf.toString());
	        			buf.setLength(0);
	        		} else {
	        			buf.append(ch);
	        			if(i==len-1) {
	        				day = DAY_MAP.get(buf.toString());
	        			}
	        		}
	        	}
	        	Lunar lunar = new Lunar();
	        	lunar.isleap = isleap;
	        	lunar.lunarYear = year;
	        	lunar.lunarMonth = month;
	        	lunar.lunarDay = day;
	        	Solar solar = lunarToSolar(lunar);
	        	return DateFormatUtil.parse(String.format("%d-%d-%d", solar.solarYear, solar.solarMonth, solar.solarDay), DateFormatUtil.Pattern.YYYY_MM_DD);
	    	} catch (Exception e) {
	    		int year = Integer.parseInt(lunarStr.split("年")[0]);
	    		return DateFormatUtil.parse(String.format("%d-%d-%d", year, 1, 1), DateFormatUtil.Pattern.YYYY_MM_DD);
	    	}
	    }
	
	// -------------------------------------------- 佛历 ----------------------------------------------------------
	private static final List<String> SIX_DATE_OF_SMALLMONTH = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			this.add("初八");
			this.add("十四");
			this.add("十五");
			this.add("二十三");
			this.add("二十八");
			this.add("二十九");
		}
	};
	
	private static final List<String> SIX_DATE_OF_BIGMONTH = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			this.add("初八");
			this.add("十四");
			this.add("十五");
			this.add("二十三");
			this.add("二十九");
			this.add("三十");
		}
	};
	
	private static final List<String> TEN_DATE = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			this.add("初一");
			this.add("初八");
			this.add("十四");
			this.add("十五");
			this.add("十八");
			this.add("二十三");
			this.add("二十四");
			this.add("二十八");
			this.add("二十九");
			this.add("三十");
		}
	};

	private static final Map<String, String> BUDDHA_BIRTH_DATE = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			this.put("正月初一", "弥勒菩萨圣诞");
			this.put("正月初六", "定光佛圣诞 华严宗五祖圭峰宗密大师圆寂日");
			this.put("正月初九", "帝释天尊圣诞");
			this.put("正月十一", "真谛三藏法师圆寂日");
			this.put("正月十二", "净宗七祖省常法师圆寂日");
			this.put("正月十七", "百丈怀海禅师圆寂日");
			this.put("正月廿一", "净宗九祖藕益法师圆寂日");

			this.put("二月初一", "马祖道一禅师圆寂日");
			this.put("二月初二", "太虚大师圆寂日");
			this.put("二月初五", "玄奘法师圆寂日 天台九祖荆溪湛然尊者圆寂");
			this.put("二月初八", "释迦牟尼佛出家 道安法师圆寂日");
			this.put("二月初九", "禅宗六祖慧能大师圣诞");
			this.put("二月十五", "释迦牟尼佛涅磐");
			this.put("二月十九", "观音菩萨圣诞");
			this.put("二月廿一", "普贤菩萨圣诞");
			this.put("二月廿六", "净宗六祖永明法师圆寂日");

			this.put("三月初三", "布袋和尚坐化日");
			this.put("三月十二", "本焕长老圆寂纪念日");
			this.put("三月十六", "准提菩萨圣诞 二祖慧可大师圆寂日");

			this.put("四月初四", "文殊菩萨圣诞 慈航菩萨涅槃日");
			this.put("四月初八", "释迦牟尼佛圣诞 道宣律师诞辰日");
			this.put("四月十四", "净宗十一祖省庵法师圆寂日");
			this.put("四月十五", "佛吉祥日");
			this.put("四月廿八", "药王菩萨圣诞");

			this.put("五月初六", "鉴真法师圆寂日");
			this.put("五月初八", "善慧菩萨圣诞日");
			this.put("五月初十", "宣化上人圆寂纪念日");
			this.put("五月十三", "伽蓝菩萨圣诞 禅宗七祖神会禅师圆寂日");

			this.put("六月初三", "韦驮菩萨圣诞");
			this.put("六月初十", "金栗如来圣诞日");
			this.put("六月十四", "明旸大和尚圆寂日");
			this.put("六月十五", "不空三藏圆寂日");
			this.put("六月十九", "观世音菩萨成道日");
			this.put("六月廿一", "唐代法相宗三祖智周大师圆寂日");
			this.put("六月廿二", "天台宗二祖慧思尊者圆寂日");

			this.put("七月初二", "净宗八祖莲池法师圆寂日");
			this.put("七月初九", "净宗十祖截流法师圆寂日");
			this.put("七月十三", "大势至菩萨圣诞日");
			this.put("七月十五", "佛欢喜日");
			this.put("七月十九", "净宗三祖承远法师圆寂日");
			this.put("七月廿一", "普庵祖师圣诞日");
			this.put("七月廿二", "唐代高僧圆测大师圆寂日 增福财神圣诞");
			this.put("七月廿四", "龙树菩萨圣诞日 隋唐高僧法琳大师圆寂日");
			this.put("七月廿九", "地藏王菩萨圣诞日 虚云和尚诞辰日");

			this.put("八月初三", "禅宗六祖慧能大师圆寂日");
			this.put("八月初六", "净宗初祖慧远法师圆寂日");
			this.put("八月十二", "圆瑛大师圆寂纪念日");
			this.put("八月十五", "月光菩萨圣诞日");

			this.put("八月十六", "金刚智三藏纪念日");
			this.put("八月廿", "鸠摩罗什圆寂日");
			this.put("八月廿二", "燃灯古佛圣诞日");


			this.put("九月初四", "弘一法师圆寂日 禅宗四祖道信大师圆寂日");
			this.put("九月初九", "摩利支天菩萨圣诞");
			this.put("九月十二", "虚云和尚往生日");
			this.put("九月十九", "观世音菩萨出家日");
			this.put("九月廿", "弘一法师诞辰");
			this.put("九月廿九", "药师佛圣诞日");

			this.put("十月初三", "道宣律师往生 净宗五祖少康法师圆寂日");
			this.put("十月初五", "阿弥陀佛圣诞");
			this.put("十月初五", "阿弥陀佛圣诞");
			this.put("十月初七", "善无畏三藏纪念日");
			this.put("十月十一", "憨山德清大师圆寂日");
			this.put("十月十二", "实叉难陀三藏圆寂日");
			this.put("十月十五", "禅宗三祖僧璨大师圆寂日");
			this.put("十月十八", "阿底峡尊者圆寂日");
			this.put("十月廿", "文殊菩萨出家日");
			this.put("十月廿三", "禅宗五祖弘忍大师圆寂日");
			this.put("十月廿五", "悟道大和尚圆寂日");

			this.put("十一月初四", "净宗十三祖印光法师圆寂日 孔子圣诞日");
			this.put("十一月十一", "悟道大和尚诞辰日");
			this.put("十一月十三", "唐代高僧慈恩大师（窥基法师）圆寂纪念日");
			this.put("十一月十七", "阿弥陀佛圣诞 净宗二祖善导大师圆寂日");
			this.put("十一月十九", "日光菩萨圣诞日");
			this.put("十一月廿四", "天台祖师智者大师圆寂日");

			this.put("十二月初一", "净宗四祖法照法师圆寂日");
			this.put("十二月初八", "释迦牟尼佛成道 佛图澄圆寂日");
			this.put("十二月十七", "净宗十二祖彻悟法师圆寂日");
			this.put("十二月十八", "太虚大师诞辰日");
			this.put("十二月廿二", "文殊菩萨成道日");
			this.put("十二月廿三", "监斋菩萨圣诞日");
			this.put("十二月廿六", "永明延寿大师圆寂日 施护三藏圆寂日");
			this.put("十二月廿九", "华严菩萨圣诞日");
		}
	};
	
	/**
	 * 是否六斋日
	 * @param cnday 农历中文大写日期
	 * @param bigMonth 是否农历大月
	 * @return
	 */
	public static boolean isSixDate(String cnday, boolean bigMonth) {
		if(bigMonth) {
			return SIX_DATE_OF_BIGMONTH.contains(cnday);
		}
		return SIX_DATE_OF_SMALLMONTH.contains(cnday);
	}
	
	/**
	 *  是否十斋日
	 * @param cnday 农历中文大写日期
	 * @return
	 */
	public static boolean isTenDate(String cnday) {
		return TEN_DATE.contains(cnday);
	}
	
	/**
	 * 佛诞日
	 * @param cnmonth 农历中文大写月份
	 * @param cnday 农历中文大写日期
	 * @return
	 */
	public static String buddhaBirthDate(String cnmonth, String cnday) {
		if(cnmonth.contains("冬")) {
			cnmonth = cnmonth.replace("冬", "十一");
		} else if(cnmonth.contains("腊")) {
			cnmonth = cnmonth.replace("腊", "十二");
		}
		return BUDDHA_BIRTH_DATE.get(cnmonth+"月"+cnday);
	}
	 
	private SolarLunarUtil(){}
}
