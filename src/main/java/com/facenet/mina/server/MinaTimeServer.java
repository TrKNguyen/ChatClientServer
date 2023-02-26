package com.facenet.mina.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import com.facenet.mina.codec.XMLCodecFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
/**
 * @author TranKhoiNguyen
 */
/**
 * this class implements a server connects to clients
 * to send and receive messages
 */
public class MinaTimeServer {
    private static final int PORT = 1910;

    public static void main(String[] args) throws IOException {
        System.out.println("try: telnet 127.0.0.1 " + PORT);
        // createAcceptor
        IoAcceptor acceptor = new NioSocketAcceptor();
        // setFilter
        acceptor.getFilterChain().addLast("logger",
                new LoggingFilter("TCPServer"));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new XMLCodecFactory()));
        // setHandler
        TimeServerHandler serverHandler = new TimeServerHandler();
        acceptor.setHandler(serverHandler);
        // Configuration
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        // bindPort
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("Server started...");
        // insert valid IP
        System.out.println("Print out the valid IP:");
        Scanner in = new Scanner(System.in);
        System.out.print("Number of valid IP: ");
        int numberValidIP = in.nextInt();
        String validIP = in.nextLine();
        for (int i = 1; i <= numberValidIP; i++) {
            System.out.print("IP: ");
            validIP = in.nextLine();
            System.out.print("limitConnect: ");
            int limitConnect = in.nextInt();
            serverHandler.addUpValidIP(validIP, limitConnect);
        }

    }
}