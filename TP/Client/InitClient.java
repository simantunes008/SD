package Client;

import java.io.IOException;

public class InitClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 12345);
        client.start();
    }
}
