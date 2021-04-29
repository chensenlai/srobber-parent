package com.srobber.manager.im.rongcloud;

import com.srobber.manager.im.ImChatroomManager;
import com.srobber.manager.im.ImException;
import com.srobber.manager.im.model.ImChatroomCreate;
import com.srobber.manager.im.model.ImChatroomDestroy;
import com.srobber.manager.im.model.ImChatroomUserExists;
import com.srobber.manager.im.model.ImChatroomUserTotal;
import io.rong.RongCloud;
import io.rong.methods.chatroom.Chatroom;
import io.rong.models.chatroom.ChatroomMember;
import io.rong.models.chatroom.ChatroomModel;
import io.rong.models.response.ChatroomUserQueryResult;
import io.rong.models.response.CheckChatRoomUserResult;
import io.rong.models.response.ResponseResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 融云聊天室管理客户端
 *
 * @author chensenlai
 * 2020-10-26 下午4:34
 */
@Slf4j
@Data
public class RongcloudChatroomManager implements ImChatroomManager {

    private String appKey;
    private String appSecret;

    @Override
    public void create(ImChatroomCreate req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Chatroom chatroom = rongCloud.chatroom;
        ChatroomModel chatroomModel = new ChatroomModel()
                .setId(req.getRoomId())
                .setName(req.getName());
        ResponseResult result = null;
        try {
            result = chatroom.create(new ChatroomModel[]{chatroomModel});
            RongcloudUtil.checkSuccess(result);
        } catch (Exception e) {
            log.error("Rongcloud IM chatroom {} create error", req.getRoomId(), e);
            throw new ImException(e);
        }
    }

    @Override
    public void destroy(ImChatroomDestroy req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Chatroom chatroom = rongCloud.chatroom;
        ChatroomModel chatroomModel = new ChatroomModel()
                .setId(req.getRoomId());
        ResponseResult result = null;
        try {
            result = chatroom.destroy(chatroomModel);
            RongcloudUtil.checkSuccess(result);
        } catch (Exception e) {
            log.error("Rongcloud IM chatroom {} destroy error", req.getRoomId(), e);
            throw new ImException(e);
        }
    }

    @Override
    public int getUserTotalInChatroom(ImChatroomUserTotal req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Chatroom chatroom = rongCloud.chatroom;

        ChatroomModel chatroomModel = new ChatroomModel()
                .setId(req.getRoomId())
                .setCount(0)
                .setOrder(1);
        ChatroomUserQueryResult result = null;
        try {
            result = chatroom.get(chatroomModel);
            if(result == null) {
                return 0;
            }
            return result.getTotal() == null ? 0 : result.getTotal();
        } catch (Exception e) {
            log.error("Rongcloud IM chatroom {} getUserTotalInChatroom error", req.getRoomId(), e);
            throw new ImException(e);
        }
    }

    @Override
    public boolean checkUserInChatroom(ImChatroomUserExists req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Chatroom chatroom = rongCloud.chatroom;
        ChatroomMember member = new ChatroomMember()
                .setChatroomId(req.getRoomId())
                .setId(req.getUserId());
        CheckChatRoomUserResult result = null;
        try {
            result = chatroom.isExist(member);
            if(result == null) {
                return false;
            }
            if(result.getInChrm() == null) {
                return false;
            }
            return result.getInChrm() == null ? false : result.getInChrm();
        } catch (Exception e) {
            log.error("Rongcloud IM chatroom {} destroy error", req.getRoomId(), e);
            throw new ImException(e);
        }
    }

}
