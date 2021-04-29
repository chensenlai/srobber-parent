package com.srobber.manager.push.xinge;

import com.srobber.common.exeption.WrapException;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.JsonUtil;
import com.srobber.manager.push.Op;
import com.srobber.manager.push.PushClient;
import com.srobber.manager.push.PushMessage;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.bean.*;
import com.tencent.xinge.push.app.PushAppRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 信鸽推送
 * SDK: https://xg.qq.com/xg/ctr_index/download
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class XingePushClient implements PushClient {
	
	private static final int RET_SUCCESS = 0;
	private static final String RET_CODE = "ret_code";
	private static final String ERR_MSG = "err_msg";

	private String androidAppId;
	private String androidSecretKey;
	private String iosAppId;
	private String iosSecretKey;

	private XingeApp xingeApp;

	public void init() {
		xingeApp = new XingeApp(iosAppId, iosSecretKey);
	}

	@Override
	public boolean pushAndroidByAccountId(String accountId, PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.account);
		request.setAccount_list(asList(accountId));
		return doPushAndroid(request, message);
	}

	@Override
	public boolean pushAndroidByDeviceId(String deviceId, PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.token);
		request.setToken_list(asList(deviceId));
		return doPushAndroid(request, message);
	}

	@Override
	public boolean pushAndroidByTag(String[] tags, Op op, PushMessage message) {
		TagListObject tagListObject = asTagListObject(tags, op);
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.tag);
		request.setTag_list(tagListObject);
		return doPushAndroid(request, message);
	}

	@Override
	public boolean pushAndroidAll(PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.all);
		return doPushAndroid(request, message);
	}

	@Override
	public boolean pushIOSByAccountId(String accountId, PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.account);
		request.setAccount_list(asList(accountId));
		return doPushIOS(request, message);
	}

	@Override
	public boolean pushIOSByDeviceId(String deviceId, PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.token);
		request.setToken_list(asList(deviceId));
		return doPushIOS(request, message);
	}

	@Override
	public boolean pushIOSByTag(String[] tags, Op op, PushMessage message) {
		TagListObject tagListObject = asTagListObject(tags, op);
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.tag);
		request.setTag_list(tagListObject);
		return doPushIOS(request, message);
	}

	@Override
	public boolean pushIOSAll(PushMessage message) {
		PushAppRequest request = new PushAppRequest();
		request.setAudience_type(AudienceType.all);
		return doPushIOS(request, message);
	}

	private ArrayList<String> asList(String str) {
		ArrayList<String> list = new ArrayList<>(1);
		list.add(str);
		return list;
	}

	private TagListObject asTagListObject(String[] tags, Op op) {
		TagListObject tagListObject = new TagListObject();
		OpType opType = null;
		if(op == Op.AND) {
			opType = OpType.AND;
		} else if(op == Op.OR) {
			opType = OpType.OR;
		}
		ArrayList<String> tagList = new ArrayList<>(tags.length);
		tagList.addAll(Arrays.asList(tags));
		tagListObject.setOp(opType);
		tagListObject.setTags(tagList);
		return tagListObject;
	}

	private boolean doPushAndroid(PushAppRequest request, PushMessage message) {
		request.setPlatform(Platform.android);
		Message msg = new Message();
		request.setMessage(msg);
		MessageAndroid msgAndroid = new MessageAndroid();
		msg.setAndroid(msgAndroid);
		//通知类型
		MessageType type = MessageType.notify;
		if(message.getMessageType() == PushMessage.MessageType.MESSAGE) {
			type = MessageType.message;
		}
		request.setMessage_type(type);
		//通知内容
		msg.setTitle(message.getTitle());
		msg.setContent(message.getContent());
		//自定义参数
		if(message.getExtraParams() != null) {
			msgAndroid.setCustom_content(JsonUtil.toStr(message.getExtraParams()));
		}
		//通知点击动作
		ClickAction clickAction = new ClickAction();
		if(message.getClickAction() == PushMessage.ClickAction.App) {
			clickAction.setAction_type(ClickAction.TYPE_ACTIVITY);
		} else if(message.getClickAction() == PushMessage.ClickAction.ACTIVITY) {
			clickAction.setAction_type(ClickAction.TYPE_ACTIVITY);
			clickAction.setActivity(message.getClickParam());
		} else if(message.getClickAction() == PushMessage.ClickAction.URL) {
			clickAction.setAction_type(ClickAction.TYPE_URL);
			Browser browser = new Browser();
			browser.setUrl(message.getClickParam());
			//是否需要用户确认
			browser.setConfirm(1);
			clickAction.setBrowser(browser);
		} else if(message.getClickAction() == PushMessage.ClickAction.CUSTOM) {
			clickAction.setAction_type(ClickAction.TYPE_INTENT);
		}
		msgAndroid.setAction(clickAction);
		//环境设置
		if(EnvironmentContext.isProdEnv()) {
			request.setEnvironment(Environment.product);
		} else {
			request.setEnvironment(Environment.dev);
		}
		try {
			JSONObject ret = xingeApp.pushApp(request);
			if (ret == null) {
				log.error("push-Android error. ret null");
				return false;
			}
			if (ret.getInt(RET_CODE) != RET_SUCCESS) {
				log.error("push-Android error. code:{} msg:{}", ret.getInt(RET_CODE), ret.getString(ERR_MSG));
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("push-Android error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	private boolean doPushIOS(PushAppRequest request, PushMessage message) {
		request.setPlatform(Platform.ios);
		Message msg = new Message();
		request.setMessage(msg);
		MessageIOS msgIOS = new MessageIOS();
		msg.setIos(msgIOS);
		//通知类型
		MessageType type = MessageType.notify;
		if(message.getMessageType() == PushMessage.MessageType.MESSAGE) {
			type = MessageType.message;
		}
		request.setMessage_type(type);
		//通知内容
		msg.setTitle(message.getTitle());
		msg.setContent(message.getContent());
		//自定义参数
		if(message.getExtraParams() != null) {
			msgIOS.setCustom(JsonUtil.toStr(message.getExtraParams()));
		}
		//环境设置
		if(EnvironmentContext.isProdEnv()) {
			request.setEnvironment(Environment.product);
		} else {
			request.setEnvironment(Environment.dev);
		}
		try {
			JSONObject ret = xingeApp.pushApp(request);
			if (ret == null) {
				log.error("push-iOS error. ret null");
				return false;
			}
			if (ret.getInt(RET_CODE) != RET_SUCCESS) {
				log.error("push-iOS error. code:{} msg:{}", ret.getInt(RET_CODE), ret.getString(ERR_MSG));
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("push-iOS error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
}