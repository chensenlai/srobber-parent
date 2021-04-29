package com.srobber.common.jump;

import com.srobber.common.enums.BaseEnum;

import java.util.List;

/**
 * 统一跳转类型, 规范化APP跳转
 * 1.不需要跳转
 * 2.跳转H5
 * 3.跳转小程序
 * 4.跳转APP定义页面
 *
 * @author chensenlai
 */
public interface BaseJumpTypeEnum extends BaseEnum {

    /**
     * 跳转类型
     * @return 跳转类型
     */
    String getJumpType();

    /**
     * 跳转参数定义
     * @return 参数定义
     */
    List<ParamDefinition> getParamDefinitions();

    /**
     * 跳转参数构造器
     * @return 参数构造器
     */
    BaseParamBuilder getParamBuilder();
}
