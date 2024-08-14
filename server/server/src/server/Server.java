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

public class Server implements SocketProcess {
    private ServerSocket serverSocket;
    private List<Session> sessions;
    private List<String> usuariosConectados;
    private Map<String, Integer> usuarioHiloMap;
    private int contadorHilos = 0;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

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

                String nombreUsuario = null;
                try {
                    nombreUsuario = (String) session.read();
                } catch (ClassNotFoundException e) {
                    logger.log(Level.SEVERE, "Error al leer el nombre de usuario", e);
                    session.close();
                    continue;
                }

                synchronized (usuariosConectados) {
                    if (usuariosConectados.contains(nombreUsuario)) {
                        session.write("ERROR:Usuario ya existe, elige otro nombre");
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

    private void actualizarUsuariosConectados() {
        StringBuilder usuariosConectadosStr = new StringBuilder("USUARIOS:");
        for (String usuario : usuariosConectados) {
            usuariosConectadosStr.append(usuario).append(",");
        }
        if (usuariosConectadosStr.length() > 0) {
            usuariosConectadosStr.setLength(usuariosConectadosStr.length() - 1);
        }

        broadcast(usuariosConectadosStr.toString(), null); // Pasar null para enviar a todos
    }

    public void broadcast(Object message) {
        broadcast(message, null);
    }

    public void broadcast(Object message, Session remitente) {
        for (Session session : sessions) {
            if (session != remitente) {
                session.write(message);
            }
        }
    }

    private synchronized void addSession(Session session) {
        sessions.add(session);
    }

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

    public synchronized int incrementarContadorHilos() {
        return ++contadorHilos;
    }

    public Map<String, Integer> getUsuarioHiloMap() {
        return usuarioHiloMap;
    }

    public int getNumeroHilosActivos() {
        return Thread.activeCount();
    }

    public int getTotalHilos() {
        return contadorHilos;
    }
}