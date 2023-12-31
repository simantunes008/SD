package Server;

import java.io.*;
import java.util.*;
import java.security.*;
import java.util.concurrent.locks.*;

public class Task {
    private final int id;
    private final byte[] job;
    private final int memory;
    private final ReadWriteLock rwLock;

    public Task() {
        this.id = generateId(null);
        this.job = null;
        this.memory = 0;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public Task(byte[] job, int memory) {
        this.id = generateId(job);
        this.job = job;
        this.memory = memory;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public Task(int id, byte[] job, int memory) {
        this.id = id;
        this.job = job;
        this.memory = memory;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public int getId() {
        try {
            this.rwLock.readLock().lock();
            return this.id;
        } finally {
            this.rwLock.readLock().unlock();
        }
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

    private int generateId(byte[] job) {
        if (job == null) {
            return 0;
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(job);
                return Arrays.hashCode(hash);
            } catch (NoSuchAlgorithmException e) {
                return 0;
            }
        }
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.id);
        out.writeInt(this.job.length);
        out.write(this.job);
        out.writeInt(memory);
    }

    public Task deserialize(DataInputStream in) throws IOException {
        int id = in.readInt();
        byte[] job = new byte[in.readInt()];
        in.readFully(job);
        int memory = in.readInt();
        return new Task(id, job, memory);
    }
}
