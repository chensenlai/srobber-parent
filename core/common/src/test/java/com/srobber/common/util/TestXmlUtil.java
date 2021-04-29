package com.srobber.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: XML工具类test用例
 *
 * @Author: lai
 * @Date: Create in 2019/10/24 下午3:35
 * @Modified by:
 */
public class TestXmlUtil {

    @Test
    public void testXmlToMap() throws Exception {
        String strXML = "<xml><name>lai</name><age>18</age></xml>";
        Map<String, String> data = XmlUtil.xmlToMap(strXML);
        Assert.assertEquals(data.get("name"), "lai");
        Assert.assertNotNull(data.get("age"));

        strXML = "<xml><name><![CDATA[lai]]></name></xml>";
        data = XmlUtil.xmlToMap(strXML);
        Assert.assertEquals(data.get("name"), "lai");

        strXML = "<xml><name><firstName>Hello</firstName><secondName>World</secondName></name></xml>";
        data = XmlUtil.xmlToMap(strXML);
        Assert.assertEquals(data.get("name"), "HelloWorld");
        Assert.assertNull(data.get("firstName"));
    }

    @Test
    public void testMapToXml() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("name", "lai");
        data.put("age", "<18>");
        String strXML = XmlUtil.mapToXml(data);
        System.out.println(strXML);
        Assert.assertNotNull(strXML);
    }
}
