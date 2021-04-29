package com.srobber.common.util;

import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chensenlai
 * 2020-10-29 下午12:37
 */
public class TestJsonTypeAwareUtil {

    @Test
    public void testJson() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setName("u1");
        userList.add(user1);

        User user2 = new User();
        user2.setName("u2");
        userList.add(user2);
        String json = JsonTypeAwareUtil.toStr(userList);
        System.out.println(json);

        Object obj = JsonTypeAwareUtil.toObject(json);
        System.out.println(obj.getClass());
    }

    @Test
    public void testLong() {
        Long l = Long.MAX_VALUE;
        System.out.println(l);
        String str = JsonTypeAwareUtil.toStr(l);
        System.out.println(JsonTypeAwareUtil.toObject(str).getClass());
    }

    @Data
    static class User{
        private String name;
    }
}
