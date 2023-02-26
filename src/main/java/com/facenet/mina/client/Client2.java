package com.facenet.mina.client;

import java.util.*;

import com.facenet.mina.codec.XMLCodecFactory;
import com.facenet.mina.object.MessageObject;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * @author TranKhoiNguyen
 */
/**
 * this class implements a chat client number 2 that connects to the server
 * to send and receive messages
 */
public class Client2 {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 1910;
    private static final long CONNECT_TIMEOUT = 30 * 1000L; // 30 seconds
    // Set this to false to use object serialization instead of custom codec.
    private static final boolean USE_CUSTOM_CODEC = false;

    public static void main(String[] args) throws Throwable {
        // prepare values to sum up
        NioSocketConnector connector = new NioSocketConnector();
        // Configure the service.
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        // setFilter
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new XMLCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter("TCP Server"));
        // setHandler
        connector.setHandler(new ClientSessionHandler());
        // tryConnectToServer
        IoSession session;
        while (true) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
                future.awaitUninterruptibly();
                session = future.getSession();
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect.");
                e.printStackTrace();
                Thread.sleep(5000);
            }
        }
        while (session.isActive()) {

            Scanner in = new Scanner(System.in);
            int type = in.nextInt();
            if (type == 1) {
                // case 1 : String
                String message = in.nextLine();
                message = "";
                int numberMessage = in.nextInt();
                for (int i = 1; i <= numberMessage; i++) {
                    String nextMessage = in.nextLine();
                    if (message.isEmpty()) message = nextMessage;
                    else message = message + " " + nextMessage;
                }
                session.write(message);
            } else {
                // case 2 : Object
                String getEmptyLine = in.nextLine();
                String sender = in.nextLine();
                String receiver = in.nextLine();
                String time = in.nextLine();
                String content = in.nextLine();
                MessageObject messageObject = new MessageObject(sender, receiver, time, content);
                session.write(messageObject);
            }
        }
        // wait until the summation is done
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

}

