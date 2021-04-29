package com.srobber.common.util;

import java.util.Date;

/**
 * 格式化工具类
 *
 * @author chensenlai
 * 2020-09-25 下午12:41
 */
public class FormatUtil {

    /**
     * 根据出生日期计算年龄
     * @param birthDate 出生日期
     * @param pattern 出生日期格式
     * @return 年龄
     */
    public static int getAge(String birthDate, DateFormatUtil.Pattern pattern) {
        Date birth = DateFormatUtil.parse(birthDate, pattern);
        String birthDateStr = DateFormatUtil.format(birth, DateFormatUtil.Pattern.YYYYMMDD);

        int bYear = Integer.parseInt(birthDateStr.substring(0, 4));
        int bMonth = Integer.parseInt(birthDateStr.substring(4, 6));
        int bDay = Integer.parseInt(birthDateStr.substring(6, 8));

        String curDate = DateFormatUtil.format(DateUtil.now(), DateFormatUtil.Pattern.YYYYMMDD);
        int cYear = Integer.parseInt(curDate.substring(0, 4));
        int cMonth = Integer.parseInt(curDate.substring(4, 6));
        int cDay = Integer.parseInt(curDate.substring(6, 8));

        int age = cYear-bYear;
        if((cMonth<bMonth)
                || (cMonth==bMonth && cDay<bDay)) {
            age--;
        }
        if(age < 0) {
            age=0;
        }
        return age;
    }
}
