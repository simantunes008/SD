package Server;

import java.net.*;
import java.io.*;
import sd23.*;

public class Handler extends Thread {
    private final DataBase dataBase;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Handler(Socket clientSocket, DataBase dataBase) throws IOException {
        this.socket = clientSocket;
        this.dataBase = dataBase;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String str;
            while (true) {
                try {
                    str = in.readUTF();
                } catch (EOFException e) {
                    break;
                }

                if (str.equals("1")) {
                    String username = in.readUTF();
                    String password = in.readUTF();

                    if (!dataBase.isUser(username)) {
                        User user = new User(username, password);
                        dataBase.setUsers(username, user);
                        out.writeBoolean(true);
                    } else {
                        out.writeBoolean(false);
                    }

                } else if (str.equals("2")) {
                    String username = in.readUTF();
                    String password = in.readUTF();

                    out.writeBoolean(dataBase.authenticateUser(username, password));
                } else if (str.equals("3")) {
                    try {
                        byte[] job = new byte[in.readInt()];
                        in.readFully(job);
                        int memory = in.readInt();

                        byte[] output = JobFunction.execute(job);

                        out.writeInt(output.length);
                        out.write(output);
                    } catch (JobFunctionException e) {
                        out.writeInt(e.getCode());
                        out.writeUTF(e.getMessage());

                        System.err.println("job failed: code="+e.getCode()+" message="+e.getMessage());
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
