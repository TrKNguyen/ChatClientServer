package com.facenet.mina.server;

import java.util.*;

import com.facenet.mina.object.MessageObject;
import com.thoughtworks.xstream.XStream;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.example.DateResponse;
/**
 * @author TranKhoiNguyen
 */
/**
 * this class implements a handler for sever
 */
public class TimeServerHandler extends IoHandlerAdapter {
    private Map<String, Integer> validIP = new HashMap<String, Integer>();
    private Set<IoSession> IoSessions = new HashSet<IoSession>();
    private XStream xstream = new XStream();

    public TimeServerHandler() {
        xstream.alias("date-response", DateResponse.class);
    }

    /**
     * @param s
     * @param limitConnect
     * @return
     * @throws
     **/
    public void addUpValidIP(String s, int limitConnect) {
        validIP.put(s, limitConnect);
    }

    /**
     * @param session
     * @param cause
     * @return
     * @throws
     **/
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    /**
     * @param session
     * @param message
     * @return
     * @throws
     **/
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        String messageString = message.toString();
        if (messageString.trim().equalsIgnoreCase("quit")) {
            session.close();
            return;
        }
        if (messageString.equals("Get the local list")) {
            for (IoSession p : IoSessions) {
                session.write(p.getId() + "\n");
            }
            return;
        }
        if (message instanceof String) {
            Date date = new Date();
            session.write(date.toString() + "\n");
            for (IoSession p : IoSessions) {
                if (p.getId() != session.getId()) {
                    p.write("Session " + session.getId() + " write: " + messageString);
                }
            }
            System.out.println(session.getId() + " " +
                    session.getLocalAddress() + " write a message: " + messageString);
        } else {
            MessageObject objectMessage = (MessageObject) (message);
            System.out.println(session.getId() + " " +
                    session.getLocalAddress() + " write a MessageObject: ");
            System.out.println("Sender :" + objectMessage.sender);
            System.out.println("Receiver :" + objectMessage.receiver);
            System.out.println("Time :" + objectMessage.time);
            System.out.println("Content :" + objectMessage.content);
        }
    }

    /**
     * @param session
     * @param status
     * @return
     * @throws
     **/
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.write("Keep Alive");
        System.out.println(session.getId() + " IDLE " + session.getIdleCount(status));
    }

    /**
     * check the limit connection of this ID and check it is in the list of valid ID
     * @param IP
     * @return
     * @throws
     */
    Boolean chkValidIP(String IP) {
        if (validIP.get(IP) != null && validIP.get(IP) != 0) {
            int numberConection = validIP.get(IP) - 1;
            validIP.put(IP, numberConection);
            return true;
        }
        return false;
    }

    /**
     * @param session
     * @return
     * @throws
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // get IP of session.RemoteAddress
        String remoteAddress = (session.getRemoteAddress()).toString();
        for (int i = 0; i < remoteAddress.length(); i++) {
            if (remoteAddress.charAt(i) == ':') {
                remoteAddress = remoteAddress.substring(1, i);
                break;
            }
        }
        if (chkValidIP(remoteAddress)) {
            System.out.println("sessionCreated: " + session.getId() + " "
                    + session.getRemoteAddress().toString());
        } else {
            session.close();
        }
    }

    /**
     * @param session
     * @return
     * @throws
     **/
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        IoSessions.add(session);
        System.out.println("Have new session: " + session.getId() + "|New SocketAddress: "
                + session.getLocalAddress() + " " + session.getRemoteAddress());
    }

    /**
     * @param session
     * @return
     **/
    @Override
    public void sessionClosed(IoSession session) {
        IoSessions.remove(session);
        System.out.println("Quite session: " + session.getId() + "|SocketAddress: "
                + session.getLocalAddress() + " " + session.getRemoteAddress());
    }


}
