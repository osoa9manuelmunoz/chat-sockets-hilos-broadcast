
import java.net.ServerSocket;
import server.JavaServerSocket;
import server.Server;
import server.SocketProcess;

/**
 * Clase principal que arranca el servidor.
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("Java Server Socket");

        JavaServerSocket javaServerSocket = new JavaServerSocket(1802, 100);
        ServerSocket serverSocket = javaServerSocket.get();
        if (serverSocket == null) {
            System.out.println("ServerSocket is null");
            return;
        }

        SocketProcess server = new Server(serverSocket);

        if (!server.bind()) {
            System.out.println("Server bind failed");
            return;
        }

        System.out.println("Java Server Socket is running");
    }
}