package Client;

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
        System.out.print("Opção: ");

        String str;

        try {
            login();

            System.out.print("Opção: ");
            while ((str = userInput.readLine()) != null) {

                if (str.equals("3")) {
                    out.writeInt(Integer.parseInt(str));
                    request();
                } else if (str.equals("4")) {
                    out.writeInt(Integer.parseInt(str));
                    System.out.println("Memória disponível: " + in.readInt());
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

    private void login() {
        String str;

        try {
            while ((str = userInput.readLine()) != null) {

                if (str.equals("1")) {
                    out.writeInt(Integer.parseInt(str));

                    System.out.print("Insira um username: ");
                    str = userInput.readLine();
                    out.writeUTF(str);

                    System.out.print("Insira uma password: ");
                    str = userInput.readLine();
                    out.writeUTF(str);

                    if (in.readBoolean()) {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Resgistro concluído com sucesso!");
                    } else {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Erro: Username já existe");
                    }
                } else if (str.equals("2")) {
                    out.writeInt(Integer.parseInt(str));

                    System.out.print("Insira o seu username: ");
                    str = userInput.readLine();
                    out.writeUTF(str);

                    System.out.print("Insira a sua password: ");
                    str = userInput.readLine();
                    out.writeUTF(str);

                    if(in.readBoolean()) {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Autenticação concluída com sucesso!");
                        break;
                    } else {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        System.out.println("Erro: Username ou password inválidos.");
                    }
                } else if (str.equals("3") || str.equals("4") || str.equals("5")) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    System.out.println("É necessário autenticar-se.");
                } else {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    System.out.println("Opção inválida.");
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

        out.writeInt(job.length);
        out.write(job);
        out.writeInt(memory);

        if (in.readBoolean()) {
            byte[] output = new byte[in.readInt()];
            in.readFully(output);
            System.err.println("success, returned " + output.length + " bytes");
        } else {
            int errorCode = in.readInt();
            String errorMessage = in.readUTF();
            System.err.println("job failed: code=" + errorCode + " message=" + errorMessage);
        }
    }
}
