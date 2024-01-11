package Client;

import java.io.IOException;

public class InitClient {
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 9090); // Same port as server
            ClientUI clientUI = new ClientUI(client);
            clientUI.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
