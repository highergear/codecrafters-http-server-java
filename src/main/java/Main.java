import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static final int PORT = 4221;
    public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage


     try (final var serverSocket = new ServerSocket(PORT)) {
        // Since the tester restarts your program quite often, setting SO_REUSEADDR
        // ensures that we don't run into 'Address already in use' errors
        serverSocket.setReuseAddress(true);

        final var threadFactory = Thread.ofVirtual().factory();

        while (true) {
            final var clientSocket = serverSocket.accept(); // Wait for connection from client.
            final var client = new Client(clientSocket);
            System.out.println("accepted new connection");

            final var thread = threadFactory.newThread(client);
            thread.start();
        }
     }  catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
     }
  }
}
