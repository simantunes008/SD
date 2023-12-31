package Client;

import java.io.IOException;

public class InitClient {
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 12345);
            ClientInterface clientInterface = new ClientInterface(client);
            clientInterface.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
