package com.srobber.manager.im.rongcloud;

import com.srobber.manager.im.ImException;
import com.srobber.manager.im.ImUserManager;
import com.srobber.manager.im.model.ImUserRegister;
import com.srobber.manager.im.model.ImUserUpdate;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 融云用户管理客户端
 *
 * @author chensenlai
 * 2020-10-26 下午3:59
 */
@Slf4j
@Data
public class RongcloudUserManager implements ImUserManager {

    private String appKey;
    private String appSecret;

    @Override
    public String register(ImUserRegister req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        User user = rongCloud.user;
        UserModel userModel = new UserModel()
                .setId(req.getUserId())
                .setName(req.getName())
                .setPortrait(req.getHeadImg());
        TokenResult result = null;
        try {
            result = user.register(userModel);
            RongcloudUtil.checkSuccess(result);
        } catch (Exception e) {
            log.error("Rongcloud IM user {} register error", req.getUserId(), e);
            throw new ImException(e);
        }

        return result.getToken();
    }

    @Override
    public void update(ImUserUpdate req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        User user = rongCloud.user;
        UserModel userModel = new UserModel()
                .setId(req.getUserId())
                .setName(req.getName())
                .setPortrait(req.getHeadImg());
        Result result = null;
        try {
            result = user.update(userModel);
            RongcloudUtil.checkSuccess(result);
        } catch (Exception e) {
            log.error("Rongcloud IM user {} update error", req.getUserId(), e);
            throw new ImException(e);
        }
    }
}
