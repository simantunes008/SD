package Client;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String str;

        System.out.print("Escolha uma opção:\n1. Registrar\n2. Autenticar\nOpção: ");

        try {
            while ((str = userInput.readLine()) != null) {
                out.writeUTF(str);

                if (str.equals("1")) {
                    if (registerUser(userInput, out, in)) {
                        System.out.println("Registro concluído com sucesso!");
                    } else {
                        System.out.println("Erro: Username já existe.");
                    }
                } else if (str.equals("2")) {
                    if (authenticateUser(userInput, out, in)) {
                        System.out.println("Autenticação concluída com sucesso!");

                        while ((str = userInput.readLine()) != null) {
                            out.writeUTF(str);

                            pedido(userInput, out, in);
                        }
                    } else {
                        System.out.println("Erro: Username ou password inválidos.");
                    }
                } else {
                    System.out.println("Opção inválida.");
                }

                System.out.print("Opção: ");
            }

            socket.shutdownOutput();
            socket.shutdownInput();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pedido(BufferedReader userInput, DataOutputStream out, DataInputStream in) throws IOException {
        System.out.print("Insira path do arquivo (ou 'stop' para parar): ");
        String str = userInput.readLine();
        System.out.print("Insira tamanho do pedido: ");
        int memory = Integer.parseInt(userInput.readLine());

        Path path = Paths.get(str);
        byte[] job = Files.readAllBytes(path);
        out.writeInt(job.length);
        out.write(job);
        out.writeInt(memory);

        byte[] output = new byte[in.readInt()];
        in.readFully(output);
        System.err.println("success, returned "+output.length+" bytes");

    }

    private static boolean authenticateUser(BufferedReader userInput, DataOutputStream out, DataInputStream in) throws IOException {
        System.out.print("Insira o seu username: ");
        String username = userInput.readLine();
        System.out.print("Insira a sua password: ");
        String password = userInput.readLine();

        out.writeUTF(username);
        out.writeUTF(password);

        return in.readBoolean();
    }

    private static boolean registerUser(BufferedReader userInput, DataOutputStream out, DataInputStream in) throws IOException {
        System.out.print("Insira um username: ");
        String username = userInput.readLine();
        System.out.print("Insira uma password: ");
        String password = userInput.readLine();

        out.writeUTF(username);
        out.writeUTF(password);

        return in.readBoolean();
    }
}
