package com.srobber.manager.im;

import com.srobber.manager.im.model.ImChatroomCreate;
import com.srobber.manager.im.model.ImChatroomDestroy;
import com.srobber.manager.im.model.ImChatroomUserExists;
import com.srobber.manager.im.model.ImChatroomUserTotal;

/**
 * IM聊天室管理
 *
 * @author chensenlai
 * 2020-10-26 下午3:55
 */
public interface ImChatroomManager {

    /**
     * 创建聊天室
     * @param req 创建聊天室请求
     */
    void create(ImChatroomCreate req);

    /**
     * 销毁聊天室
     * @param req 销毁聊天室请求
     */
    void destroy(ImChatroomDestroy req);

    /**
     * 查询聊天室在线用户数
     * @param req 查询在线用户数请求
     * @return 在线用户数
     */
    int getUserTotalInChatroom(ImChatroomUserTotal req);

    /**
     * 检查用户是否在聊天室
     * @param req 检查用户是否在聊天室请求
     * @return 是否存在
     */
    boolean checkUserInChatroom(ImChatroomUserExists req);
}
