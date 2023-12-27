package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private final DataBase dataBase;
    private final MemoryManager memoryManager;
    private final TaskManager taskManager;
    private final ServerSocket serverSocket;
    List<ServerTHD> threads;

    public Server(int port) throws IOException {
        this.dataBase = new DataBase();
        this.memoryManager = new MemoryManager(1024);
        this.taskManager = new TaskManager();
        this.serverSocket = new ServerSocket(port);
        this.threads = new ArrayList<>();
    }

    public void start() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ServerTHD handler = new ServerTHD(socket, dataBase, memoryManager, taskManager);

                threads.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (ServerTHD thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
