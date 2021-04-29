package com.srobber.common.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * XML和Map互转工具类
 * 已修复XXE漏洞
 *
 * @author chensenlai
 */
@Slf4j
public class XmlUtil {

	/**
     * XML格式字符串转换为Map
     * eg: <name>lai</name> => name=lai
     *     <user><name>lai</name><age>18</age></user> => user=lai18
     * @param strXml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXml) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            String FEATURE = null;
            try {
            	// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
            	// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            	FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            	documentBuilderFactory.setFeature(FEATURE, true);
            	
            	// If you can't completely disable DTDs, then at least do the following:
            	// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            	// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            	// JDK7+ - http://xml.org/sax/features/external-general-entities 
            	FEATURE = "http://xml.org/sax/features/external-general-entities";
            	documentBuilderFactory.setFeature(FEATURE, false);
            	
            	// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            	// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            	// JDK7+ - http://xml.org/sax/features/external-parameter-entities 
            	FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            	documentBuilderFactory.setFeature(FEATURE, false);
            	
            	// Disable external DTDs as well
            	FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            	documentBuilderFactory.setFeature(FEATURE, false);
            	
            	// and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            	documentBuilderFactory.setXIncludeAware(false);
            	documentBuilderFactory.setExpandEntityReferences(false);
            	
            	// And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then 
            	// ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
            	// (http://cwe.mitre.org/data/definitions/918.html) and denial 
            	// of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."
            	
            	// remaining parser logic
            } catch (ParserConfigurationException e) {
            	// This should catch a failed setFeature feature
            	log.info("ParserConfigurationException was thrown. The feature '" +
            	FEATURE + "' is probably not supported by your XML processor.");
            }
            catch (Exception e) {
            	// On Apache, this should be thrown when disallowing DOCTYPE
                log.warn("A DOCTYPE was passed into the XML document");
                log.error("IOException occurred, XXE may still possible: " + e.getMessage());
            }
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            log.warn("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXml);
            throw ex;
        }

    }

    /**
     * 将Map转换为XML格式的字符串
     * 特殊字符会转义处理
     * eg: age=<18> => <age>&lt;18&gt;</age>
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString();
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }
}
