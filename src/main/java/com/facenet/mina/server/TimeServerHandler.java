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

        String str = message.toString();
        if (str.trim().equalsIgnoreCase("quit")) {
            session.close();
            return;
        }
        if (str.equals("Get the local list")) {
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
                    p.write("Session " + session.getId() + " write: " + str);
                }
            }
            System.out.println(session.getId() + " " + session.getLocalAddress() + " write a message: " + str);
        } else {
            MessageObject now = (MessageObject) (message);
            System.out.println(session.getId() + " " + session.getLocalAddress() + " write a MessageObject: ");
            System.out.println("Sender :" + now.sender);
            System.out.println("Receiver :" + now.receiver);
            System.out.println("Time :" + now.time);
            System.out.println("Content :" + now.content);
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
     * @param s
     * @return
     * @throws
     */
    Boolean chkValidIP(String s) {
        if (validIP.get(s) != null && validIP.get(s) != 0) {
            int t = validIP.get(s) - 1;
            validIP.put(s, t);
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
        String s = (session.getRemoteAddress()).toString();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ':') {
                s = s.substring(1, i);
                break;
            }
        }
        if (chkValidIP(s)) {
            System.out.println("sessionCreated: " + session.getId() + " " + session.getRemoteAddress().toString());
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
        System.out.println("Have new session: " + session.getId() + "|New SocketAddress: " + session.getLocalAddress() + " " + session.getRemoteAddress());
    }

    /**
     * @param session
     * @return
     **/
    @Override
    public void sessionClosed(IoSession session) {
        IoSessions.remove(session);
        System.out.println("Quite session: " + session.getId() + "|SocketAddress: " + session.getLocalAddress() + " " + session.getRemoteAddress());
    }


}
