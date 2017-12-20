/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.sunrun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.StringUtils;

public class SftpUtils {
    private String host;
    private int port;
    private String username;
    private String password;
    private ChannelSftp sftp = null;
    private Session sshSession = null;
    private Channel channel = null;
    private static final Logger logger = LoggerFactory.getLogger(SftpUtils.class);

    public SftpUtils() {
    }

    public SftpUtils(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void connect() throws Exception {
        try {
            JSch jsch = new JSch();
            jsch.getSession(this.username, this.host, this.port);
            this.sshSession = jsch.getSession(this.username, this.host, this.port);
            System.out.println("Session created.");
            this.sshSession.setPassword(this.password);
            if (this.password != null) {
                this.sshSession.setPassword(this.password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            this.sshSession.setConfig(sshConfig);
            this.sshSession.setTimeout(10000);
            this.sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            this.channel = this.sshSession.openChannel("sftp");
            this.channel.connect();
            this.sftp = ((ChannelSftp) this.channel);
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接成功!-----");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接失败!-----");
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    public void connectRSA() throws Exception {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(PropertyUtils.getValue("com.sunrun.bill.sftp.rsa"));
            this.sshSession = jsch.getSession(this.username, this.host, this.port);
            UserInfo userinfo = new MyUserInfo(this.password);
            Properties sshConfig = new Properties();
            this.sshSession.setTimeout(10000);
            this.sshSession.setUserInfo(userinfo);
            this.sshSession.connect();
            this.channel = this.sshSession.openChannel("sftp");
            this.channel.connect();
            this.sftp = ((ChannelSftp) this.channel);
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接成功!-----");
        } catch (Exception e) {
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接失败!-----");
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    public void connectRSA(int timeout) throws Exception {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(PropertyUtils.getValue("com.sunrun.bill.sftp.rsa"));
            this.sshSession = jsch.getSession(this.username, this.host, this.port);
            UserInfo userinfo = new MyUserInfo(this.password);
            Properties sshConfig = new Properties();
            this.sshSession.setTimeout(timeout);
            this.sshSession.setUserInfo(userinfo);
            this.sshSession.connect();
            this.channel = this.sshSession.openChannel("sftp");
            this.channel.connect();
            this.sftp = ((ChannelSftp) this.channel);
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接成功!-----");
        } catch (Exception e) {
            logger.info("-----sftp [" + this.host + ":" + this.port + "] 连接失败!-----");
            logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    public void upload(String directory, String uploadFile) {
        try {
            if (!recursiveCreateDir(directory)) {
                throw new Exception("创建目录" + directory + "异常");
            }

            this.sftp.cd(directory);
            File file = new File(uploadFile);
            if (file.exists()) {
                this.sftp.put(new FileInputStream(file), file.getName());
            }
            logger.info("-----sftp 文件[" + uploadFile + "] 上传到  [" + directory + " ] 成功!-----");
        } catch (Exception e) {
            logger.error("-----sftp 文件[" + uploadFile + "] 上传到  [" + directory + " ] 失败!-----");
            logger.error(e.getMessage(), e);
        }
    }

    private Boolean recursiveCreateDir(String directory) throws SftpException {

        if (StringUtils.isBlank(directory)) {
            logger.info("目录为空");
            return false;
        }

        if ("/".equals(directory)) {
            return true;
        }
        // 去除末尾/
        directory = directory.replaceAll("/$", "");

        LinkedList<String> pathList = new LinkedList<String>();

        for (;;) {
            try {
                this.sftp.mkdir(directory);
                logger.info("创建目录:{}", directory);
                break;
            } catch (Exception e) {
                logger.info("创建目录异常:{}", directory);
                pathList.addFirst(directory);
            }
            directory = directory.substring(0, directory.lastIndexOf("/"));
        }
        for (String path : pathList) {
            this.sftp.mkdir(path);
            logger.info("创建目录:{}", path);
        }

        return true;
    }

    public Boolean downloadAndSave(String downloadPath, String downloadFile, String saveFilePath, String saveFileName) {
        File saveFile = new File(saveFilePath);
        saveFile.mkdirs();
        File file = new File(saveFilePath + saveFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            this.sftp.cd(downloadPath);
            this.sftp.get(downloadFile, fos);
            logger.info("-----sftp 文件[" + downloadPath + downloadFile + "] 下载成功!-----");
            Boolean localBoolean = Boolean.TRUE;

            return localBoolean;
        } catch (Exception e) {
            logger.info("-----sftp 文件[" + downloadPath + downloadFile + "] 下载失败!-----");
            logger.error(e.getMessage(), e);

            return Boolean.FALSE;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
        }
    }

    public Boolean checkFileExists(String filePath, String fileName) {
        Boolean fileExistsFlag = Boolean.FALSE;
        try {
            this.sftp.cd(filePath);
            this.sftp.get(fileName);
            fileExistsFlag = Boolean.TRUE;
            logger.info("-----sftp 文件[" + filePath + fileName + "] 存在!-----");
        } catch (SftpException e) {
            logger.info("-----sftp 文件[" + filePath + fileName + "] 不存在!-----");
        }
        return fileExistsFlag;
    }

    public void delete(String directory, String deleteFile) {
        try {
            this.sftp.cd(directory);
            this.sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector listFiles(String directory) throws SftpException {
        return this.sftp.ls(directory);
    }

    public void logOut() {
        if ((this.sftp != null) && (this.sftp.isConnected())) {
            this.sftp.disconnect();
            logger.info("-----sftp [" + this.host + ":" + this.port + "]  已登出!-----");
        }

        if ((this.sshSession != null) && (this.sshSession.isConnected())) {
            this.sshSession.disconnect();
            logger.info("-----sshSession [" + this.host + ":" + this.port + "]  已登出!-----");
        }
        if (this.channel != null)
            this.channel.disconnect();
    }

    public InputStream getFile(String directory) throws SftpException {
        return this.sftp.get(directory);
    }

    public static void main(String[] args) {
        String host = "192.168.110.1";
        int port = 1688;
        String username = "admin";
        String password = "admin";
        SftpUtils sf = new SftpUtils(host, port, username, password);
        try {
            sf.connect();
        } catch (Exception e) {
        }
        String directory = "./sftp";
        String downloadFile = "CommissionPaymentBill_20161229.csv";
        String saveFile = "F:/sftp/20161227/down/CommissionPaymentBill_20161229.csv";
        try {
            System.out.println("finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MyUserInfo implements UserInfo {
        private String passphrase = null;

        public MyUserInfo(String passphrase) {
            this.passphrase = passphrase;
        }

        public String getPassphrase() {
            return this.passphrase;
        }

        public String getPassword() {
            return null;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public void showMessage(String message) {
            System.out.println(message);
        }
    }
}