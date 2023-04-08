package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static String host;
    private static int port;
    private static String name;
    public static Socket socket;
    public static BufferedReader in;
    public static PrintWriter out;
    public static boolean connected;

    public Client(String host, int port, String name) {
        Client.host = host;
        Client.port = port;
        Client.name = name;
        connected = false;
    }

    public static void connect(String host, int port) throws IOException {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Invalid host");
        }

        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        Thread thread = new Thread(new IncomingMessageHandler());
        thread.start();

        connected = true;
    }


    public static void send(String message) {
        out.println(message);
    }

    public static class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                while (connected) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static void disconnect() throws IOException {
        if (connected) {
            out.println("-exit");
            socket.close();
            connected = false;
        }
    }
    public static void handleUserInput(String userInput) throws IOException {
        if (userInput.equals("-exit")) {
            disconnect();
        } else{
            send(userInput);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client1 = new Client("localhost", 8088, "client1");
        connect("localhost",8088);

        Scanner scanner = new Scanner(System.in);
        while(true){
            String userInput = scanner.nextLine();
            handleUserInput(userInput);
        }
    }


}


