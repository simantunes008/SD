package ex4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {

    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data) { this.tag = tag; this.data = data; }
    }

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ReentrantLock inLock;
    private ReentrantLock outLock;

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.inLock = new ReentrantLock();
        this.outLock = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        try {
            outLock.lock();
            out.writeInt(tag);
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            outLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        try {
            inLock.lock();
            int tag = in.readInt();
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return new Frame(tag, data);
        } finally {
            inLock.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
