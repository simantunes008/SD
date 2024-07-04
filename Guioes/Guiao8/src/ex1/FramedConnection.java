package ex1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ReentrantLock inLock;
    private ReentrantLock outLock;

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.inLock = new ReentrantLock();
        this.outLock = new ReentrantLock();
    }

    public void send(byte[] data) throws IOException {
        try {
            outLock.lock();
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            outLock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        try {
            inLock.lock();
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return data;
        } finally {
            inLock.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
