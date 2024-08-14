package server;

import java.io.Serializable;
import java.util.List;

/**
 * Clase que encapsula una lista de usuarios conectados.
 */
public class ListaUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> usuarios;

    /**
     * Constructor que inicializa la lista de usuarios.
     * 
     * @param usuarios la lista de usuarios
     */
    public ListaUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Método que retorna la lista de usuarios.
     * 
     * @return la lista de usuarios
     */
    public List<String> getUsuarios() {
        return usuarios;
    }
}
