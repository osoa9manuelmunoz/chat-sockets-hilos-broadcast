package server;
/**
 * Clase que maneja la comunicación con un cliente específico en un hilo separado.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que maneja la comunicación con un cliente específico en un hilo separado.
 */
public class ClientHandler implements Runnable {
    private Session session;
    private Server server;
    private int numeroHilo;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Session session, Server server) {
        this.session = session;
        this.server = server;

        // Asignar un número de hilo al usuario
        String nombreUsuario = session.getUsuario();
        if (server.getUsuarioHiloMap().containsKey(nombreUsuario)) {
            this.numeroHilo = server.getUsuarioHiloMap().get(nombreUsuario); // Recuperar el número de hilo existente
        } else {
            this.numeroHilo = server.incrementarContadorHilos(); // Asignar un nuevo número de hilo
            server.getUsuarioHiloMap().put(nombreUsuario, this.numeroHilo); // Guardar la asociación en el mapa
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Usuario " + session.getUsuario() + " usa el Hilo " + numeroHilo);
        try {
            Object message;
            while ((message = session.read()) != null) {
                logger.log(Level.INFO, "Received message: " + message + " (Hilo " + numeroHilo + " para " + session.getUsuario() + ")");
                server.broadcast(message, this.session); // Pasar la sesión actual para excluirla
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.removeSession(session);

            int totalHilos = Thread.activeCount(); // Total de hilos activos
            int hilosExistentes = server.getTotalHilos();   // Total de hilos creados

            logger.log(Level.INFO, "Total de hilos existentes: " + hilosExistentes + ", Hilos activos: " + (totalHilos - 1) + ", Hilo " + numeroHilo + " terminado.");
        }
    }
}