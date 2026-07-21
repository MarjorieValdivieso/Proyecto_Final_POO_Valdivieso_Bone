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

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
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
        aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {
        if (usuarioActual == null) return;
        boolean esReportes = "Reportes".equalsIgnoreCase(usuarioActual.getRol());

        txtNombre.setDisable(esReportes);
        txtPrecio.setDisable(esReportes);
        txtDuracion.setDisable(esReportes);

        btnGuardar.setVisible(!esReportes);
        btnGuardar.setManaged(!esReportes);
        btnEliminar.setVisible(!esReportes);
        btnEliminar.setManaged(!esReportes);
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
        String nombre = txtNombre.getText().trim();
        String duracion = txtDuracion.getText().trim();
        double precio;

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del servicio es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El precio del servicio es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        if (duracion.isEmpty()) {
            mostrarAlerta("Campo requerido", "La duración del servicio es obligatoria.", Alert.AlertType.WARNING);
            return;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarAlerta("Error", "El nombre solo puede contener letras.", Alert.AlertType.WARNING);
            return;
        }

        for (Servicio s : listaServicios) {
            if (s.getNombre().equalsIgnoreCase(nombre)) {
                mostrarAlerta("Error", "Ya existe un servicio con ese nombre.", Alert.AlertType.WARNING);
                return;
            }
        }

        // Validar precio
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());

            if (precio <= 0) {
                mostrarAlerta("Error", "El precio debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El precio debe ser un número válido.", Alert.AlertType.WARNING);
            return;
        }

        // Validar duración
        try {
            int minutos = Integer.parseInt(duracion);

            if (minutos <= 0) {
                mostrarAlerta("Error", "La duración debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La duración debe contener solo números.", Alert.AlertType.WARNING);
            return;
        }

        Servicio servicio = new Servicio(0, nombre, precio, duracion);

        boolean exito = servicioDao.guardar(servicio);

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


}