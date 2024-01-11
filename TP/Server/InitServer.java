package Server;

import java.io.IOException;

public class InitServer {
    public static void main(String[] args) {
        try {
            Server server = new Server(9090); // Port number is arbitrary
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
