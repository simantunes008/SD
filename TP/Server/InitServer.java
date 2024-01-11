package Server;

import java.io.IOException;

public class InitServer {
    public static void main(String[] args) {
        try {
            Server server = new Server(9090); // O número de porta é arbitrário
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
