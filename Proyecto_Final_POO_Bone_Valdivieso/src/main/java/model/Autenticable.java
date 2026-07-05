package model;

public interface Autenticable {
    boolean validarCredenciales(String usuario, String clave);
}