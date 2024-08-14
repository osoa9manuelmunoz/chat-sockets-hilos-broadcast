package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que encapsula la creación de un ServerSocket.
 */
public class JavaServerSocket {
    private int port;
    private int amountClients;

    /**
     * Constructor que inicializa el puerto y la cantidad máxima de clientes.
     * 
     * @param port el puerto en el que el servidor escuchará
     * @param amountClients la cantidad máxima de clientes
     */
    public JavaServerSocket(int port, int amountClients) {
        this.port = port;
        this.amountClients = amountClients;
    }

    /**
     * Método que crea y retorna un ServerSocket.
     * 
     * @return un objeto ServerSocket o null si ocurre un error
     */
    public ServerSocket get() {
        try {
            return new ServerSocket(this.port, this.amountClients);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
}
