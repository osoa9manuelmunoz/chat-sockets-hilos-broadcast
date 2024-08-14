package client;

import java.io.*;
import java.net.Socket;

/**
 * Clase Cliente para gestionar la conexión y comunicación con el servidor.
 */
public class Cliente {
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String nombreUsuario;
    private String host;
    private int port;
    private InterfazUsuario interfaz;

    /**
     * Constructor para inicializar el cliente.
     *
     * @param host           Dirección IP o hostname del servidor.
     * @param port           Puerto del servidor.
     * @param nombreUsuario  Nombre de usuario para el chat.
     * @param interfaz       Instancia de la interfaz de usuario.
     */
    public Cliente(String host, int port, String nombreUsuario, InterfazUsuario interfaz) {
        this.host = host;
        this.port = port;
        this.nombreUsuario = nombreUsuario;
        this.interfaz = interfaz;
    }

    /**
     * Método para conectar al servidor.
     *
     * @return true si la conexión fue exitosa, false en caso contrario.
     */
    public boolean connect() {
        try {
            this.socket = new Socket(host, port);
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar el nombre de usuario al servidor en formato JSON
            String json = String.format("{\"username\": \"%s\"}", nombreUsuario);
            this.output.write(json);
            this.output.newLine();
            this.output.flush();

            // Iniciar el hilo que escucha los mensajes del servidor
            new Thread(new ListenerThread()).start();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método para enviar un mensaje al servidor.
     *
     * @param mensaje El mensaje a enviar.
     */
    public void enviarMensaje(String mensaje) {
        try {
            String json = String.format("{\"message\": \"%s\"}", mensaje);
            output.write(json);
            output.newLine();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hilo que escucha los mensajes del servidor y los procesa.
     */
    private class ListenerThread implements Runnable {
        public void run() {
            try {
                String mensaje;
                while ((mensaje = input.readLine()) != null) {
                    if (mensaje.contains("\"usuarios\"")) {
                        // Procesar lista de usuarios
                        String usuariosString = mensaje.substring(mensaje.indexOf("[") + 1, mensaje.indexOf("]"));
                        String[] usuarios = usuariosString.replace("\"", "").split(",");
                        interfaz.actualizarListaUsuarios(usuarios);
                    } else if (mensaje.contains("\"message\"")) {
                        // Procesar mensaje
                        String mensajeStr = mensaje.substring(mensaje.indexOf(":") + 2, mensaje.lastIndexOf("\""));
                        interfaz.mostrarMensaje(mensajeStr);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                cerrarConexion();
            }
        }
    }

    /**
     * Método para cerrar la conexión con el servidor.
     */
    public void cerrarConexion() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
