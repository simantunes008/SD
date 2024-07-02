package ex3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private static class ServerThread implements Runnable {
        Socket socket;
        public ServerThread(Socket socket) { this.socket = socket; }
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                int sum = 0;
                int count = 0;

                String line;
                while ((line = in.readLine()) != null) {
                    sum += Integer.parseInt(line);
                    count++;
                    out.println(sum);
                    out.flush();
                }

                out.println(sum / count);
                out.flush();

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);

            while (true) {
                Socket socket = ss.accept();
                Thread worker = new Thread(new ServerThread(socket));
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
