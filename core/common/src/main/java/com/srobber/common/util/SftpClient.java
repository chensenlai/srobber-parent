package com.srobber.common.util;

import com.jcraft.jsch.*;
import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * SFTP客户端, 提供上传/下载/获取目录等操作
 * SFTPClient client = new SFTPClient(host, port, username, password);
 * client.login();
 * try {
 *  use client download/upload
 * } finally {
 *   client().logout(); //or client().close();
 * }
 *
 * @author chensenlai
 */
@Slf4j
public class SftpClient implements AutoCloseable{

    /**
     * IP地址
     */
    private String host;
    /**
     * 端口, 默认22
     */
    private int port;
    /**
     * 登录名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;

    private ChannelSftp sftp;
    private Session session;

    public SftpClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * 连接服务器
     */
    public void login() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
        } catch (JSchException e) {
            log.error("SFTPClient login error. {}@{}:{} {}", username, host, port, e.getMessage());
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
            sftp.cd(basePath);
        } catch (SftpException e) {
            log.error("SFTPClient upload error. basePath {} not exists", basePath);
            throw new WrapException(e);
        }
        try {
            sftp.cd(uploadDirectory);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String tempPath = basePath;
            String[] dirs = uploadDirectory.split("/");
            for (String dir : dirs) {
                if(dir==null || dir.equals("")) {
                    continue;
                }
                tempPath += "/" + dir;
                try {
                    sftp.cd(tempPath);
                } catch (SftpException e2) {
                    try {
                        sftp.mkdir(tempPath);
                        sftp.cd(tempPath);
                    } catch (SftpException e3) {
                        log.error("SFTPClient upload error. {}{} not exists and create fail", basePath, uploadDirectory);
                        throw new WrapException(e3);
                    }
                }
            }
        }
        try {
            sftp.put(is, uploadFileName);
        } catch (SftpException e) {
            log.error("SFTPClient upload error. {}{} {} {}", basePath, uploadDirectory, uploadFileName, e.getMessage());
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
        try {
            if (directory != null && !directory.equals("")) {
                sftp.cd(directory);
            }
            File file = new File(storeFile);
            sftp.get(downloadFile, new FileOutputStream(file));
        } catch (SftpException | IOException e) {
            log.error("SFTPClient download error. {}{} to {} {}", directory, downloadFile, storeFile, e.getMessage());
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
        try {
            if (directory != null && !directory.equals("")) {
                sftp.cd(directory);
            }
            InputStream is = sftp.get(downloadFile);
            byte[] fileData = IOUtils.toByteArray(is);
            return fileData;
        } catch (SftpException | IOException e) {
            log.error("SFTPClient download error. {}{} {}", directory, downloadFile, e.getMessage());
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
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (SftpException e) {
            log.error("SFTPClient delete error. {}{} {}", directory, deleteFile, e.getMessage());
            throw new WrapException(e);
        }
    }


    /**
     * 列出目录下的文件
     */
    public List<String> listFiles(String directory) {
        List<String> fileList = new ArrayList<>();
        try {
            Vector<?> vector = sftp.ls(directory);
            if(vector != null) {
                for(Object obj : vector) {
                    ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry)obj;
                    fileList.add(lsEntry.getFilename());
                }
            }
        } catch (SftpException e) {
            log.error("SFTPClient listFiles error. {} {}", directory, e.getMessage());
            throw new WrapException(e);
        }
        return  fileList;
    }

    /**
     * 关闭连接
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public void close() throws Exception {
        logout();
    }
}
