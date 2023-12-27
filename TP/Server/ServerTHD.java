package Server;

import java.net.*;
import java.io.*;
import sd23.*;

public class ServerTHD extends Thread {
    private final DataBase dataBase;
    private final MemoryManager memoryManager;
    private final TaskManager taskManager;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ServerTHD(Socket clientSocket, DataBase dataBase, MemoryManager memoryManager, TaskManager taskManager) throws IOException {
        this.socket = clientSocket;
        this.memoryManager = memoryManager;
        this.taskManager = taskManager;
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
                        User user = new User();
                        user = user.deserialize(in);

                        String username = user.getUsername();

                        if (!dataBase.isUser(username)) {
                            dataBase.putUser(username, user);
                            out.writeBoolean(true);
                        } else {
                            out.writeBoolean(false);
                        }

                    }
                    case 2 -> {
                        User user = new User();
                        user = user.deserialize(in);

                        String username = user.getUsername();
                        String password = user.getPassword();

                        out.writeBoolean(dataBase.authenticateUser(username, password));
                    }
                    case 3 -> {
                        Task task = new Task();
                        task = task.deserialize(in);

                        byte[] job = task.getJob();
                        int memory = task.getMemory();

                        taskManager.incrementPendingTasks();
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
                        taskManager.decrementPendingTasks();
                    }
                    case 4 -> {
                        out.writeInt(memoryManager.getSystemMemory());
                    }
                    case 5 -> {
                        out.writeInt(taskManager.getPendingTasks());
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
