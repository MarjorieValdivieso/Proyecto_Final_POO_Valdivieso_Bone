package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Cita;
import model.Usuario;

import java.io.IOException;

public class CitaController {

    // 1. Elementos visuales vinculados al FXML
    @FXML private ComboBox<?> cbCliente;
    @FXML private ComboBox<?> cbServicio;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colEstado;

    // Botones clave para resolver los errores de vinculación
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    // 2. Control de sesión del usuario actual
    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {
        // Tu lógica para poblar tablas y ComboBoxes al cargar la pantalla
    }

    // 3. Método requerido por la línea 89 del FXML para registrar las citas
    @FXML
    public void handleGuardarCita() {
        // Tu lógica para recopilar la información y llamar a CitaDao
        System.out.println("Procesando el guardado de la cita...");
    }

    // 4. Acción asignada al botón de regreso en la barra superior
    @FXML
    public void irADashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Belleza Elegante - Panel de Control");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error del Sistema");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo regresar al panel de control.");
            alerta.showAndWait();
            e.printStackTrace();
        }
    }
}