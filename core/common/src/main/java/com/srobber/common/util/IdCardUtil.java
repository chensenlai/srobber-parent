package com.srobber.common.util;

import com.srobber.common.enums.GenderEnum;
import com.srobber.common.exeption.BusinessException;

/**
 * 身份证工具类
 *
 * @author chensenlai
 */
public class IdCardUtil {

    /**
     * 根据身份证提取性别
     * @param idCard 身份证号码
     * @return 性别
     */
    public static GenderEnum extractSex(String idCard) {
        if(!CheckUtil.checkIdCard(idCard)) {
            throw new BusinessException("身份证格式错误");
        }
        int idxSexStart = 0;
        if(idCard.length() == 18) {
            idxSexStart = 16;
        } else if(idCard.length() == 15) {
            idxSexStart = 14;
        }
        String idxSexStr = idCard.substring(idxSexStart, idxSexStart + 1);
        int idxSex = Integer.parseInt(idxSexStr) % 2;
        //1-男  0-女
        GenderEnum gender = GenderEnum.Unknown;
        if(idxSex == 0) {
            gender = GenderEnum.Female;
        } else if(idxSex == 1) {
            gender = GenderEnum.Male;
        }
        return gender;
    }

    /**
     * 根据身份证提取用户生日(yyyyMMdd)
     * @param idCard 身份证号码
     * @return 用户生日 yyyyMMdd
     */
    public static String extractBirthDate(String idCard) {
        if(!CheckUtil.checkIdCard(idCard)) {
            throw new BusinessException("身份证格式错误");
        }
        String birthDate = "";
        if(idCard.length() == 18) {
            birthDate = idCard.substring(6, 14);
        } else if(idCard.length() == 15) {
            birthDate = "19"+idCard.substring(6, 12);
        }
        return birthDate;
    }

    /**
     * 根据身份证提取用户性别
     * @param idCard 身份证号码
     * @return 年龄
     */
    public static int extractAge(String idCard) {
        String birthDate = extractBirthDate(idCard);
        return FormatUtil.getAge(birthDate, DateFormatUtil.Pattern.YYYYMMDD);
    }
}
