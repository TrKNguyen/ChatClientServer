package com.facenet.mina.codec;

import com.facenet.mina.object.MessageObject;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
/**
 * decode message
 * @author TranKhoiNguyen
 */
public class XMLDecoder implements ProtocolDecoder {
    /**
     * @param ioSession
     * @param ioBuffer
     * @param protocolDecoderOutput
     * @return
     * @throws Exception
     **/
    @Override
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        System.out.println("XMLDecoder decode");
        byte[] bytes = new byte[ioBuffer.remaining()];
        ioBuffer.get(bytes);
        try {
            MessageObject messageObject = (MessageObject) (this.deserialize(bytes));
            protocolDecoderOutput.write(messageObject);
            return;
        } catch (Exception e) {

        }
        String receivedMessage = new String(bytes);
        protocolDecoderOutput.write(receivedMessage);
    }

    /**
     * @param ioSession
     * @param protocolDecoderOutput
     * @return
     * @throws Exception
     **/
    @Override
    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        System.out.println("XMLDecoder finishDecode");
    }

    /**
     * @param ioSession
     * @return
     * @throws Exception
     **/
    @Override
    public void dispose(IoSession ioSession) throws Exception {
        System.out.println("XMLDecoder dispose");
    }

    /**
     * @param bytes
     * @return
     * @throws Exception
     **/
    private Object deserialize(byte[] bytes) throws Exception {
        try (ByteArrayInputStream a = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream b = new ObjectInputStream(a)) {
                return b.readObject();
            }
        }
    }
}