package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Usuario;

public class DashboardController {

    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    public void inicializarDashboard(Usuario usuario) {
        lblUsuario.setText("Usuario: " + usuario.getUsuario());
        lblRol.setText(usuario.getRol().toUpperCase());
    }
    @FXML
    public void handleGuardarCita() {

    }

    @FXML
    public void handleActualizarCita() {

    }

    @FXML
    public void handleEliminarCita() {

    }

    @FXML
    public void handleLimpiarCita() {

    }

    @FXML
    public void irAClientes() {
        // navegación a clientes.fxml
    }

    @FXML
    public void irAServicios() {
        // navegación a servicios.fxml
    }

    @FXML
    public void handleCerrarSesion() {
        // regresa al login
    }
}