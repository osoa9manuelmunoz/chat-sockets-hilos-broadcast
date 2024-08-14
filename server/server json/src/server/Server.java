package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que representa el servidor y maneja la conexión con múltiples clientes.
 */
public class Server implements SocketProcess {
    private ServerSocket serverSocket;
    private List<Session> sessions;
    private List<String> usuariosConectados;
    private Map<String, Integer> usuarioHiloMap;
    private int contadorHilos = 0;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /**
     * Constructor que inicializa el servidor con un ServerSocket.
     * 
     * @param serverSocket el ServerSocket para aceptar conexiones
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.sessions = new ArrayList<>();
        this.usuariosConectados = new ArrayList<>();
        this.usuarioHiloMap = new HashMap<>();
    }

    @Override
    public boolean bind() {
        try {
            while (true) {
                Socket socket = this.serverSocket.accept();
                Session session = new Session(socket);

                String nombreUsuario = session.read();
                if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                    logger.log(Level.SEVERE, "Error al leer el nombre de usuario");
                    session.close();
                    continue;
                }

                synchronized (usuariosConectados) {
                    if (usuariosConectados.contains(nombreUsuario)) {
                        session.write("{\"error\":\"Usuario ya existe, elige otro nombre\"}");
                        session.close();
                        continue;
                    } else {
                        usuariosConectados.add(nombreUsuario);
                        session.setUsuario(nombreUsuario);
                        addSession(session);
                        logger.log(Level.INFO, "Nuevo usuario conectado: " + nombreUsuario);
                        actualizarUsuariosConectados();
                    }
                }

                Thread clientThread = new Thread(new ClientHandler(session, this));
                clientThread.start();
                logger.log(Level.INFO, "Hilo iniciado para el usuario: " + nombreUsuario);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error en el bind del servidor", e);
            return false;
        }
    }

    @Override
    public List<Object> listen() {
        return null;
    }

    @Override
    public boolean response(List<Object> data) {
        return false;
    }

    @Override
    public boolean close() {
        try {
            for (Session session : sessions) {
                session.close();
            }
            serverSocket.close();
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cerrar el servidor", e);
            return false;
        }
    }

    /**
     * Método que actualiza la lista de usuarios conectados y la envía a todos los clientes.
     */
    private void actualizarUsuariosConectados() {
        StringBuilder usuariosConectadosStr = new StringBuilder("{\"usuarios\":[");
        for (String usuario : usuariosConectados) {
            usuariosConectadosStr.append("\"").append(usuario).append("\",");
        }
        if (usuariosConectadosStr.length() > 0) {
            usuariosConectadosStr.setLength(usuariosConectadosStr.length() - 1);
        }
        usuariosConectadosStr.append("]}");

        broadcast(usuariosConectadosStr.toString(), null); // Pasar null para enviar a todos
    }

    /**
     * Método que envía un mensaje a todos los clientes.
     * 
     * @param message el mensaje a enviar
     */
    public void broadcast(String message) {
        broadcast(message, null);
    }

    /**
     * Método que envía un mensaje a todos los clientes, excepto al remitente.
     * 
     * @param message el mensaje a enviar
     * @param remitente la sesión del remitente
     */
    public void broadcast(String message, Session remitente) {
        for (Session session : sessions) {
            if (session != remitente) {
                session.write(message);
            }
        }
    }

    /**
     * Método que agrega una nueva sesión a la lista de sesiones activas.
     * 
     * @param session la sesión a agregar
     */
    private synchronized void addSession(Session session) {
        sessions.add(session);
    }

    /**
     * Método que elimina una sesión de la lista de sesiones activas.
     * 
     * @param session la sesión a eliminar
     */
    public synchronized void removeSession(Session session) {
        synchronized (usuariosConectados) {
            usuariosConectados.remove(session.getUsuario());
            logger.log(Level.INFO, "Usuario desconectado: " + session.getUsuario());
            actualizarUsuariosConectados();
        }
        sessions.remove(session);
        session.close();
        logger.log(Level.INFO, "Hilo cerrado para el usuario: " + session.getUsuario());
    }

    /**
     * Método que incrementa el contador de hilos y retorna el nuevo valor.
     * 
     * @return el nuevo valor del contador de hilos
     */
    public synchronized int incrementarContadorHilos() {
        return ++contadorHilos;
    }

    /**
     * Método que retorna el mapa de usuarios a números de hilo.
     * 
     * @return el mapa de usuarios a números de hilo
     */
    public Map<String, Integer> getUsuarioHiloMap() {
        return usuarioHiloMap;
    }

    /**
     * Método que retorna el total de hilos creados.
     * 
     * @return el total de hilos creados
     */
    public int getTotalHilos() {
        return contadorHilos;
    }
}
