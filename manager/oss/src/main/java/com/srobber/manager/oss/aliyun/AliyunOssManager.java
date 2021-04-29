package com.srobber.manager.oss.aliyun;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.srobber.common.util.StringUtil;
import com.srobber.manager.oss.OssException;
import com.srobber.manager.oss.OssManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;

/**
 * 阿里云OSS客户端
 *
 * @author chensenlai
 */
@Slf4j
@Data
public class AliyunOssManager implements OssManager, InitializingBean {

    /**
     * Endpoint以杭州为例，其它Region请按实际情况填写。
     * https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;
    /**
     * 阿里云主账号
     * 服务端直接用主帐号访问OSS
     * 客户端需要用STS临时授权访问OSS
     */
    private String accessKeyId;
    private String accessKeySecret;


    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        OSS ossClient = new OSSClientBuilder().build(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrorCode();
            String errorMessage = e.getErrorMessage();
            log.error("OSS putObject error, {} {} {}", requestId, errorCode, errorMessage, e);
            throw new OssException("OSS上传失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        } catch (ClientException e) {
            String requestId = e.getRequestId();
            String errorCode = e.getErrorCode();
            String errorMessage = e.getErrorMessage();
            log.error("OSS putObject error, {} {} {}", requestId, errorCode, errorMessage, e);
            throw new OssException("OSS上传失败,错误码:"+errorCode+",错误消息:"+errorMessage);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if(StringUtil.isBlank(this.endpoint)) {
            throw new IllegalStateException("OSS endpoint is blank");
        }
        if(StringUtil.isBlank(this.accessKeyId)) {
            throw new IllegalStateException("OSS accessKeyId is blank");
        }
        if(StringUtil.isBlank(this.accessKeySecret)) {
            throw new IllegalStateException("OSS accessKeySecret is blank");
        }
    }
}
