package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final DataBase dataBase;
    private final ServerSocket serverSocket;
    List<Handler> threads;

    public Server(int port) throws IOException {
        this.dataBase = new DataBase();
        this.serverSocket = new ServerSocket(port);
        this.threads = new ArrayList<Handler>();
    }

    public void start() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket, dataBase);

                threads.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
