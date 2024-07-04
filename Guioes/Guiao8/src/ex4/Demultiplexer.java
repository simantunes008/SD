package ex4;

import ex4.TaggedConnection.Frame;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {

    private class Messages {
        Condition condition = lock.newCondition();
        Deque<byte[]> data = new ArrayDeque<byte[]>();
    }

    private TaggedConnection conn;
    private Map<Integer, Messages> map;
    private ReentrantLock lock;

    public Demultiplexer(TaggedConnection conn) {
        this.conn = conn;
        this.map = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        conn.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        conn.send(tag, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        try {
            lock.lock();
            Frame frame = conn.receive();
            Messages messages1 = map.get(frame.tag);
            if (messages1 == null) {
                messages1 = new Messages();
                map.put(frame.tag, messages1);
            }
            messages1.data.add(frame.data);
            messages1.condition.signalAll();

            if (frame.tag == tag)
                return messages1.data.remove();

            Messages messages2 = map.get(tag);
            if (messages2 == null) {
                messages2 = new Messages();
                map.put(tag, messages2);
            }
            while (messages2.data.isEmpty())
                messages2.condition.await();
            return messages2.data.remove();
        } finally {
            lock.unlock();
        }
    }

    public void close() throws IOException {
        conn.close();
    }

}
