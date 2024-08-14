package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase InterfazUsuario para la interfaz gráfica del cliente de chat.
 */
public class InterfazUsuario extends JFrame {
    private Cliente cliente;
    private JTextArea areaChat;
    private JTextField campoMensaje;
    private DefaultListModel<String> modeloUsuarios;

    /**
     * Constructor para inicializar la interfaz de usuario.
     *
     * @param host          Dirección IP o hostname del servidor.
     * @param port          Puerto del servidor.
     * @param nombreUsuario Nombre de usuario para el chat.
     */
    public InterfazUsuario(String host, int port, String nombreUsuario) {
        setTitle("Chat Global - Usuario: " + nombreUsuario);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        JScrollPane scrollChat = new JScrollPane(areaChat);

        campoMensaje = new JTextField();
        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = campoMensaje.getText();
                cliente.enviarMensaje(nombreUsuario + ": " + mensaje);
                mostrarMensaje("Yo: " + mensaje);
                campoMensaje.setText("");
            }
        });

        modeloUsuarios = new DefaultListModel<>();
        JList<String> listaUsuarios = new JList<>(modeloUsuarios);
        JScrollPane scrollUsuarios = new JScrollPane(listaUsuarios);

        JPanel panelMensaje = new JPanel(new BorderLayout());
        panelMensaje.add(campoMensaje, BorderLayout.CENTER);
        panelMensaje.add(botonEnviar, BorderLayout.EAST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollChat, scrollUsuarios);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);
        add(panelMensaje, BorderLayout.SOUTH);

        cliente = new Cliente(host, port, nombreUsuario, this);

        if (!cliente.connect()) {
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        setVisible(true);
    }

    /**
     * Actualiza la lista de usuarios conectados en la interfaz.
     *
     * @param usuarios Lista de nombres de usuario.
     */
    public void actualizarListaUsuarios(String[] usuarios) {
        SwingUtilities.invokeLater(() -> {
            modeloUsuarios.clear();
            for (String usuario : usuarios) {
                // Remover los caracteres '{' y '}' si están presentes
                usuario = usuario.replace("{", "").replace("}", "");
                // Remover "username:" si está presente
                if (usuario.startsWith("username:")) {
                    usuario = usuario.substring("username:".length()).trim();
                }
                modeloUsuarios.addElement(usuario);
            }
        });
    }

    /**
     * Muestra un mensaje en el área de chat.
     *
     * @param mensaje El mensaje a mostrar.
     */
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> areaChat.append(mensaje + "\n"));
    }

    /**
     * Agrega un usuario a la lista de usuarios conectados.
     *
     * @param nombreUsuario El nombre del usuario a agregar.
     */
    public void agregarUsuario(String nombreUsuario) {
        SwingUtilities.invokeLater(() -> modeloUsuarios.addElement(nombreUsuario));
    }

    /**
     * Remueve un usuario de la lista de usuarios conectados.
     *
     * @param nombreUsuario El nombre del usuario a remover.
     */
    public void removerUsuario(String nombreUsuario) {
        SwingUtilities.invokeLater(() -> modeloUsuarios.removeElement(nombreUsuario));
    }
}
