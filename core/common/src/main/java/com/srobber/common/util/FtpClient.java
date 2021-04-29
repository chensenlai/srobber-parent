package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP工具类, 提供上传/下载/获取目录等操作
 * 处理了中文编码问题
 * eg:
 * FTPClient client = new FTPClient(host, port, username, password);
 * try {
 *      //use upload/download
 * } finally {
 *      client.logout(); //or client.close();
 * }
 *
 * @author chensenlai
 */
@Slf4j
public class FtpClient implements AutoCloseable {

    private String host;
    private int port;
    private String username;
    private String password;

    private org.apache.commons.net.ftp.FTPClient ftpClient = null;
    /**
     * FTP协议约定文件路径和文件名都是ISO-8859-1编码
     */
    private String serverCharset = "ISO-8859-1";
    private String localCharset = "GBK";

    public FtpClient(String host, int port,
                     String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 连接服务器
     */
    public void login() {
        ftpClient = new org.apache.commons.net.ftp.FTPClient();
        try {
            ftpClient.connect(this.host, this.port);
            // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，
            // 否则就使用本地编码（GBK）.
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                    "OPTS UTF8", "ON"))) {
                localCharset = "UTF-8";
            }
        } catch (IOException e) {
            log.error("FTPClient {}:{} connect error. {}", this.host, this.port, e.getMessage());
            throw new WrapException(e);
        }
        ftpClient.setControlEncoding(localCharset);

        try {
            ftpClient.login(this.username, this.password);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.error("FTPClient {}@{}:{} login error. code={}", this.username, this.host, this.port, replyCode);
                throw new RuntimeException("login fail");
            }
        } catch (IOException e) {
            log.error("FTPClient {}@{}:{} login error. {}", this.username, this.host, this.port, e.getMessage());
            throw new WrapException(e);
        }
    }


    /**
     * 上传文件到服务器
     * @param basePath          想对路径(必须存在)
     * @param uploadDirectory   上传相对路径(不存在则会新建)
     * @param uploadFileName    上传文件名(如果文件存在则会覆盖)
     * @param is 上传流
     */
    public void upload(String basePath, String uploadDirectory, String uploadFileName,
                       InputStream is) {
        try {
            ftpClient.setFileType(org.apache.commons.net.ftp.FTPClient.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(basePath);
        } catch (IOException e) {
            log.error("FTPClient upload error. basePath {} not exists", basePath);
            throw new WrapException(e);
        }
        try {
            ftpClient.changeWorkingDirectory(uploadDirectory);
        } catch (IOException e) {
            //目录不存在，则创建文件夹
            String tempPath = basePath;
            String[] dirs = uploadDirectory.split("/");
            for (String dir : dirs) {
                if(dir==null || dir.equals("")) {
                    continue;
                }
                tempPath += "/" + dir;
                try {
                    ftpClient.changeWorkingDirectory(tempPath);
                } catch (IOException e2) {
                    try {
                        ftpClient.makeDirectory(tempPath);
                        ftpClient.changeWorkingDirectory(tempPath);
                    } catch (IOException e3) {
                        log.error("FTPClient upload error. {}{} not exists and create fail", basePath, uploadDirectory);
                        throw new WrapException(e3);
                    }
                }
            }
        }
        try {
            ftpClient.storeFile(uploadFileName, is);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("FTPClient upload error. {}{} code={}", basePath, uploadDirectory, replyCode);
                throw new RuntimeException("upload fail");
            }
        } catch (IOException e) {
            log.error("FTPClient upload error. {}{} {}", basePath, uploadDirectory, uploadFileName);
            throw new WrapException(e);
        }
    }

    /**
     * 下载文件保存在本地
     * @param directory     下载目录
     * @param downloadFile  下载的文件名
     * @param storeFile     本地文件
     */
    public void download(String directory, String downloadFile,
                         String storeFile) {
        try (FileOutputStream os = new FileOutputStream(storeFile);) {
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.retrieveFile(downloadFile, os);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("FTPClient download error. {}{} to {} code={}", directory, downloadFile, storeFile, replyCode);
                throw new RuntimeException("download fail");
            }
        } catch (IOException e) {
            log.error("FTPClient download error. {}{} {} {}", directory, downloadFile, storeFile, e.getMessage());
            throw new WrapException(e);
        }
    }

    /**
     * 下载文件
     * @param directory    下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String downloadFile) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            ftpClient.changeWorkingDirectory(directory);
            ftpClient.retrieveFile(downloadFile, os);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.warn("FTPClient download error. {}{} code={}", directory, downloadFile, replyCode);
                throw new RuntimeException("download fail");
            }
            return os.toByteArray();
        } catch (IOException e) {
            log.error("FTPClient download error. {}{} {}", directory, downloadFile, e.getMessage());
            throw new WrapException(e);
        }
    }

    /**
     * 删除文件
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) {
        try {
            ftpClient.changeWorkingDirectory(directory);
            int replyCode = ftpClient.dele(deleteFile);
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.error("FTPClient delete error. {}{} code={}", directory, deleteFile, replyCode);
                throw new RuntimeException("delete fail");
            }
        } catch (IOException e) {
            log.info("FTPClient delete error. {}{}", directory, deleteFile, e.getMessage());
            throw new WrapException(e);
        }
    }

    /**
     * 列出目录下的文件
     */
    public List<String> listFiles(String directory) {
        List<String> fileList = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(directory);
            if(ftpFiles != null) {
                for(FTPFile ftpFile : ftpFiles) {
                    fileList.add(ftpFile.getName());
                }
            }
        } catch (IOException e) {
            log.error("FTPClient listFiles error. {} {}", directory, e.getMessage());
            throw new WrapException(e);
        }
        return  fileList;
    }

    /**
     * 关闭连接
     */
    public void logout() {
        if(ftpClient != null) {
            try {
                ftpClient.logout();
            } catch (IOException e) {}
        }
    }

    @Override
    public void close() throws Exception {
        logout();
    }
}
