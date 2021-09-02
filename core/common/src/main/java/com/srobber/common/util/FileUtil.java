package com.srobber.common.util;

import java.io.File;

/**
 * 文件操作工具类
 * @author chensenlai
 */
public class FileUtil {

    /**
     * 生成同名路径其他后缀文件名
     * @param srcPath 源文件
     * @param suffix 后缀名
     * @return 生成文件名
     */
    public static String genSuffixFilePath(String srcPath, String suffix) {
        String fileName = (new File(srcPath)).getName();
        int lastIndex = fileName.lastIndexOf(".");
        return lastIndex==-1 ? srcPath+"."+suffix : srcPath.substring(0, srcPath.length()-fileName.length()+lastIndex)+"."+suffix;
    }
}
