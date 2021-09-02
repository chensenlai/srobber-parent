package com.srobber.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.srobber.common.exeption.WrapException;
import com.srobber.common.enums.BaseEnum;
import com.srobber.common.jackson.EnumJsonDeserializer;
import com.srobber.common.jackson.EnumJsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON数据与对象转化的工具类
 * 1、属性和属性值支持没有引号，单引号，双引号。 支持转移字符。
 * 2、序列化时null对象序列化为null, 忽略对象中null属性。
 * 3、反序列化忽略未知属性。
 * 4、对系统定义的枚举类特殊。
 *
 * 对于布尔值boolean/Boolean转化测试:
 * 1)非0(true)/0(false)
 * 2)字符串"true"/"false"
 * 3)true/false互转
 *
 * @author chensenlai
 */
@Slf4j
public class JsonUtil {

	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		initMapper(mapper);
	}

	/**
	 * 初始化jackson mapper
	 * 提供工具类, MVC消息转化, Cache储存等初始化mapper
	 * @param mapper JSON映射器
	 */
	public static void initMapper(ObjectMapper mapper) {
		//转义字符
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		//没有引号
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		//单引号
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		//序列化特性配置
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.ALWAYS);

		//反序列化特性配置
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		//日期格式
		mapper.setDateFormat(new SimpleDateFormat(DateFormatUtil.Pattern.YYYY_MM_DD_HH_MM_SS.getPattern()));

        //FIXME JSON和枚举类的处理强耦合在一起
		//对于系统定义枚举值的特殊处理
		SimpleModule module = new SimpleModule();
		for(final Class<? extends BaseEnum> enumClass : BaseEnumUtil.all()) {
			module.addSerializer(enumClass, (EnumJsonSerializer<BaseEnum>)new EnumJsonSerializer<>(enumClass));
		}
		for(final Class<? extends BaseEnum> enumClass : BaseEnumUtil.all()) {
			module.addDeserializer((Class<BaseEnum>)enumClass, new EnumJsonDeserializer<>(enumClass));
		}
		mapper.registerModule(module);
	}

	/**
	 * json字符转对象
	 * @param jsonStr json字符串
	 * @param clazz 转化类型
	 * @return java对象
	 */
	public static <T> T toObject(String jsonStr, Class<T> clazz) {
		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (IOException e) {
			log.error("toObject error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}
	
	/**
	 * json字符转对象, 支持泛型
	 * @param jsonStr json字符串
	 * @param valueTypeRef 对象类型
	 * @param <T> 泛型约束
	 * @return 对象
	 */
	public static <T> T toObject(String jsonStr, TypeReference<T> valueTypeRef) {
		try {
			return mapper.readValue(jsonStr, valueTypeRef);
		} catch (IOException e) {
			log.error("toObject error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	/**
	 * json转HashMap
	 * @param jsonStr json字符串
	 * @return hashMap对象
	 */
	public static HashMap<?, ?> toMap(String jsonStr) {
		try {
			return mapper.readValue(jsonStr, HashMap.class);
		} catch (IOException e) {
			log.error("toMap error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	/**
	 * json转List
	 * @param jsonStr json字符串
	 * @param clazz 储存对象类型
	 * @param <T> 泛型约束
	 * @return List对象
	 */
	public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
		try {
			CollectionType listType = mapper.getTypeFactory()
					.constructCollectionType(ArrayList.class, clazz);
			return mapper.readValue(jsonStr, listType);
		} catch (IOException e) {
			log.error("toList error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	/**
	 * 对象转json字符串
	 * @param obj 对象
	 * @return json字符串
	 */
	public static String toStr(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("toStr error. {}", e.getMessage());
			throw new WrapException(e);
		}
	}

	/**
	 * map转json字符串
	 * @param map map对象
	 * @return json字符串
	 */
	public static String toStr(Map<?, ?> map) {
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, map);
		} catch (Exception e) {
			log.error("toStr error. {}", e.getMessage());
			throw new WrapException(e);
		}
		return writer.toString();
	}

	public JsonUtil() {}
}
