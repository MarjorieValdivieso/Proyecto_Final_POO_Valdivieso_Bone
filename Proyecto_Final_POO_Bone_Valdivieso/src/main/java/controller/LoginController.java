package controller;

import dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Button btnIngresar;

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @FXML
    public void initialize() {
        if (cmbRol != null) {
            cmbRol.setItems(FXCollections.observableArrayList("Administrador", "Cajero", "Reportes"));
        }
    }

    @FXML
    public void handleAcceder() {
        String username = txtUsuario.getText();
        String password = txtContrasena.getText();
        String rolSeleccionado = cmbRol.getValue();

        // Validación de campos vacíos
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                rolSeleccionado == null) {

            mostrarAlerta("Campos Incompletos", "Por favor, llene todos los campos y seleccione un rol.", Alert.AlertType.WARNING);
            return;
        }

        Usuario usuarioLogueado = usuarioDao.autenticarUsuario(username, password, rolSeleccionado);


        if (usuarioLogueado == null || !usuarioLogueado.validarCredenciales(username, password)) {
            mostrarAlerta("Credenciales Incorrectas", "El usuario, la contraseña o el rol no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardCtrl = loader.getController();
            dashboardCtrl.inicializarDashboard(usuarioLogueado);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Belleza Elegante - Panel de Control");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo cargar la pantalla principal del Dashboard.", Alert.AlertType.ERROR);
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