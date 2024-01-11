package Client;

import Server.User;
import Server.Task;

import java.io.*;
import java.net.*;

public class Client implements IClient {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private boolean authenticated;

    public Client(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.authenticated = false;
    }

    public boolean register(User user) throws IOException {
        out.writeInt(1);
        user.serialize(out);
        return in.readBoolean();
    }

    public boolean authenticate(User user) throws IOException {
        out.writeInt(2);
        user.serialize(out);
        authenticated = in.readBoolean();
        return authenticated;
    }

    public byte[] request(Task task) throws IOException {
        if (authenticated) {
            out.writeInt(3);

            task.serialize(out);

            if (in.readBoolean()) {
                byte[] output = new byte[in.readInt()];
                in.readFully(output);
                return output;
            } else {
                int errorCode = in.readInt();
                String errorMessage = in.readUTF();
                throw new RuntimeException("Job failed: code = " + errorCode + ", message = " + errorMessage);
            }
        } else {
            throw new IllegalStateException("É necessário autenticar-se primeiro.");
        }
    }

    public int getAvailableMemory() throws IOException {
        if (authenticated) {
            out.writeInt(4);
            return in.readInt();
        } else {
            throw new IllegalStateException("É necessário autenticar-se primeiro.");
        }
    }

    public int getPendingTasks() throws IOException {
        if (authenticated) {
            out.writeInt(5);
            return in.readInt();
        } else {
            throw new IllegalStateException("É necessário autenticar-se primeiro.");
        }
    }

    public void closeConnection() throws IOException {
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }
}
