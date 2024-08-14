package client;

import java.io.Serializable;
import java.util.List;

/**
 * Clase para gestionar la lista de usuarios conectados.
 */
public class ListaUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> usuarios;

    /**
     * Constructor para inicializar la lista de usuarios.
     *
     * @param usuarios Lista de nombres de usuario.
     */
    public ListaUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Obtiene la lista de usuarios conectados.
     *
     * @return Lista de nombres de usuario.
     */
    public List<String> getUsuarios() {
        return usuarios;
    }
}
