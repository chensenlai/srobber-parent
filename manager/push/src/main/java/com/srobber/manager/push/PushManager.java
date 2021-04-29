package com.srobber.manager.push;


/**
 * APP推送
 *
 * @author chensenlai
 */
public interface PushManager {

    /**
     * 按帐号推送
     * @param accountId 帐号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushByAccountId(String accountId, PushMessage message);

    /**
     * 按设备推送
     * @param deviceId 设备号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushByDeviceId(String deviceId, PushMessage message);

    /**
     * 按打标签推送
     * @param tags 标签
     * @param op 标签操作(与/或)
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushByTag(String[] tags, Op op, PushMessage message);

    /**
     * 推送所有
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushAll(PushMessage message);

    /**
     * 按帐号推送
     * @param accountId 帐号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushAndroidByAccountId(String accountId, PushMessage message);

    /**
     * 按设备推送
     * @param deviceId 设备号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushAndroidByDeviceId(String deviceId, PushMessage message);

    /**
     * 按打标签推送
     * @param tags 标签
     * @param op 标签操作(与/或)
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushAndroidByTag(String[] tags, Op op, PushMessage message);

    /**
     * 推送所有
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushAndroidAll(PushMessage message);

    /**
     * 按帐号推送
     * @param accountId 帐号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushIOSByAccountId(String accountId, PushMessage message);

    /**
     * 按设备推送
     * @param deviceId 设备号
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushIOSByDeviceId(String deviceId, PushMessage message);

    /**
     * 按打标签推送
     * @param tags 标签
     * @param op 标签操作(与/或)
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushIOSByTag(String[] tags, Op op, PushMessage message);

    /**
     * 推送所有
     * @param message 推送消息
     * @return 推送结果
     */
    boolean pushIOSAll(PushMessage message);
}
