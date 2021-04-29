package com.srobber.third.wechat.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srobber.common.util.HttpsClient;
import com.srobber.common.util.StringUtil;
import com.srobber.third.wechat.oauth.model.AccessToken;
import com.srobber.third.wechat.oauth.model.UserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信授权帐号管理
 *
 * @author chensenlai
 * 2020-09-17 下午7:47
 */
@Slf4j
@Data
public class WechatOAuthThirdImpl implements WechatOAuthThird {

    /**
     * 获取accessToken
     */
    private static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    /**
     * 获取用户详细信息
     */
    private static String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    private static String SNSAPI_BASE = "snsapi_base";
    private static String SNSAPI_USERINFO = "snsapi_userinfo";

    private String appid;
    private String appSecret;

    @Override
    public AccessToken getAccessToken(String code) {
        String url = ACCESS_TOKEN_URL
                .replace("APPID", appid)
                .replace("SECRET", appSecret)
                .replace("CODE", code);

        JsonNode root = null;
        try {
            String jsonStr = HttpsClient.post(url, null);
            if(StringUtil.isBlank(jsonStr) ) {
                log.error("Wechat access-token error, {} response null", url);
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(jsonStr);
            if (StringUtil.isNotBlank((String) root.path("errcode").asText())
                    && root.path("errcode").asInt() != 0) {
                log.error("Wechat access-token error, errcode:{} errmsg:{}",
                        root.path("errcode").asInt(), root.path("errmsg").asText());
                return null;
            }
        } catch (Exception e) {
            log.error("Wechat access-token error", e);
            return null;
        }
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(root.path("access_token").asText());
        accessToken.setExpiresIn(root.path("expires_in").asInt());
        accessToken.setRefreshToken(root.path("refresh_token").asText());
        accessToken.setOpenid(root.path("openid").asText());
        accessToken.setScope(root.path("scope").asText());
        return accessToken;
    }

    @Override
    public UserInfo getUserInfo(String accessToken, String openid) {
        String url = USER_INFO_URL
                .replace("ACCESS_TOKEN", accessToken)
                .replace("OPENID", openid);

        JsonNode root = null;
        try {
            String jsonStr = HttpsClient.post(url, null);
            if(StringUtil.isBlank(jsonStr) ) {
                log.error("Wechat user-info error, {} response null", url);
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(jsonStr);
            if (StringUtil.isNotBlank((String) root.path("errcode").asText())
                    && root.path("errcode").asInt() != 0) {
                log.error("Wechat user-info error, errcode:{} errmsg:{}",
                        root.path("errcode").asInt(), root.path("errmsg").asText());
                return null;
            }
        } catch (Exception e) {
            log.error("Wechat user-info error", e);
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setOpenid(root.path("openid").asText());
        userInfo.setUnionid(root.path("unionid").asText());
        userInfo.setNickname(root.path("nickname").asText());
        userInfo.setHeadimgurl(root.path("headimgurl").asText());
        userInfo.setSex(root.path("sex").asInt());
        userInfo.setCity(root.path("city").asText());
        userInfo.setProvince(root.path("province").asText());
        userInfo.setCountry(root.path("country").asText());
        return userInfo;
    }
}
