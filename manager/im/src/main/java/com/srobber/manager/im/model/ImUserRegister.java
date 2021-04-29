package com.srobber.manager.im.model;

import lombok.Data;

/**
 * 用户注册请求
 *
 * @author chensenlai
 * 2020-10-26 下午3:39
 */
@Data
public class ImUserRegister {

    /**
     * 用户唯一标识
     */
    private String userId;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户头像地址
     */
    private String headImg;
}
