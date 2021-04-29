package com.srobber.manager.im;

import com.srobber.manager.im.model.ImUserRegister;
import com.srobber.manager.im.model.ImUserUpdate;

/**
 * IM用户管理
 *
 * @author chensenlai
 * 2020-10-26 下午3:16
 */
public interface ImUserManager {

    /**
     * 用户注册IM
     * @param req 注册请求
     * @return token
     */
    String register(ImUserRegister req);

    /**
     * 更新IM用户信息
     * @param req 更新请求
     */
    void update(ImUserUpdate req);

}
