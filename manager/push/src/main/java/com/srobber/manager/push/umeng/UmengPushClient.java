package com.srobber.manager.push.umeng;

import com.srobber.common.exeption.WrapException;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.StringUtil;
import com.srobber.manager.push.Op;
import com.srobber.manager.push.PushClient;
import com.srobber.manager.push.PushMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import push.umeng.AndroidNotification;
import push.umeng.IOSNotification;
import push.umeng.android.AndroidBroadcast;
import push.umeng.android.AndroidCustomizedcast;
import push.umeng.android.AndroidGroupcast;
import push.umeng.android.AndroidUnicast;
import push.umeng.ios.IOSBroadcast;
import push.umeng.ios.IOSCustomizedcast;
import push.umeng.ios.IOSGroupcast;
import push.umeng.ios.IOSUnicast;

import java.util.Map;

/**
 * 有盟推送客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class UmengPushClient implements PushClient {

	/**
	 * 离线推送厂商参数
	 */
	public static final String MIPUSH = "mipush";
	public static final String MI_ACTIVITY = "mi_activity";

	private String androidAppkey;
	private String androidAppMasterSecret;
	private String iosAppkey;
	private String iosAppMasterSecret;

	private final push.umeng.PushClient client = new push.umeng.PushClient();

	@Override
	public boolean pushAndroidByAccountId(String accountId, PushMessage message) {
		try {
			AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(androidAppkey, androidAppMasterSecret);
			customizedcast.setAlias(accountId, PushClient.ACCOUNT_ID);
			return doPushAndroid(customizedcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushAndroidByDeviceId(String deviceId, PushMessage message) {
		try {
			AndroidUnicast unicast = new AndroidUnicast(androidAppkey, androidAppMasterSecret);
			unicast.setDeviceToken(deviceId);
			return doPushAndroid(unicast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	/**
	 * 友盟自带的标签(会和友盟强耦合,建议客户端自己分类打标签)
	 *
	 * 目前开放的筛选字段有:
	 *
	 * “app_version”(应用版本)
	 * “channel”(渠道)
	 * “device_model”(设备型号)
	 * “province”(省)
	 * “tag”(用户标签)
	 * “country”(国家和地区) //“country”和”province”的类型定义请参照 附录J
	 * “language”(语言)
	 * “launch_from”(一段时间内活跃)
	 * “not_launch_from”(一段时间内不活跃)
	 */
	@Override
	public boolean pushAndroidByTag(String[] tags, Op op, PushMessage message) {
		JSONObject filter = asFilter(tags, op);
		try {
			AndroidGroupcast groupcast = new AndroidGroupcast(androidAppkey, androidAppMasterSecret);
			groupcast.setFilter(filter);
			return doPushAndroid(groupcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushAndroidAll(PushMessage message) {
		try {
			AndroidBroadcast broadcast = new AndroidBroadcast(androidAppkey, androidAppMasterSecret);
			return doPushAndroid(broadcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushIOSByAccountId(String accountId, PushMessage message) {
		try {
			IOSCustomizedcast customizedcast = new IOSCustomizedcast(iosAppkey, iosAppMasterSecret);
			customizedcast.setAlias(accountId, PushClient.ACCOUNT_ID);
			return doPushIOS(customizedcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushIOSByDeviceId(String deviceId, PushMessage message) {
		try {
			IOSUnicast unicast = new IOSUnicast(iosAppkey, iosAppMasterSecret);
			unicast.setDeviceToken(deviceId);
			return doPushIOS(unicast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushIOSByTag(String[] tags, Op op, PushMessage message) {
		JSONObject filter = asFilter(tags, op);
		try {
			IOSGroupcast groupcast = new IOSGroupcast(iosAppkey, iosAppMasterSecret);
			groupcast.setFilter(filter);
			return doPushIOS(groupcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	@Override
	public boolean pushIOSAll(PushMessage message) {
		try {
			IOSBroadcast broadcast = new IOSBroadcast(iosAppkey, iosAppMasterSecret);
			return doPushIOS(broadcast, message);
		} catch (Exception e) {
			throw new WrapException(e);
		}
	}

	private JSONObject asFilter(String[] tags, Op op) {
		JSONObject filter = new JSONObject();
		JSONObject where = new JSONObject();
		if(op == Op.AND) {
			where.put("and", tags);
		} else if(op == Op.OR) {
			where.put("or", tags);
		}
		filter.put("where", where);
		return filter;
	}


	private boolean doPushAndroid(AndroidNotification notify, PushMessage message) {
		try {
			//通知类型
			AndroidNotification.DisplayType displayType = AndroidNotification.DisplayType.NOTIFICATION;
			if(message.getMessageType() == PushMessage.MessageType.MESSAGE) {
				displayType = AndroidNotification.DisplayType.MESSAGE;
			}
			notify.setDisplayType(displayType);
			//通知内容
			notify.setTicker(message.getTip());
			notify.setTitle(message.getTitle());
			notify.setText(message.getContent());

			//通知点击动作
			if(message.getClickAction() == PushMessage.ClickAction.App) {
				notify.goAppAfterOpen();
			} else if(message.getClickAction() == PushMessage.ClickAction.ACTIVITY) {
				notify.goActivityAfterOpen(message.getClickParam());
			} else if(message.getClickAction() == PushMessage.ClickAction.URL) {
				notify.goUrlAfterOpen(message.getClickParam());
			} else if(message.getClickAction() == PushMessage.ClickAction.CUSTOM) {
				notify.goCustomAfterOpen(message.getClickParam());
			}
			//友盟配置厂商通道, 实现离线推送
			if(message.getExtraParams() != null) {
				Object mipush = message.getExtraParams().get(MIPUSH);
				if(mipush != null) {
					notify.setPredefinedKeyValue(MIPUSH, mipush);
					message.getExtraParams().remove(MIPUSH);
				}
				Object miActivity = message.getExtraParams().get(MI_ACTIVITY);
				if(miActivity != null) {
					notify.setPredefinedKeyValue(MI_ACTIVITY, miActivity);
					message.getExtraParams().remove(MI_ACTIVITY);
				}
			}
			//自定义参数
			if(message.getExtraParams() != null) {
				for(Map.Entry<String, String> entity : message.getExtraParams().entrySet()) {
					notify.setExtraField(entity.getKey(), entity.getValue());
				}
			}
			//环境设置
			if(EnvironmentContext.isProdEnv()) {
				notify.setProductionMode();
			} else {
				notify.setTestMode();
			}

			String ret = client.send(notify);
			if(StringUtil.isNotBlank(ret)) {
				log.error("push-Android error. {}", ret);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("push-Android error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	private boolean doPushIOS(IOSNotification notify, PushMessage message) {
		try {
			//通知内容
			notify.setAlert(message.getTitle()+"\r\n"+message.getContent());
			notify.setBadge(1);
			notify.setSound("default");

			//自定义参数
			if(message.getExtraParams() != null) {
				for(Map.Entry<String, String> entity : message.getExtraParams().entrySet()) {
					notify.setCustomizedField(entity.getKey(), entity.getValue());
				}
			}
			//环境设置
			if(EnvironmentContext.isProdEnv()) {
				notify.setProductionMode();
			} else {
				notify.setTestMode();
			}

			String ret = client.send(notify);
			if(StringUtil.isNotBlank(ret)) {
				log.error("push-iOS error. {}", ret);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("push-iOS error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
}
