package controller;

import dao.ServicioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Servicio;
import model.Usuario;

import java.io.IOException;

public class ServicioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDuracion;

    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, String> colNombre;
    @FXML private TableColumn<Servicio, Double> colPrecio;
    @FXML private TableColumn<Servicio, String> colDuracion;

    private Usuario usuarioActual;

    private final ServicioDao servicioDao = new ServicioDao();
    private final ObservableList<Servicio> listaServicios = FXCollections.observableArrayList();

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        tablaServicios.setItems(listaServicios);
        cargarServicios();

        tablaServicios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtNombre.setText(seleccionado.getNombre());
                txtPrecio.setText(String.valueOf(seleccionado.getPrecio()));
                txtDuracion.setText(seleccionado.getDuracion());
            }
        });
    }

    private void cargarServicios() {
        listaServicios.clear();
        listaServicios.addAll(servicioDao.listarTodos());
    }

    @FXML
    public void handleGuardarServicio() {
        String nombre = txtNombre.getText();
        String duracion = txtDuracion.getText();
        double precio;

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del servicio es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio inválido", "Ingresa un número válido para el precio (ej. 5.00).", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = servicioDao.guardar(new Servicio(0, nombre, precio, duracion));

        if (exito) {
            cargarServicios();
            limpiarCampos();
            mostrarAlerta("Éxito", "Servicio guardado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar el servicio.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarServicio() {
        Servicio seleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un servicio", "Debes elegir una fila de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = servicioDao.eliminar(seleccionado.getId());

        if (exito) {
            cargarServicios();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el servicio.", Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtPrecio.clear();
        txtDuracion.clear();
        tablaServicios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void irADashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Belleza Elegante - Panel de Control");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo volver al Dashboard.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Belleza Elegante - Iniciar Sesión");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo cerrar sesión.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}