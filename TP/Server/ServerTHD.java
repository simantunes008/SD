package Server;

import java.net.*;
import java.io.*;
import sd23.*;

public class ServerTHD extends Thread {
    private final DataBase dataBase;
    private final MemoryManager memoryManager;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ServerTHD(Socket clientSocket, DataBase dataBase, MemoryManager memoryManager) throws IOException {
        this.socket = clientSocket;
        this.memoryManager = memoryManager;
        this.dataBase = dataBase;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            int request;
            while (true) {
                try {
                    request = in.readInt();
                } catch (EOFException e) {
                    break;
                }

                switch (request) {
                    case 1 -> {
                        String username = in.readUTF();
                        String password = in.readUTF();

                        if (!dataBase.isUser(username)) {
                            User user = new User(username, password);
                            dataBase.putUser(username, user);
                            out.writeBoolean(true);
                        } else {
                            out.writeBoolean(false);
                        }

                    }
                    case 2 -> {
                        String username = in.readUTF();
                        String password = in.readUTF();

                        out.writeBoolean(dataBase.authenticateUser(username, password));
                    }
                    case 3 -> {
                        byte[] job = new byte[in.readInt()];
                        in.readFully(job);
                        int memory = in.readInt();

                        try {
                            memoryManager.allocateMemory(memory);

                            try {
                                byte[] output = JobFunction.execute(job);

                                out.writeBoolean(true);
                                out.writeInt(output.length);
                                out.write(output);
                            } catch (JobFunctionException e) {
                                out.writeBoolean(false);
                                out.writeInt(e.getCode());
                                out.writeUTF(e.getMessage());
                            }

                            memoryManager.releaseMemory(memory);

                        } catch (IllegalArgumentException e) {
                            out.writeBoolean(false);
                            out.writeInt(0);
                            out.writeUTF(e.getMessage());
                        }
                    }
                    case 4 -> {
                        out.writeInt(memoryManager.getSystemMemory());
                    }
                }
            }

            socket.shutdownOutput();
            socket.shutdownInput();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
