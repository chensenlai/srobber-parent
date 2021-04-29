package com.srobber.manager.sms.xuanwu;

import com.esms.MessageData;
import com.esms.PostMsg;
import com.esms.common.entity.Account;
import com.esms.common.entity.GsmsResponse;
import com.esms.common.entity.MTPack;
import com.srobber.common.util.DateFormatUtil;
import com.srobber.common.util.DateUtil;
import com.srobber.common.util.PlaceholderResolverUtil;
import com.srobber.manager.sms.SmsException;
import com.srobber.manager.sms.SmsManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 玄武短信客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class XuanwuSmsManager implements SmsManager {

	private String user;
	private String psw;
	private String ip;
	private int mtPort;
	private int moPort;
	
	private Account ac;
	private PostMsg pm;
	
	public void init() {
		this.ac = new Account(user, psw);
		this.pm = new PostMsg();
		//设置网关的IP和port，用于发送信息
		this.pm.getCmHost().setHost(ip, mtPort);
		//设置网关的IP和port，用于获取账号信息、上行、状态报告等等
		this.pm.getWsHost().setHost(ip, moPort);
	}

	@Override
	public boolean send(String phone, String templateId,
						String templateContent, Map<String, String> templateParamMap) {
		String batchName = "S_"+ DateFormatUtil.format(DateUtil.now(),
				DateFormatUtil.Pattern.YYYYMMDDHHMMSSSSS);
		String content = PlaceholderResolverUtil.resolver(templateContent, templateParamMap);
		MTPack pack = new MTPack();
		pack.setBatchID(UUID.randomUUID());
		pack.setBatchName(batchName);
		pack.setMsgType(MTPack.MsgType.SMS);
		pack.setBizType(0);
		pack.setSendType(MTPack.SendType.MASS);
		pack.setDistinctFlag(false);
		/*单发，一号码一内容*/
		List<MessageData> msgs = new ArrayList<MessageData>(1);
		msgs.add(new MessageData(phone, content));
		pack.setMsgs(msgs);
		
		try {
			GsmsResponse resp = this.pm.post(this.ac, pack);
			if(resp.getResult() != 0) {
				log.warn("sms send {} error. {} {}", phone, resp.getResult(), resp.getMessage());
				return false;
			}
			log.info("sms send {} ok.", phone);
			return true;
		} catch (Exception e) {
			log.error("sms send {} error. {}", phone, e.getMessage());
			throw new SmsException("短信发送失败");
		}
	}
	
	@Override
	public boolean batchSend(List<String> phoneList, String templateId,
							 String templateContent, List<Map<String, String>> templateParamMapList){
		String batchName = "B_"+DateFormatUtil.format(DateUtil.now(),
				DateFormatUtil.Pattern.YYYYMMDDHHMMSSSSS);
		MTPack pack = new MTPack();
		pack.setBatchID(UUID.randomUUID());
		pack.setBatchName(batchName);
		pack.setMsgType(MTPack.MsgType.SMS);
		pack.setBizType(0);
		pack.setSendType(MTPack.SendType.MASS);
		pack.setDistinctFlag(false);
		/*多发，多号码一内容*/
		List<MessageData> msgs = new ArrayList<MessageData>(phoneList.size());
		for(int i=0,len=phoneList.size(); i<len; i++) {
			String phone = phoneList.get(i);
			Map<String, String> templateParamMap = templateParamMapList.get(i);
			String content = PlaceholderResolverUtil.resolver(templateContent, templateParamMap);
			msgs.add(new MessageData(phone, content));
		}
		pack.setMsgs(msgs);
		
		try {
			GsmsResponse resp = this.pm.post(this.ac, pack);
			if(resp.getResult() != 0) {
				log.warn("sms batchSend {} error. {} {}", Arrays.toString(phoneList.toArray()), resp.getResult(), resp.getMessage());
				return false;
			}
			log.info("sms batchSend {} ok.", Arrays.toString(phoneList.toArray()));
			return true;
		} catch (Exception e) {
			log.error("sms batchSend {} error. {}", Arrays.toString(phoneList.toArray()), e.getMessage());
			throw new SmsException("短信发送失败");
		}
	}
}
