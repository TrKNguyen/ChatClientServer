package com.facenet.mina.codec;

import com.facenet.mina.object.MessageObject;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
/**
 * @author TranKhoiNguyen
 */
public class XMLEncoder implements ProtocolEncoder {
    /**
     * @param ioSession
     * @param message
     * @param protocolEncoderOutput
     * @return
     * @throws Exception
     **/
    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        System.out.println("XMLEncoder encode");
        // case message is String
        if (message instanceof String) {
            String xmlData = "<response>" + (String) message + "</response>";
            System.out.println("sending message: ");
            System.out.println("****************");
            System.out.println(xmlData);
            System.out.println("****************");
            IoBuffer buffer = IoBuffer.allocate(xmlData.getBytes().length, false);
            buffer.put(xmlData.getBytes());
            buffer.flip();
            protocolEncoderOutput.write(buffer);
        } else {
            assert (message instanceof MessageObject);
            try (ByteArrayOutputStream a = new ByteArrayOutputStream()) {
                try (ObjectOutputStream b = new ObjectOutputStream(a)) {
                    b.writeObject(message);
                }
                IoBuffer buffer = IoBuffer.allocate(a.size(), false);
                buffer.put(a.toByteArray());
                buffer.flip();
                protocolEncoderOutput.write(buffer);
            }

        }
    }

    /**
     * @param ioSession
     * @return
     * @throws Exception
     **/
    @Override
    public void dispose(IoSession ioSession) throws Exception {
        System.out.println("XMLEncoder dispose");
    }
}