package com.srobber.manager.push.jpush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.srobber.common.exeption.WrapException;
import com.srobber.manager.push.Op;
import com.srobber.manager.push.PushClient;
import com.srobber.manager.push.PushMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 极光推送
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class JpushPushClient implements PushClient {

    private String masterSecret;
    private String appKey;

    private JPushClient jPushClient;

    public void init() {
        jPushClient = new JPushClient(masterSecret, appKey);
    }

    @Override
    public boolean pushAndroidByAccountId(String accountId, PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.alias(accountId));
        return doPushAndroid(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushAndroidByDeviceId(String deviceId, PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.registrationId(deviceId));
        return doPushAndroid(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushAndroidByTag(String[] tags, Op op, PushMessage message) {
        Audience audience = asTag(tags, op);
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(audience);
        return doPushAndroid(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushAndroidAll(PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.all());
        return doPushAndroid(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushIOSByAccountId(String accountId, PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.alias(accountId));
        return doPushIOS(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushIOSByDeviceId(String deviceId, PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.registrationId(deviceId));
        return doPushIOS(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushIOSByTag(String[] tags, Op op, PushMessage message) {
        Audience audience = asTag(tags, op);
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(audience);
        return doPushIOS(pushPayloadBuilder, message);
    }

    @Override
    public boolean pushIOSAll(PushMessage message) {
        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder();
        pushPayloadBuilder.setAudience(Audience.all());
        return doPushIOS(pushPayloadBuilder, message);
    }

    private Audience asTag(String[] tags, Op op) {
        if(op == Op.AND) {
            return Audience.tag_and(tags);
        } else if(op == Op.OR) {
            return Audience.tag(tags);
        }
        throw new IllegalArgumentException("tag op " + op);
    }

    private boolean doPushAndroid(PushPayload.Builder pushPayloadBuilder, PushMessage message) {
        pushPayloadBuilder.setPlatform(Platform.android());
        //推送的类型
        if(message.getMessageType() == PushMessage.MessageType.NOTIFY) {
            Notification notification = Notification.newBuilder()
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            //标题
                            .setTitle(message.getTitle())
                            //内容
                            .setAlert(message.getContent())
                            //自定义参数
                            .addExtras(message.getExtraParams())
                            .build())
                    .build();
            pushPayloadBuilder.setNotification(notification);
        } else if(message.getMessageType() == PushMessage.MessageType.MESSAGE) {
            pushPayloadBuilder.setMessage(Message.content(message.getContent()));
        }
        //TODO 通知点击类型
        try {
            PushResult pushResult = jPushClient.sendPush(pushPayloadBuilder.build());
            if (pushResult == null) {
                log.error("push-Android error. ret null");
                return false;
            }
            if (pushResult.getResponseCode() != 200) {
                log.error("push-Android error. code:{} msg:{}", pushResult.getResponseCode(), pushResult.getOriginalContent());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("push-Android error. {}", e.getMessage());
            throw new WrapException(e);
        }
    }

    private boolean doPushIOS(PushPayload.Builder pushPayloadBuilder, PushMessage message) {
        pushPayloadBuilder.setPlatform(Platform.ios());
        //推送的类型
        if(message.getMessageType() == PushMessage.MessageType.NOTIFY) {
            Notification notification = Notification.newBuilder()
                    .addPlatformNotification(IosNotification.newBuilder()
                            //内容
                            .setAlert(message.getContent())
                            .autoBadge()
                            //自定义参数
                            .addExtras(message.getExtraParams())
                            .build())
                    .build();
            pushPayloadBuilder.setNotification(notification);
        } else if(message.getMessageType() == PushMessage.MessageType.MESSAGE) {
            pushPayloadBuilder.setMessage(Message.content(message.getContent()));
        }
        try {
            PushResult pushResult = jPushClient.sendPush(pushPayloadBuilder.build());
            if (pushResult == null) {
                log.error("push-iOS error. ret null");
                return false;
            }
            if (pushResult.getResponseCode() != 200) {
                log.error("push-iOS error. code:{} msg:{}", pushResult.getResponseCode(), pushResult.getOriginalContent());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("push-iOS error. {}", e.getMessage());
            throw new WrapException(e);
        }
    }
}
