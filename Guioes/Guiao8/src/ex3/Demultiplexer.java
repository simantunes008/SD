package ex3;

import ex3.TaggedConnection.Frame;

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

    public void start() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Frame frame = conn.receive();
                    try {
                        lock.lock();
                        Messages messages = map.get(frame.tag);
                        if (messages == null) {
                            messages = new Messages();
                            map.put(frame.tag, messages);
                        }
                        messages.data.add(frame.data);
                        messages.condition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException e) {
                try {
                    lock.lock();
                    map.forEach((k, v) -> v.condition.signalAll());
                } finally {
                    lock.unlock();
                }
            }
        });
        t.start();
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
            Messages messages = map.get(tag);
            if (messages == null) {
                messages = new Messages();
                map.put(tag, messages);
            }
            while (messages.data.isEmpty())
                messages.condition.await();
            return messages.data.remove();
        } finally {
            lock.unlock();
        }
    }

    public void close() throws IOException {
        conn.close();
    }

}
