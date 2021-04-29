package com.srobber.manager.im;

import com.srobber.manager.im.model.ImMessageChatroom;
import com.srobber.manager.im.model.ImMessagePrivate;
import com.srobber.manager.im.model.ImMessageSystem;

/**
 * IM消息管理
 *
 * @author chensenlai
 * 2020-10-26 下午5:19
 */
public interface ImMessageManager {

    /**
     * 发送私聊消息
     * @param req 发送请求
     * @return 发送结果
     */
    boolean sendPrivate(ImMessagePrivate req);

    /**
     * 发送聊天室消息
     * @param req 发送请求
     * @return 发送结果
     */
    boolean sendChatroom(ImMessageChatroom req);

    /**
     * 发送系统消息
     * @param req 发送请求
     * @return 发送结果
     */
    boolean sendSystem(ImMessageSystem req);

}
