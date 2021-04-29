package com.srobber.manager.im.model;

import lombok.Data;

/**
 * IM用户信息更新
 *
 * @author chensenlai
 * 2020-10-26 下午4:09
 */
@Data
public class ImUserUpdate {

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
