import socket
import re
import threading
import json

class Cliente:
    def __init__(self, host, port, nombre_usuario, interfaz):
        """
        Inicializa el cliente con la información del servidor y del usuario.
        
        :param host: Dirección IP o hostname del servidor.
        :param port: Puerto del servidor.
        :param nombre_usuario: Nombre de usuario para el chat.
        :param interfaz: Instancia de la interfaz de usuario.
        """
        self.host = host
        self.port = port
        self.nombre_usuario = nombre_usuario
        self.interfaz = interfaz
        self.socket = None
        self.input = None
        self.output = None

    def connect(self):
        """
        Conecta al servidor y envía el nombre de usuario en formato JSON.
        """
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((self.host, self.port))
            self.output = self.socket.makefile('w')
            self.input = self.socket.makefile('r')

            # Enviar el nombre de usuario al servidor en formato JSON
            json_data = json.dumps({"username": self.nombre_usuario})
            self.output.write(json_data + '\n')
            self.output.flush()

            # Iniciar el hilo que escucha los mensajes del servidor
            threading.Thread(target=self.listener_thread, daemon=True).start()
            return True

        except Exception as e:
            print(f"Error al conectar: {e}")
            return False

    def enviar_mensaje(self, mensaje):
        """
        Envía un mensaje al servidor en formato JSON.
        
        :param mensaje: El mensaje a enviar.
        """
        try:
            json_data = json.dumps({"message": mensaje})
            self.output.write(json_data + '\n')
            self.output.flush()
        except Exception as e:
            print(f"Error al enviar mensaje: {e}")

    def listener_thread(self):
        """
        Hilo que escucha mensajes del servidor y los procesa.
        """
        try:
            while True:
                mensaje = self.input.readline().strip()
                if mensaje:
                    print(f"Mensaje recibido crudo: {mensaje}")  # Depuración
                    
                    try:
                        data = self.parse_mensaje(mensaje)
                        
                        if "usuarios" in data:
                            # Procesar lista de usuarios
                            usuarios_lista = [usuario.get("username", "Desconocido") for usuario in data["usuarios"]]
                            self.interfaz.actualizar_lista_usuarios(usuarios_lista)

                        elif "message" in data:
                            # Procesar mensaje
                            mensaje_str = data["message"]
                            self.interfaz.mostrar_mensaje(mensaje_str)

                        elif "error" in data:
                            # Procesar mensaje de error
                            error_str = data["error"]
                            self.interfaz.mostrar_mensaje(f"Error: {error_str}")

                    except Exception as e:
                        print(f"Error al procesar mensaje: {e}")
                        continue

        except Exception as e:
            print(f"Error en listener: {e}")
            self.cerrar_conexion()

    def parse_mensaje(self, mensaje):
        """
        Parsea el mensaje recibido del servidor y lo convierte en un diccionario.

        :param mensaje: El mensaje recibido.
        :return: Diccionario con los datos del mensaje.
        """
        if mensaje.startswith('{"usuarios":'):
            # Extraer la lista de usuarios
            match = re.search(r'"usuarios":\[(.*?)\]', mensaje)
            if match:
                usuarios_str = match.group(1)
                # Extraer los nombres de usuario
                usernames = re.findall(r'"username":\s*"(.*?)"', usuarios_str)
                return {"usuarios": [{"username": username} for username in usernames]}
        elif mensaje.startswith('{"message":'):
            # Extraer el mensaje
            match = re.search(r'"message":\s*"(.*?)"', mensaje)
            if match:
                return {"message": match.group(1)}
        elif mensaje.startswith('{"error":'):
            # Extraer el error
            match = re.search(r'"error":\s*"(.*?)"', mensaje)
            if match:
                return {"error": match.group(1)}
        
        # Si no coincide con ningún formato conocido, intentar parsear como JSON normal
        return json.loads(mensaje)

    def cerrar_conexion(self):
        """
        Cierra la conexión con el servidor.
        """
        try:
            if self.input:
                self.input.close()
            if self.output:
                self.output.close()
            if self.socket:
                self.socket.close()
        except Exception as e:
            print(f"Error al cerrar conexión: {e}")
