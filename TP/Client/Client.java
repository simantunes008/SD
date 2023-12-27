package Client;

import Server.User;
import Server.Task;

import java.net.*;
import java.io.*;
import java.nio.file.*;

public class Client {
    private final Socket socket;
    private final BufferedReader userInput;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Client(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.userInput = new BufferedReader(new InputStreamReader(System.in));
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void start() {
        System.out.println("Escolha uma opção:");
        System.out.println("1. Registrar");
        System.out.println("2. Autenticar");
        System.out.println("3. Enviar pedido de tarefa");
        System.out.println("4. Consultar memória disponível");
        System.out.println("5. Consultar fila de espera");
        System.out.print("Opção: ");

        String str;

        try {
            login();

            System.out.print("Opção: ");
            while ((str = userInput.readLine()) != null) {

                switch (str) {
                    case "3" -> {
                        out.writeInt(Integer.parseInt(str));
                        request();
                    }
                    case "4" -> {
                        out.writeInt(Integer.parseInt(str));
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Memória disponível: " + in.readInt());
                    }
                    case "5" -> {
                        out.writeInt(Integer.parseInt(str));
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Número de tarefas pendentes: " + in.readInt());
                    }
                    default -> System.out.println("Opção inválida.");
                }

                System.out.print("Opção: ");
            }

            socket.shutdownOutput();
            socket.shutdownInput();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String str;
        String username;
        String password;

        try {
            label:
            while ((str = userInput.readLine()) != null) {

                switch (str) {
                    case "1" -> {
                        out.writeInt(Integer.parseInt(str));

                        System.out.print("Insira um username: ");
                        username = userInput.readLine();

                        System.out.print("Insira uma password: ");
                        password = userInput.readLine();

                        User user = new User(username, password);
                        user.serialize(out);

                        if (in.readBoolean()) {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            System.out.println("Resgistro concluído com sucesso!");
                        } else {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            System.out.println("Erro: Username já existe");
                        }
                    }
                    case "2" -> {
                        out.writeInt(Integer.parseInt(str));

                        System.out.print("Insira o seu username: ");
                        username = userInput.readLine();

                        System.out.print("Insira a sua password: ");
                        password = userInput.readLine();

                        User user = new User(username, password);
                        user.serialize(out);

                        if (in.readBoolean()) {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            System.out.println("Autenticação concluída com sucesso!");
                            break label;
                        } else {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            System.out.println("Erro: Username ou password inválidos.");
                        }
                    }
                    case "3", "4", "5" -> {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("É necessário autenticar-se.");
                    }
                    default -> {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Opção inválida.");
                    }
                }

                System.out.print("Opção: ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void request() throws IOException {
        System.out.print("Insira path do arquivo: ");
        String str = userInput.readLine();

        System.out.print("Insira tamanho do pedido: ");
        int memory = Integer.parseInt(userInput.readLine());

        Path path = Paths.get(str);
        byte[] job = Files.readAllBytes(path);

        Task task = new Task(job, memory);
        task.serialize(out);

        if (in.readBoolean()) {
            byte[] output = new byte[in.readInt()];
            in.readFully(output);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Success, returned " + output.length + " bytes!");
        } else {
            int errorCode = in.readInt();
            String errorMessage = in.readUTF();
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Job failed: code = " + errorCode + " message = " + errorMessage);
        }
    }
}
