package com.snower.msg;

public class Message {

    /**会话ID*/
    private long sessionId;
    /**头部*/
    private Header header;
    /**消息体*/
    private byte[] body;

    public long getSessionId() {
        return sessionId;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public static class Header{

        public static int HEADER_SIZE = 0;

        /**模块号*/
        private int module;
        /**指令号*/
        private int command;
        /**消息长度*/
        private int length;
        /**状态码*/
        private int state  = 200;
        /**加密方式*/
        private byte encryption;


    }

}
