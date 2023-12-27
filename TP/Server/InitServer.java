package Server;

import java.io.IOException;

public class InitServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server(12345);
        server.start();
    }
}
