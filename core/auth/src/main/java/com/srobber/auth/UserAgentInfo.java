package com.srobber.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户客户端信息
 * 格式:渠道#运营商#网络类型#客户端机型型号#客户端UI版本(int)
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class UserAgentInfo {

    private String os;
    private String market;
    private String isp;
    private String client;
    private int appVersion;

    private String ip;

    @JsonIgnore
    public boolean isAndroid() {
        return this.os != null && this.os.startsWith("Android");
    }

    @JsonIgnore
    public boolean isiOS() {
        return this.os != null && this.os.startsWith("iOS");
    }

    @JsonIgnore
    public boolean isH5() {
        return "H5".equalsIgnoreCase(this.os);
    }

    @JsonIgnore
    public boolean isSwagger() {
        return "Swagger".equalsIgnoreCase(this.os);
    }
}
