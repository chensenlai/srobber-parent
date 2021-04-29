package com.srobber.manager.im.rongcloud;

import com.srobber.common.util.JsonUtil;
import com.srobber.common.util.StringUtil;
import com.srobber.manager.im.ImException;
import com.srobber.manager.im.ImMessageManager;
import com.srobber.manager.im.model.ImMessageChatroom;
import com.srobber.manager.im.model.ImMessagePrivate;
import com.srobber.manager.im.model.ImMessageSystem;
import com.srobber.manager.im.rongcloud.message.ImChatroomContent;
import com.srobber.manager.im.rongcloud.message.ImPrivateContent;
import com.srobber.manager.im.rongcloud.message.ImSystemContent;
import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.message.ChatroomMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.ResponseResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 融云消息发送管理
 *
 * @author chensenlai
 * 2020-10-26 下午5:25
 */
@Slf4j
@Data
public class RongcloudMessageManager implements ImMessageManager {

    /**
     * 融云默认用户
     */
    private static final String RONGCLOUD_USER = "rongcloud_user";


    private String appKey;
    private String appSecret;

    @Override
    public boolean sendPrivate(ImMessagePrivate req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Private msgPrivate = rongCloud.message.msgPrivate;
        ResponseResult result = null;
        try {
            BaseMessage content = null;
            if(req.getContent() instanceof BaseMessage) {
                content = (BaseMessage) req.getContent();
            } else {
                content = new ImPrivateContent(req.getCode(), req.getContent().toString());
            }
            PrivateMessage message = new PrivateMessage()
                    .setSenderId(StringUtil.getNotNullStr(req.getSendUserId(), RONGCLOUD_USER))
                    .setTargetId(req.getReceiveUserIds())
                    .setObjectName(content.getType())
                    .setContent(content)
                    .setPushData(req.getPushData())
                    ;

            result = msgPrivate.send(message);
            RongcloudUtil.checkSuccess(result);
            return true;
        } catch (Exception e) {
            log.error("Rongcloud IM sendOne {} error", JsonUtil.toStr(req), e);
            throw new ImException(e);
        }
    }

    @Override
    public boolean sendChatroom(ImMessageChatroom req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Chatroom chatroom = rongCloud.message.chatroom;

        BaseMessage content = new ImChatroomContent(req.getCode(), req.getContent().toString());
        ChatroomMessage chatroomMessage = new ChatroomMessage()
                .setSenderId(StringUtil.getNotNullStr(req.getSendUserId(), RONGCLOUD_USER))
                .setTargetId(new String[]{req.getRoomId()})
                .setObjectName(content.getType())
                .setContent(content);
        ResponseResult result = null;
        try {
            result = chatroom.send(chatroomMessage);
            RongcloudUtil.checkSuccess(result);
            return true;
        } catch (Exception e) {
            log.error("Rongcloud IM sendChatroom {} error", JsonUtil.toStr(req), e);
            throw new ImException(e);
        }
    }

    @Override
    public boolean sendSystem(ImMessageSystem req) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        MsgSystem system = rongCloud.message.system;
        BaseMessage content = new ImSystemContent(req.getCode(), req.getPushData());
        SystemMessage systemMessage = new SystemMessage()
                .setSenderId(StringUtil.getNotNullStr(req.getSendUserId(), RONGCLOUD_USER))
                .setTargetId(req.getReceiveUserIds())
                .setObjectName(content.getType())
                .setContent(content)
                .setPushData(req.getPushData())
                .setPushContent(req.getContent())
                ;
        ResponseResult result = null;
        try {
            result = system.send(systemMessage);
            RongcloudUtil.checkSuccess(result);
            return true;
        } catch (Exception e) {
            log.error("Rongcloud IM sendSystem {} error", JsonUtil.toStr(req), e);
            throw new ImException(e);
        }
    }
}
