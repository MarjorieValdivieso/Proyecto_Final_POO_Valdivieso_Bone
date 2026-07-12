package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Usuario;

import java.io.IOException;

public class DashboardController {

    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private AnchorPane contentArea;


    @FXML private Button btnIrClientes;
    @FXML private Button btnIrServicios;
    @FXML private Button btnCitas;
    @FXML private Button btnReportes;

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        lblUsuario.setText("Usuario: " + usuario.getUsuario());
        lblRol.setText(usuario.getRol().toUpperCase());

        String rol = usuario.getRol();

        if ("Cajero".equalsIgnoreCase(rol)) {
            btnReportes.setVisible(false);
            btnReportes.setManaged(false);

        } else if ("Reportes".equalsIgnoreCase(rol)) {
            btnIrClientes.setVisible(false);
            btnIrClientes.setManaged(false);
            btnIrServicios.setVisible(false);
            btnIrServicios.setManaged(false);
            btnCitas.setVisible(false);
            btnCitas.setManaged(false);

        } else {

        }

        mostrarClientes();
    }

    @FXML
    public void mostrarCitas() {
        cargarContenido("/vista/citas.fxml");
    }

    @FXML
    public void mostrarClientes() {
        cargarContenido("/vista/clientes.fxml");
    }

    @FXML
    public void mostrarServicios() {
        cargarContenido("/vista/servicios.fxml");
    }

    @FXML
    public void mostrarReportes() {
        cargarContenido("/vista/reportes.fxml");
    }

    private void cargarContenido(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent nuevoContenido = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ClienteController c) c.setUsuario(usuarioActual);
            if (controller instanceof ServicioController s) s.setUsuario(usuarioActual);
            if (controller instanceof CitaController ci) ci.setUsuario(usuarioActual);
            if (controller instanceof ReportesController r) r.setUsuario(usuarioActual);

            AnchorPane.setTopAnchor(nuevoContenido, 0.0);
            AnchorPane.setBottomAnchor(nuevoContenido, 0.0);
            AnchorPane.setLeftAnchor(nuevoContenido, 0.0);
            AnchorPane.setRightAnchor(nuevoContenido, 0.0);

            contentArea.getChildren().setAll(nuevoContenido);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la seccion solicitada.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Belleza Elegante - Iniciar Sesion");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla de inicio de sesion.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}