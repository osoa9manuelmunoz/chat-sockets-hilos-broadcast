package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * La clase Session maneja la comunicación entre el servidor y un cliente
 * a través de un socket. Proporciona métodos para leer y escribir datos en
 * la conexión, así como para gestionar la sesión del usuario.
 */
public class Session {
    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private String usuario;

    /**
     * Constructor que inicializa una nueva instancia de la clase Session.
     * Configura los flujos de entrada y salida basados en el socket proporcionado.
     *
     * @param socket El socket asociado con la conexión del cliente.
     */
    public Session(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            this.writer = null;
            this.reader = null;
            this.socket = null;
        }
    }

    /**
     * Lee una línea de datos del flujo de entrada.
     *
     * @return El dato leído como una cadena.
     * @throws IOException Si ocurre un error durante la lectura.
     */
    public String read() throws IOException {
        return this.reader.readLine();
    }

    /**
     * Escribe una línea de datos al flujo de salida.
     *
     * @param data La cadena de datos a enviar al cliente.
     * @return true si la escritura fue exitosa, false en caso contrario.
     */
    public boolean write(String data) {
        try {
            this.writer.write(data);
            this.writer.newLine();
            this.writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cierra los flujos de entrada y salida, y el socket asociado con la sesión.
     *
     * @return true si el cierre fue exitoso, false en caso contrario.
     */
    public boolean close() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el nombre de usuario asociado con la sesión.
     *
     * @return El nombre de usuario como una cadena.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario para la sesión.
     *
     * @param usuario El nombre de usuario a asociar con la sesión.
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
