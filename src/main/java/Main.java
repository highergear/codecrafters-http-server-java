import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage

     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
        serverSocket = new ServerSocket(4221);
        // Since the tester restarts your program quite often, setting SO_REUSEADDR
        // ensures that we don't run into 'Address already in use' errors
        serverSocket.setReuseAddress(true);
        clientSocket = serverSocket.accept(); // Wait for connection from client.
        System.out.println("accepted new connection");

        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String request  = reader.readLine();
        System.out.printf("HTTP Request accepted: %s \n", request);
        String[] lines = request.split(" ", 0);


        OutputStream output = clientSocket.getOutputStream();

        String[] urlPath = lines[1].split("/", 0);

        if (urlPath.length <= 1) {
            output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
        }
        else if (urlPath[1].equals("echo")) {
            String strOut = String.format(
                        "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                        urlPath[2].length(),
                        urlPath[2]
                    );
            output.write(strOut.getBytes());
        } else {
            output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
        }
     }  catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
     }
  }
}
