package com.srobber.common.util;

import com.srobber.common.exeption.BusinessException;
import lombok.Getter;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 期格式化/解析工具
 * Pattern设计成enum原因,防止自定义格式传入,造成意想不到的错误.
 * 日期格式大小:
 * yyyyISO8601,week base year
 * YYYY年
 * MM  月
 * DD  在一年中第DD天
 * dd  在一月中第dd天
 * HH  24小时制
 * hh  12小时制
 * mm  分
 * ss  秒
 * SSS 毫秒
 *
 * @author chensenlai
 */
public class DateFormatUtil {

    public static final int ONE_MINUTES = 1;
    public static final int HOUR_MINUTES = 60;
    public static final int DAY_MINUTES = 24*60;
    public static final int WEEK_MINUTES = 7*24*60;


    /**
     * 日期格式化
     */
    public static String format(Date date, Pattern pattern) {
        DateTime dt = new DateTime(date);
        return dt.toString(pattern.pattern);
    }

    /**
     * 日期解析
     */
    public static Date parse(String dateStr, Pattern pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern.pattern);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw new BusinessException("date parse error:" +dateStr+","+pattern.pattern);
        }
    }

    /**
     * 日期人类可读格式化
     */
    public static String humanDateStr(Date date) {
        String formatStr = "";
        if(date == null) {
            return formatStr;
        }
        Date curDate = DateUtil.now();
        int minutes = DateUtil.minutesCompare(date, curDate);
        if(minutes < ONE_MINUTES) {
            formatStr = "刚刚";
        } else if(minutes < HOUR_MINUTES) {
            formatStr = minutes+"分钟前";
        } else if(minutes < DAY_MINUTES) {
            formatStr = minutes/HOUR_MINUTES+"小时前";
        } else if(minutes < WEEK_MINUTES) {
            formatStr = minutes/DAY_MINUTES+"天前";
        } else {
            if(format(date, Pattern.YYYY).equals(format(curDate, Pattern.YYYY))) {
                formatStr = format(date, Pattern.MM_DD_HH_MM);
            } else {
                formatStr = format(date, Pattern.YYYY_MM_DD_HH_MM);
            }
        }
        return formatStr;
    }

    /**
     * 生成毫秒内的时间串 （长度16）
     * @return
     */
    public static String dateMills() {
        DateTime d = DateTime.now();
        return d.toString("yyyyMMdd") + d.getMillisOfDay();
    }

    /**
     * 日期格式枚举值
     * yyyyISO8601,week base year
     * YYYY年
     * MM  月
     * DD  在一年中第DD天
     * dd  在一月中第dd天
     * HH  24小时制
     * hh  12小时制
     * mm  分
     * ss  秒
     * SSS 毫秒
     */
    @Getter
    public enum Pattern {

        YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYY_MM("yyyy-MM"),
        MM_DD_HH_MM("MM-dd HH:mm"),
        HH_MM("HH:mm"),

        CN_YYYY_MM_DD_HH_MM_SS_SSS("yyyy年MM月dd日HH时mm分ss秒SSS毫秒"),
        CN_YYYY_MM_DD_HH_MM_SS("yyyy年MM月dd日HH时mm分ss秒"),
        CN_YYYY_MM_DD_HH_MM("yyyy年MM月dd日HH时mm分"),
        CN_YYYY_MM_DD("yyyy年MM月dd日"),
        CN_YYYY_MM("yyyy年MM月"),
        CN_MM_DD_HH_MM("MM月dd日HH时mm秒"),
        CN_HH_MM("HH时mm秒"),

        YYYYMMDDHHMMSSSSS("yyyyMMddHHmmssSSS"),
        YYYYMMDDHHMMSS("yyyyMMddHHmmss"),
        YYYYMMDDHHMM("yyyyMMddHHmm"),
        YYYYMMDD("yyyyMMdd"),
        YYYYMM("yyyyMM"),
        YYYY("yyyy"),
        HHMM("HHmm")
        ;

        private String pattern;

        Pattern(String pattern) {
            this.pattern = pattern;
        }
    }
}
