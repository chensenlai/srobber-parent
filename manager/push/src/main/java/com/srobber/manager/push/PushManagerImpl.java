package com.srobber.manager.push;

import lombok.Data;

/**
 * APP推送
 *
 * @author chensenlai
 */
@Data
public class PushManagerImpl implements PushManager {

    private PushClient pushClient;

    @Override
    public boolean pushByAccountId(String accountId, PushMessage message) {
        return pushClient.pushAndroidByAccountId(accountId, message)
                || pushClient.pushIOSByAccountId(accountId, message);
    }

    @Override
    public boolean pushByDeviceId(String deviceId, PushMessage message) {
        return pushAndroidByDeviceId(deviceId, message)
                || pushIOSByAccountId(deviceId, message);
    }

    @Override
    public boolean pushByTag(String[] tags, Op op, PushMessage message) {
        return pushAndroidByTag(tags, op, message)
                || pushIOSByTag(tags, op, message);
    }

    @Override
    public boolean pushAll(PushMessage message) {
        return pushAndroidAll(message)
                || pushIOSAll(message);
    }

    @Override
    public boolean pushAndroidByAccountId(String accountId, PushMessage message) {
        return pushClient.pushAndroidByAccountId(accountId, message);
    }

    @Override
    public boolean pushAndroidByDeviceId(String deviceId, PushMessage message) {
        return pushClient.pushAndroidByDeviceId(deviceId, message);
    }

    @Override
    public boolean pushAndroidByTag(String[] tags, Op op, PushMessage message) {
        return pushClient.pushAndroidByTag(tags, op, message);
    }

    @Override
    public boolean pushAndroidAll(PushMessage message) {
        return pushClient.pushAndroidAll(message);
    }

    @Override
    public boolean pushIOSByAccountId(String accountId, PushMessage message) {
        return pushClient.pushIOSByAccountId(accountId, message);
    }

    @Override
    public boolean pushIOSByDeviceId(String deviceId, PushMessage message) {
        return pushClient.pushIOSByDeviceId(deviceId, message);
    }

    @Override
    public boolean pushIOSByTag(String[] tags, Op op, PushMessage message) {
        return pushClient.pushIOSByTag(tags, op, message);
    }

    @Override
    public boolean pushIOSAll(PushMessage message) {
        return pushClient.pushIOSAll(message);
    }
}
