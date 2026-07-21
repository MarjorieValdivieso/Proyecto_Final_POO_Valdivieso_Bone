package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class CitaController {

    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private ComboBox<Servicio> cbServicio;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private ComboBox<String> cbEstado;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colEstado;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnImprimir;

    private final CitaDao citaDao = new CitaDao();

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {
        if (usuarioActual == null) return;
        boolean esReportes = "Reportes".equalsIgnoreCase(usuarioActual.getRol());

        cbCliente.setDisable(esReportes);
        cbServicio.setDisable(esReportes);
        dpFecha.setDisable(esReportes);
        txtHora.setDisable(esReportes);
        cbEstado.setDisable(esReportes);

        btnGuardar.setVisible(!esReportes);
        btnGuardar.setManaged(!esReportes);
        btnEliminar.setVisible(!esReportes);
        btnEliminar.setManaged(!esReportes);
        btnImprimir.setVisible(!esReportes);
        btnImprimir.setManaged(!esReportes);
    }

    @FXML
    public void initialize() {

        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) { return c != null ? c.getNombre() : ""; }
            @Override
            public Cliente fromString(String s) { return null; }
        });

        cbServicio.setConverter(new StringConverter<Servicio>() {
            @Override
            public String toString(Servicio s) { return s != null ? s.getNombre() : ""; }
            @Override
            public Servicio fromString(String s) { return null; }
        });

        // Columnas de la tabla
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colServicio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getServicio() != null
                                ? data.getValue().getServicio().getNombre() : ""));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cbCliente.setItems(FXCollections.observableArrayList(new ClienteDao().listarTodos()));
        cbServicio.setItems(FXCollections.observableArrayList(new ServicioDao().listarTodos()));
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "Confirmada", "Cancelada"));

        tablaCitas.setItems(FXCollections.observableArrayList(citaDao.listarTodos()));
    }

    @FXML
    public void handleGuardarCita() {

        Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
        Servicio servicioSeleccionado = cbServicio.getSelectionModel().getSelectedItem();
        String estadoSeleccionado = cbEstado.getSelectionModel().getSelectedItem();
        String horaTexto = txtHora.getText() != null ? txtHora.getText().trim() : "";

        //  Validaciones antes de guardar

        if (clienteSeleccionado == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar un cliente.", Alert.AlertType.WARNING);
            return;
        }

        if (servicioSeleccionado == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar un servicio.", Alert.AlertType.WARNING);
            return;
        }

        if (dpFecha.getValue() == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar una fecha.", Alert.AlertType.WARNING);
            return;
        }

        if (dpFecha.getValue().isBefore(java.time.LocalDate.now())) {
            mostrarAlerta("Error", "No puede seleccionar una fecha anterior al día de hoy.", Alert.AlertType.WARNING);
            return;
        }

        if (horaTexto.isEmpty()) {
            mostrarAlerta("Campo requerido", "Debe ingresar la hora.", Alert.AlertType.WARNING);
            return;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(horaTexto);
        } catch (DateTimeParseException e) {
            mostrarAlerta("Error", "La hora ingresada no es válida. Use el formato HH:mm (ej. 14:30).", Alert.AlertType.WARNING);
            return;
        }

        if (estadoSeleccionado == null) {
            mostrarAlerta("Campo requerido", "Debe seleccionar un estado.", Alert.AlertType.WARNING);
            return;
        }

        // No duplicados
        if (citaDao.existeCita(clienteSeleccionado.getId(), dpFecha.getValue(), hora)) {
            mostrarAlerta("Duplicado", "Ya existe una cita para ese cliente en esa fecha y hora.", Alert.AlertType.WARNING);
            return;
        }

        // Guarda la cita

        Cita cita = new Cita();
        cita.setClienteId(clienteSeleccionado.getId());
        cita.setClienteNombre(clienteSeleccionado.getNombre());
        cita.setServicioId(servicioSeleccionado.getId());
        cita.setServicio(servicioSeleccionado);
        cita.setFecha(dpFecha.getValue());
        cita.setHora(hora);
        cita.setEstado(estadoSeleccionado);

        boolean guardado = citaDao.guardar(cita);

        if (guardado) {
            tablaCitas.getItems().add(cita);
            mostrarAlerta("Exito", "Cita guardada correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar la cita.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarCita() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita", "Debes elegir una fila de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar la cita de " + seleccionada.getClienteNombre() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {

            boolean exito = citaDao.eliminar(seleccionada.getId());

            if (exito) {
                tablaCitas.getItems().remove(seleccionada);
                mostrarAlerta("Exito", "Cita eliminada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la cita.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleImprimirComprobante() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Selecciona una cita", "Debes elegir una fila de la tabla para generar el comprobante.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/factura.fxml"));
            Parent root = loader.load();

            FacturaController facturaController = loader.getController();
            facturaController.setCita(seleccionada);

            Stage stage = new Stage();
            stage.setTitle("Comprobante de Pago");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo generar el comprobante.", Alert.AlertType.ERROR);
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