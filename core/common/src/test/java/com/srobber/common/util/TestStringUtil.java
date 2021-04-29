package com.srobber.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class TestStringUtil {

    @Test
    public void testFilterChar() {
        Assert.assertEquals(StringUtil.filterChar("*Aa你+=好*"), "Aa你好");
    }

    @Test
    public void testIsChineseChar() {
        Assert.assertTrue(StringUtil.isChineseChar('你'));
        Assert.assertFalse(StringUtil.isChineseChar('a'));
        Assert.assertFalse(StringUtil.isChineseChar('='));
    }

    @Test
    public void testIsLowerChar() {
        Assert.assertTrue(StringUtil.isLowerChar('a'));
        Assert.assertFalse(StringUtil.isLowerChar('A'));
        Assert.assertFalse(StringUtil.isLowerChar('='));
    }

    @Test
    public void testIsUpperChar() {
        Assert.assertTrue(StringUtil.isUpperChar('A'));
        Assert.assertFalse(StringUtil.isUpperChar('a'));
        Assert.assertFalse(StringUtil.isUpperChar('='));
    }

    @Test
    public void testHex2byte() {
        Assert.assertArrayEquals(StringUtil.hex2byte("0a"), new byte[]{10});
        Assert.assertArrayEquals(StringUtil.hex2byte("0a0b"), new byte[]{10, 11});
        Assert.assertNull(StringUtil.hex2byte("a"));
    }

    @Test
    public void testByte2Hex() {
        Assert.assertEquals(StringUtil.byte2hex(new byte[]{10}), "0A");
        Assert.assertEquals(StringUtil.byte2hex(new byte[]{10, 11}), "0A0B");
    }

    @Test
    public void testUuid() {
        Assert.assertEquals(StringUtil.uuid().length(), 32);
    }

    @Test
    public void testGetNotNullStr() {
        Assert.assertEquals(StringUtil.getNotNullStr("abc"), "abc");
        Assert.assertEquals(StringUtil.getNotNullStr(null), "");
        Assert.assertEquals(StringUtil.getNotNullStr(null, "nullValue"), "nullValue");
    }

    @Test
    public void testIsBlank() {
        Assert.assertTrue(StringUtil.isBlank("  \t\n"));
    }

    @Test
    public void testIsEqual() {
        Assert.assertTrue(StringUtil.isEqual(null, null));
        Assert.assertTrue(StringUtil.isEqual("hello", "hello"));
        Assert.assertFalse(StringUtil.isEqual("hello", null));
        Assert.assertFalse(StringUtil.isEqual(null, "hell"));

        Assert.assertFalse(StringUtil.isNotNullAndEqual(null, null));
    }

    @Test
    public void testAppend() {
        Assert.assertEquals(StringUtil.leftAppend("aaa", 'b', 5), "bbaaa");
        Assert.assertEquals(StringUtil.leftAppend("aaa", 'b', 2), "aaa");

        Assert.assertEquals(StringUtil.rightAppend("aaa", 'b', 5), "aaabb");
        Assert.assertEquals(StringUtil.rightAppend("aaa", 'b', 2), "aaa");
    }

    @Test
    public void testSplit() {
        String text = "1,2,3";
        List<Integer> list = StringUtil.spilt(text, ",", Integer.class);
        System.out.println(Arrays.asList(list.toArray()));
        Assert.assertEquals((int)list.get(0), 1);
        Assert.assertEquals((int)list.get(1), 2);
        Assert.assertEquals((int)list.get(2), 3);
    }
}
