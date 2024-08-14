from tkinter import simpledialog
from interfaz_usuario import InterfazUsuario
from cliente import Cliente

if __name__ == "__main__":
    import tkinter as tk
    root = tk.Tk()
    root.withdraw()  # Oculta la ventana principal de Tkinter

    # Pedir el nombre de usuario al iniciar la aplicación
    nombre_usuario = simpledialog.askstring("Nombre de usuario", "Ingrese su nombre de usuario:")
    if nombre_usuario:
        # Crear el cliente y la interfaz de usuario
        cliente = Cliente("localhost", 1802, nombre_usuario, None)
        interfaz = InterfazUsuario(cliente)
        cliente.interfaz = interfaz
        interfaz.mainloop()
    else:
        root.destroy()
