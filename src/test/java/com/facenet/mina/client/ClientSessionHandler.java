package com.facenet.mina.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author TranKhoiNguyen
 */
public class ClientSessionHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private boolean finished;

    private IoSession serverWritingSession;

    public boolean isFinished() {
        return finished;
    }

    /**
     * @param serverWritingSession
     * @return
     **/
    public void setServerWritingSessions(IoSession serverWritingSession) {
        this.serverWritingSession = serverWritingSession;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("ClientSessionHandler: sessionOpened");
    }

    /**
     * @param session
     * @return
     **/
    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("ClientSessionHandler: sessionClosed");
    }

    /**
     * @param session
     * @param message
     * @return
     **/
    @Override
    public void messageReceived(IoSession session, Object message) {

        // server only sends ResultMessage. otherwise, we will have to identify
        // its type using instanceof operator.
        // ResultMessage../../../../org/apache/mina/example/sumup/message/ResultMessage.html#ResultMessage">
        String str = message.toString();
        if (str.trim().equalsIgnoreCase("quit")) {
            session.close();
            return;
        }
        if (str.equals("<response>Keep Alive</response>")) {
            return;
        }
        System.out.println("ClientSessionHandler: messageReceived");
        Date date = new Date();
        System.out.println("System (" + session.getId() + ") write a message: " + str);

    }

    /**
     * @param session
     * @param message
     * @return
     * @throws Exception
     **/
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    /**
     * @param session
     * @param cause
     * @return
     **/
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.closeNow();
    }
}