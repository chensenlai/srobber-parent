package com.srobber.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * APP安装包地址下载地址工具类
 * 区分android和iOS
 * iOS从跳转到苹果应用市场, android优先解析应用宝的下载地址, 不行在从cdn下载
 *
 * @author chensenlai
 */
@Slf4j
public class DownloadUrlUtil {

    /**
     * 根据userAgent判断当前设备应该使用的下载地址
     * 1 苹果使用苹果应用市场(itunesUrl)
     * 2 微信使用应用包下载地址
     * 3 其他的优先使用应用宝最新地址, 解析不出来再走cdn
     * @param userAgent 客户端agent
     * @param itunesUrl iTunes下载地址
     * @param apkName 	应用宝上apkName
     * @param yingYongBaoUrl 	应用宝下载地址
     * @param cdnUrl 		cdn下载地址
     * @return 下载地址
     */
    public static String getDownloadUrl(String userAgent, String itunesUrl,
                                        String apkName, String yingYongBaoUrl, String cdnUrl) {
        //苹果只能跳转到苹果应用市场
        if(fromApple(userAgent)) {
            return itunesUrl;
        }

        //微信只能从应用宝下载
        String downloadUrl = yingYongbaoDownloadUrl(apkName);
        if(fromWechat(userAgent)) {
            if(downloadUrl == null) {
                downloadUrl = yingYongBaoUrl;
            }
            return downloadUrl;
        }

        //其它的优先从解析的应用宝地址下载,如果解析出错,用cdn下载地址
        if(downloadUrl == null) {
            downloadUrl = cdnUrl;
        }
        return downloadUrl;
    }

    private static boolean fromApple(String userAgent) {
        if( userAgent != null
                && (userAgent.contains("iphone")
                || userAgent.contains("ipod")
                || userAgent.contains("ipad")) ){
            return true;
        }
        return false;
    }

    private static boolean fromWechat(String userAgent) {
        if(userAgent.contains("micromessenger")) {
            return true;
        }
        return false;
    }

    private static String yingYongbaoDownloadUrl(String apkName) {
        String url = "https://sj.qq.com/myapp/detail.htm?apkName="+apkName;
        try {
            HttpClient.HttpResult<String> hr = HttpClient.get(url);
            if(hr.getStatusCode() != 200) {
                log.warn("get myapp download url={} statusCode={}.", url, hr.getStatusCode());
                return null;
            }
            String content = hr.getContent();
            return parseDownloadUrl(content);
        } catch (Exception e) {
            log.error("get myapp download url={} error.", url, e);
            return null;
        }
    }

    /**
     * 从应用宝下载界面解析出下载地址
     * @param content 应用宝返回页面内容
     * @return 下载地址
     */
    private static String parseDownloadUrl(String content) {
        int len = content.length();
        int i = content.indexOf("downUrl");
        if(i>-1) {
            State state = State.begin;
            StringBuilder downloadUrlBuf = new StringBuilder(100);
            while(i<len) {
                char ch = content.charAt(i++);
                if( ch == '"') {
                    if(state == State.begin) {
                        state = State.accept;
                        continue;
                    }
                    if(state == State.accept) {
                        state = State.end;
                        break;
                    }

                }
                if(state == State.accept) {
                    downloadUrlBuf.append(ch);
                }
            }
            return downloadUrlBuf.toString();
        }
        return null;
    }

    /**
     * 解析阶段
     */
    private static enum State {
        /**
         * 开始
         */
        begin,
        /**
         * 接受
         */
        accept,
        /**
         * 结束
         */
        end
    }
}
