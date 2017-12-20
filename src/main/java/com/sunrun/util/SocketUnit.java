package com.sunrun.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunrun.mpos.common.utils.PropertyUtils;
import com.sunrun.mpos.common.utils.StringUtils;

public class SocketUnit {
    
    /** 日志 */
	private static final Logger logger = LoggerFactory.getLogger(SocketUnit.class);
    
    private Socket socket = null;

    public SocketUnit(String ip, int port) {
        try {
        	logger.info("ip:"+ip+","+"port:"+port);
            socket = new Socket(ip, port);
            socket.setSoLinger(true, 5);
        } catch (Exception e) {
            try {
                throw new Exception(e);
            } catch (Exception e1) {
            	logger.error(e1.getMessage(),e1);
            }
        }
    }
    
    public SocketUnit(String ip, int port , int outTime) {
        try {
        	logger.info("ip:"+ip+","+"port:"+port+","+"outTime:"+outTime);
            socket = new Socket(ip, port);
            socket.setSoTimeout(outTime);
            socket.setSoLinger(true, 5);
        } catch (Exception e) {
        	logger.error("创建SOCKET通讯连接失败：" , e);
        }
    }
    

    public String sendAndRec(String sendMsg, int msgLengthPosition, boolean isAllLength) throws Exception {
        String recXml = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
             //将报文发出
            byte[] writeBytes = sendMsg.getBytes("utf-8");
            outputStream.write(writeBytes);
            outputStream.flush();
             //接收报文
            StringBuffer bf = new StringBuffer();
            reader = new InputStreamReader(inputStream,"utf-8");
            int c = 0;
            while ((c = reader.read()) != -1) {
                bf.append((char) c);
                String tmp = bf.toString();
                int len = tmp.getBytes("utf-8").length;
                if (len >= msgLengthPosition) {
                    String msgPos = tmp.substring(0, msgLengthPosition);
                    int msgLen = Integer.parseInt(msgPos);
                    if (!isAllLength) {
                        msgLen += msgLengthPosition;
                    }
                    if (len == msgLen) {
                        recXml = bf.toString();
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        return recXml;
    }

    
    public String sendAndRec(String sendMsg, int msgLengthPosition, boolean isAllLength, String encoding) throws Exception {
        String recXml = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
             //将报文发出
            byte[] writeBytes = sendMsg.getBytes(encoding);
            outputStream.write(writeBytes);
            outputStream.flush();
             //接收报文
            StringBuffer bf = new StringBuffer();
            reader = new InputStreamReader(inputStream,encoding);
            int c = 0;
            while ((c = reader.read()) != -1) {
                bf.append((char) c);
                String tmp = bf.toString();
                int len = tmp.getBytes(encoding).length;
                if (len >= msgLengthPosition) {
                    String msgPos = tmp.substring(0, msgLengthPosition);
                    int msgLen = Integer.parseInt(msgPos);
                    if (!isAllLength) {
                        msgLen += msgLengthPosition;
                    }
                    if (len == msgLen) {
                        recXml = bf.toString();
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }

        return recXml;
    }
    
    
    public String sendAndRecFixLength(String sendMsg, int msgLengthPosition, String encoding) throws Exception {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
        	logger.debug("发送内容：" + sendMsg + "||报文长度位：" + msgLengthPosition + "编码： " + encoding);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
             //将报文发出
            byte[] writeBytes = sendMsg.getBytes(encoding);
            outputStream.write(writeBytes);
            outputStream.flush();
            logger.debug("发送报文完成.");
             //接收报文
            StringBuffer bf = new StringBuffer();
            reader = new InputStreamReader(inputStream,encoding);
            
            /*
             * 先读取msgLengthPosition 位长度位
             */
            int c = 0;
            for(int i = 0; i<msgLengthPosition; i++){
                c = reader.read();
                bf.append((char) c);
            }
            logger.info("接收到批量返回报文的长度：" + bf);
            int msgLen = Integer.parseInt(bf.toString());
            /*
             * 再读取msgLen个长度的报文正文
             */
            bf = new StringBuffer();
            for (int i = 0; i<msgLen; i++) {
                c = reader.read();
                bf.append((char) c);
            }
            logger.info("接收到批量返回报文的正文：" + bf);
            return bf.toString();
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            throw new Exception(e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
    }
    
    /**
     * 二飞处读取baowen
     * @param sendMsg
     * @param msgLengthPosition
     * @param isAllLength
     * @param encode
     * @return
     * @throws Exception
     */
    public String sendAndRec(String sendMsg) throws Exception {
        String recXml = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            // 将报文发出
            byte[] writeBytes = sendMsg.getBytes("GBK");
            outputStream.write(writeBytes);
            outputStream.flush();
            //接收报文固定长度
            int array=19;
            byte[] arrays=new  byte[array];
            inputStream.read(arrays,0,array);
            recXml=new String(arrays);
            logger.info("接收到批量返回报文的正文：" + recXml);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        return recXml;
    }
    
    public  static String makeSendMsg(String msg, int contentLen) throws Exception {
        StringBuffer sendMsg = new StringBuffer();
        String s = "";
        int msgLen = msg.getBytes("gbk").length;
        sendMsg.append(StringUtils.leftPad(String.valueOf(msgLen), contentLen, '0'));
        sendMsg.append(msg);
        logger.info("send:" + sendMsg.toString());
        SocketUnit sc = new SocketUnit(PropertyUtils.getValue("XYH_IP"), Integer.parseInt(PropertyUtils.getValue("XYH_PORT")), Integer.parseInt(PropertyUtils.getValue("XYH_TIMEOUT")));
//        SocketUnit sc = new SocketUnit("22.32.102.184", 56002, 60000);
        s = sc.sendAndRec(sendMsg.toString(), contentLen, false, "gbk");
        String result =s.substring(contentLen);
        logger.info("return:" + s);
        return result;
    }
    
    public  static String makeSendMsg(String msg, int contentLen,String ip,int port,String timeout) throws Exception {
    	 StringBuffer sendMsg = new StringBuffer();
         String s = "";
         int msgLen = msg.getBytes("gbk").length;
         sendMsg.append(StringUtils.leftPad(String.valueOf(msgLen), contentLen, '0'));
         sendMsg.append(msg);
         logger.info("send:" + sendMsg.toString());
         SocketUnit sc = null;
         if(StringUtils.isEmpty(ip)||StringUtils.isEmpty(String.valueOf(port))){
        	 sc = new SocketUnit(PropertyUtils.getValue("XYH_IP"), Integer.parseInt(PropertyUtils.getValue("XYH_PORT")), Integer.parseInt(PropertyUtils.getValue("XYH_TIMEOUT")));
         }else{
        	 sc = new SocketUnit(ip, port, Integer.parseInt(timeout));
         }
         s = sc.sendAndRec(sendMsg.toString(), contentLen, false, "gbk");
         String result =s.substring(contentLen);
         logger.info("return:" + s);
         return result;
    }

}
