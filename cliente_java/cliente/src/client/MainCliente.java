package client;

import javax.swing.*;

/**
 * Clase principal para iniciar el cliente de chat.
 */
public class MainCliente {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String nombreUsuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario:");
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                new InterfazUsuario("localhost", 1802, nombreUsuario);
            }
        });
    }
}
