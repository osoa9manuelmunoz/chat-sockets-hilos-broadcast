package server;


import java.io.Serializable;
import java.util.List;

public class ListaUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> usuarios;

    public ListaUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    public List<String> getUsuarios() {
        return usuarios;
    }
}