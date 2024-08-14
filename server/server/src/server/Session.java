package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Clase que representa una sesión de cliente y maneja la comunicación de entrada y salida.
 */
public class Session {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private String usuario;

    public Session(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            this.objectOutputStream = null;
            this.objectInputStream = null;
            this.socket = null;
        }
    }

    public Object read() throws IOException, ClassNotFoundException {
        return this.objectInputStream.readObject();
    }

    public boolean write(Object data) {
        try {
            this.objectOutputStream.writeObject(data);
            this.objectOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        try {
            if (objectOutputStream != null) objectOutputStream.close();
            if (objectInputStream != null) objectInputStream.close();
            if (socket != null) socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}