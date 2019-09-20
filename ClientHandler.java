//package multusession_1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.time.LocalTime;

public class ClientHandler implements Runnable{

    private Server server;
    private PrintWriter out;

    private Scanner in;
    private static final String HOST = "localhost";
    private static final int PORT = 80;

    private Socket clientSocket = null;

    private static int clients_count = 0;

    long a;
    long b;
    boolean first_time;

    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.out = new PrintWriter(socket.getOutputStream());
            this.in = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            server.sendMessageToAllClients("Новый участник вошёл n в чат!");


            a = System.currentTimeMillis();
            first_time = true;

            System.out.println("Новый участник вошёл n в чат" + a);


            server.sendMessageToAllClients("Клиентов в чате = " + clients_count);

            while (true) {
                if (in.hasNext()) {
                    String clientMessage = in.nextLine();

                    b = System.currentTimeMillis();
                    System.out.println(b - a);

                    if (clientMessage.equalsIgnoreCase("##session##end##")||((b-a)>120000)&&first_time==true) {
                        break;
                    }
                    System.out.println(clientMessage);
                    server.sendMessageToAllClients(clientMessage);

                    first_time = false;
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            out.println(msg);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
