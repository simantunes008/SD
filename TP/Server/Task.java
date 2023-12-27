package Server;

import java.io.*;
import java.util.concurrent.locks.*;

public class Task {
    private final byte[] job;
    private final int memory;
    private final ReadWriteLock rwLock;

    public Task() {
        this.job = null;
        this.memory = 0;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public Task(byte[] job, int memory) {
        this.job = job;
        this.memory = memory;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public byte[] getJob() {
        try {
            this.rwLock.readLock().lock();
            return this.job;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public int getMemory() {
        try {
            this.rwLock.readLock().lock();
            return this.memory;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.job.length);
        out.write(this.job);
        out.writeInt(memory);
    }

    public Task deserialize(DataInputStream in) throws IOException {
        byte[] job = new byte[in.readInt()];
        in.readFully(job);
        int memory = in.readInt();
        return new Task(job, memory);
    }
}
