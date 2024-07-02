package ex4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EchoServer {

    private static class DataBase {
        int sum = 0;
        int count = 0;
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        void add(int value) {
            try {
                readWriteLock.writeLock().lock();
                sum += value;
                count++;
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
        int average() {
            try {
                readWriteLock.readLock().lock();
                if (count == 0)
                    return 0;
                return sum/count;
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }

    private static class ServerThread implements Runnable {
        Socket socket;
        DataBase dataBase;
        ServerThread(Socket socket, DataBase dataBase) {
            this.socket = socket;
            this.dataBase = dataBase;
        }
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                int sum = 0;

                String line;
                while ((line = in.readLine()) != null) {
                    int value = Integer.parseInt(line);
                    dataBase.add(value);
                    sum += value;
                    out.println(sum);
                    out.flush();
                }

                out.println(dataBase.average());
                out.flush();

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            DataBase db = new DataBase();

            while (true) {
                Socket socket = ss.accept();
                Thread worker = new Thread(new ServerThread(socket, db));
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
