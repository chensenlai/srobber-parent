package com.srobber.manager.realname.baidu;

import com.baidu.aip.face.AipFace;
import com.srobber.common.util.JsonUtil;
import com.srobber.manager.realname.RealnameManager;
import com.srobber.manager.realname.RealnameResultEnum;
import com.srobber.manager.realname.baidu.model.BaiduRealnameResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 百度活体实名认证
 * (对接公安系统, 0.6元/次)
 *
 * @author chensenlai
 * 2020-10-15 上午10:54
 */
@Slf4j
@Setter
public class BaiduRealnameManager implements RealnameManager {

    private String appId;
    private String apiKey;
    private String secretKey;

    /**
     * 图片质量控制
     * NONE: 不进行控制
     * LOW:较低的质量要求
     * NORMAL: 一般的质量要求
     * HIGH: 较高的质量要求 默认 NONE
     */
    private String qualityControl = "NORMAL";
    /**
     * 活体检测控制
     * NONE: 不进行控制
     * LOW:较低的活体要求(高通过率 低攻击拒绝率)
     * NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)
     * HIGH: 较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
     */
    private String livenessControl = "NORMAL";

    /**
     * 与公安小图相似度可能性，用于验证生活照与公安小图是否为同一人，有正常分数时为[0~1]，
     * 推荐阈值0.8，超过即判断为同一人
     */
    private double score = 8.0;

    /**
     * 新建一个AipFace,初始化完成后建议单例使用,
     * 避免重复获取access_token
     */
    private AipFace client = null;

    public void init() {
        client = new AipFace(this.appId, this.apiKey, this.secretKey);
    }

    @Override
    public RealnameResultEnum personVerify(String realName, String idCard, String imgUrl) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>(4);
        options.put("quality_control", this.qualityControl);
        options.put("liveness_control", this.livenessControl);

        //取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串
        String imageType = "URL";

        // 身份验证
        try {
            JSONObject json = client.personVerify(imgUrl, imageType, idCard, realName, options);
            BaiduRealnameResult result = JsonUtil.toObject(json.toString(), BaiduRealnameResult.class);
            if(result.getError_code() != 0) {
                log.warn("baidu-realname imgUrl={} idCard={} realname={} error_code={} error_msg={}",
                        imgUrl, idCard, realName, result.getError_code(), result.getError_msg());
                return RealnameResultEnum.NoPass;
            }
            if(result.getResult().getScore() < this.score) {
                log.warn("baidu-realname imgUrl={} idCard={} realname={} score={} < {}",
                        imgUrl, idCard, realName, json.toString(), this.score);
                return RealnameResultEnum.NoPass;
            }
            return RealnameResultEnum.Pass;
        } catch (Exception e) {
            log.error("baidu-realname error.", e);
            return RealnameResultEnum.NoPass;
        }
    }
}
