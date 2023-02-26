package com.facenet.mina.object;
/**
 * implements serializable message data object
 * @author TranKhoiNguyen
 */
public class MessageObject extends java.lang.Object implements java.io.Serializable {
    public String sender, receiver, time, content;

    /**
     * @param sender
     * @param receiver
     * @param time
     * @param content
     * @return
     **/
    public MessageObject(String sender, String receiver, String time, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.content = content;
    }
}
