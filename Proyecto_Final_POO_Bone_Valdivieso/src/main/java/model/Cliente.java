package model;

public class Cliente extends Persona {

    public Cliente() {
        super();
    }

    public Cliente(int id, String nombre, String telefono, String correo) {
        super(id, nombre, telefono, correo);
    }

    @Override
    public String mostrarInfo() {
        return "Cliente: " + nombre + " | Tel: " + telefono + " | Correo: " + correo;
    }
}