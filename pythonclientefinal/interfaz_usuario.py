import tkinter as tk
from tkinter import scrolledtext, messagebox

class InterfazUsuario(tk.Tk):
    def __init__(self, cliente):
        """
        Inicializa la interfaz de usuario de la aplicación de chat.
        
        :param cliente: Instancia del cliente que maneja la conexión con el servidor.
        """
        super().__init__()
        self.title(f"Chat Global - Usuario: {cliente.nombre_usuario}")
        self.geometry("800x400")

        self.cliente = cliente

        # Configuración del marco principal
        self.marco_principal = tk.Frame(self)
        self.marco_principal.pack(fill=tk.BOTH, expand=True)

        # Configuración del área de chat
        self.area_chat = scrolledtext.ScrolledText(self.marco_principal, state='disabled', wrap=tk.WORD)
        self.area_chat.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(10, 0), pady=10)

        # Configuración de la lista de usuarios
        self.lista_usuarios = tk.Listbox(self.marco_principal, width=30)
        self.lista_usuarios.pack(side=tk.RIGHT, fill=tk.Y, padx=10, pady=10)

        # Configuración del marco inferior
        self.marco_inferior = tk.Frame(self)
        self.marco_inferior.pack(side=tk.BOTTOM, fill=tk.X, padx=10, pady=10)

        # Configuración del campo de mensaje
        self.campo_mensaje = tk.Entry(self.marco_inferior)
        self.campo_mensaje.pack(side=tk.LEFT, fill=tk.X, expand=True)

        # Configuración del botón de enviar
        self.boton_enviar = tk.Button(self.marco_inferior, text="Enviar", command=self.enviar_mensaje)
        self.boton_enviar.pack(side=tk.RIGHT, padx=5)

        if not self.cliente.connect():
            messagebox.showerror("Error", "Error al conectar con el servidor")
            self.destroy()
        self.protocol("WM_DELETE_WINDOW", self.on_close)

    def enviar_mensaje(self):
        """
        Envía un mensaje al servidor y lo muestra en la interfaz de usuario.
        """
        mensaje = self.campo_mensaje.get()
        if mensaje:
            self.cliente.enviar_mensaje(f"{self.cliente.nombre_usuario}: {mensaje}")
            self.mostrar_mensaje(f"Yo: {mensaje}")
            self.campo_mensaje.delete(0, tk.END)

    def mostrar_mensaje(self, mensaje):
        """
        Muestra un mensaje en el área de chat.
        
        :param mensaje: El mensaje a mostrar.
        """
        self.area_chat.config(state='normal')
        self.area_chat.insert(tk.END, mensaje + '\n')
        self.area_chat.config(state='disabled')
        self.area_chat.yview(tk.END)  # Desplazar hacia abajo para mostrar el último mensaje

    def actualizar_lista_usuarios(self, usuarios):
        """
        Actualiza la lista de usuarios conectados.
        
        :param usuarios: Lista de nombres de usuario.
        """
        self.lista_usuarios.delete(0, tk.END)
        for usuario in usuarios:
            self.lista_usuarios.insert(tk.END, usuario)

    def on_close(self):
        """
        Cierra la conexión y destruye la ventana al cerrar la aplicación.
        """
        self.cliente.cerrar_conexion()
        self.destroy()
