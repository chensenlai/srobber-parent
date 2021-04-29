package com.srobber.manager.oss;

import java.io.InputStream;

/**
 * OSS对象存储管理器
 *
 * @author chensenlai
 */
public interface OssManager {

    /**
     * 上传文件
     * @param bucketName 存储空间名
     * @param objectName 对象名, 支持 /test/xxx.png
     * @param inputStream 上传文件流
     */
    void putObject(String bucketName, String objectName, InputStream inputStream);
}
