package com.srobber.common.util;

import org.junit.Test;

/**
 * @author chensenlai
 * 2020-10-16 下午2:51
 */
public class TestIpUtil {

    @Test
    public void testIpAddress() {
        IpUtil.ProvinceCity pc = IpUtil.ipAddress("171.106.37.219");
        System.out.println(pc.getProvince()+", "+pc.getCity());
    }
}
