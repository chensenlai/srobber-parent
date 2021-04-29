package com.srobber.common.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用Jaxb2.2实现XML<->Java Object的Binder.
 * 特别支持Root对象是List的情形.
 *
 * @author chensenlai
 */
@Slf4j
public class JaxbUtil {
	
	 /**
     * 对象转换成xml 
     * 默认编码UTF-8 
     */  
    public static String toXml(Object obj) {  
        return toXml(obj, "UTF-8");  
    }  
  
    /** 
     * 对象转换成xml 
     */  
    public static String toXml(Object obj, String encoding) {  
        String result = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(obj.getClass());  
            Marshaller marshaller = context.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
  
            StringWriter writer = new StringWriter();  
            marshaller.marshal(obj, writer);  
            result = writer.toString();
        } catch (Exception e) {  
        	log.error("jaxb toXml error. {}", e.getMessage());
        	throw new WrapException(e);
        }  
        return result;  
    }  
  
    /** 
     * xml转换成对象
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T toObject(String xml, Class<T> c) {  
        T t = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(c);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            //防止XXE攻击
            XMLInputFactory xif = XMLInputFactory.newFactory();
    		xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    		xif.setProperty(XMLInputFactory.SUPPORT_DTD, true);
    		XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));
            t = (T) unmarshaller.unmarshal(xsr);  
        } catch (Exception e) {  
        	log.error("jaxb toObject error. {}", e.getMessage());
            throw new WrapException(e);
        }  
        return t;  
    }  
	
    private JaxbUtil() {}
}

