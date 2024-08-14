package server;

import java.util.List;

/**
 * La interfaz SocketProcess define los métodos esenciales para manejar la
 * comunicación a través de sockets en el servidor. Los métodos incluyen la
 * vinculación del socket, la escucha de conexiones, la respuesta a datos y el
 * cierre de la conexión.
 */
public interface SocketProcess {

    /**
     * Vincula el socket al puerto especificado para empezar a aceptar conexiones.
     *
     * @return true si la vinculación fue exitosa, false en caso contrario.
     */
    public boolean bind();

    /**
     * Escucha las conexiones entrantes y devuelve una lista de objetos representando
     * los datos recibidos.
     *
     * @return Una lista de objetos que representan los datos recibidos de los clientes.
     */
    public List<Object> listen();

    /**
     * Envía una respuesta basada en los datos proporcionados a la lista de clientes
     * conectados.
     *
     * @param data Lista de objetos que representan los datos a enviar.
     * @return true si la respuesta fue enviada con éxito, false en caso contrario.
     */
    public boolean response(List<Object> data);

    /**
     * Cierra el socket y libera los recursos asociados.
     *
     * @return true si el cierre fue exitoso, false en caso contrario.
     */
    public boolean close();
}
