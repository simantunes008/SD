package Client;

import Server.Task;
import Server.User;

import java.io.*;
import java.nio.file.*;

public class ClientUI {
    private final IClient client;
    private final BufferedReader userInput;

    public ClientUI(IClient client) {
        this.client = client;
        this.userInput = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        System.out.println("Escolha uma opção:");
        System.out.println("1. Registrar");
        System.out.println("2. Autenticar");
        System.out.println("3. Enviar pedido de tarefa");
        System.out.println("4. Consultar memória disponível");
        System.out.println("5. Consultar fila de espera");
        System.out.println("6. Sair");
        System.out.print("Opção: ");

        String str;

        User user = new User();

        try {
            while ((str = userInput.readLine()) != null) {
                try {
                    switch (str) {
                        case "1":
                            System.out.print("Insira um username: ");
                            user.setUsername(userInput.readLine());

                            System.out.print("Insira uma password: ");
                            user.setPassword(userInput.readLine());

                            if (client.register(user)) {
                                System.out.println("Registro concluído com sucesso!");
                            } else {
                                System.out.println("Erro: Username já existe");
                            }
                            break;
                        case "2":
                            System.out.print("Insira o seu username: ");
                            user.setUsername(userInput.readLine());

                            System.out.print("Insira a sua password: ");
                            user.setPassword(userInput.readLine());

                            if (client.authenticate(user)) {
                                System.out.println("Autenticação concluída com sucesso!");
                            } else {
                                System.out.println("Erro: Username ou password inválidos.");
                            }
                            break;
                        case "3":
                            System.out.print("Insira path do arquivo: ");
                            str = userInput.readLine();

                            Path path = Paths.get(str);
                            if (!Files.exists(path) || Files.isDirectory(path)) {
                                System.out.println("Erro: Arquivo inexistente ou caminho inválido.");
                                break;
                            }
                            byte[] job = Files.readAllBytes(path);

                            System.out.print("Insira tamanho do pedido: ");
                            int memory = Integer.parseInt(userInput.readLine());

                            Task task = new Task(job, memory);
                            System.out.println("Número de tarefa: " + task.getId());

                            byte[] output = client.request(task);

                            try (FileOutputStream fout = new FileOutputStream("/home/loki/IdeaProjects/SD/src/Results/" + task.getId())) {
                                fout.write(output);
                            }

                            System.out.println("Success, returned " + output.length + " bytes!");
                            break;
                        case "4":
                            int availableMemory = client.getAvailableMemory();
                            System.out.println("Memória disponível: " + availableMemory);
                            break;
                        case "5":
                            int pendingTasks = client.getPendingTasks();
                            System.out.println("Número de tarefas pendentes: " + pendingTasks);
                            break;
                        case "6":
                            System.out.println("Encerrando...");
                            client.closeConnection();
                            System.exit(0);
                        default:
                            System.out.println("Opção inválida.");
                    }
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }

                System.out.print("Opção: ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
