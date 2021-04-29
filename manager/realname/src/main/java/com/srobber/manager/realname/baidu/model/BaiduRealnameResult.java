package com.srobber.manager.realname.baidu.model;

import lombok.Data;

/**
 * 百度活体检测响应结果
 *
 * @author chensenlai
 * 2020-10-15 上午11:03
 */
@Data
public class BaiduRealnameResult {
    /**
     * 日志编号
     */
    private long log_id;
    /**
     * 错误码
     * 0 - 成功
     */
    private long error_code;
    /**
     * 错误消息
     */
    private String error_msg;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 结果
     */
    private Result result;

    @Data
    public static class Result {
        /**
         * 与公安小图相似度可能性，用于验证生活照与公安小图是否为同一人，
         * 有正常分数时为[0~1]，
         * 推荐阈值0.8，超过即判断为同一人
         */
        private double score;
    }
}
