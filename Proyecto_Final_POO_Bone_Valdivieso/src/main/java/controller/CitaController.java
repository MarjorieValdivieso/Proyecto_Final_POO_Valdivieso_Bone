package controller;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CitaController {

    @FXML
    private ComboBox<Cliente> cmbCliente;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TextField txtHoraCita;

    @FXML
    private ComboBox<Servicio> cmbServicios;

    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private TableView<Cita> tvCitas;

    @FXML
    private TableColumn<Cita, String> colFecha;

    @FXML
    private TableColumn<Cita, String> colHora;

    @FXML
    private TableColumn<Cita, String> colCliente;

    @FXML
    private TableColumn<Cita, String> colServicio;

    @FXML
    private TableColumn<Cita, String> colEstado;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnLimpiar;

    private final CitaDao citaDao = new CitaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final ServicioDao servicioDao = new ServicioDao();

    private Cita citaSeleccionada;
    private Usuario usuarioActual;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {

        cargarClientes();
        cargarServicios();
        cargarEstados();
        configurarTabla();
        cargarCitas();

        tvCitas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, actual) -> {

                    if (actual != null) {
                        mostrarCitaEnFormulario(actual);
                    }

                });

    }

    private void cargarClientes() {

        cmbCliente.setItems(
                FXCollections.observableArrayList(
                        clienteDao.listarTodos()));

        cmbCliente.setConverter(new StringConverter<>() {

            @Override
            public String toString(Cliente c) {

                if (c == null) return "";

                return c.getNombre() + " - " + c.getTelefono();
            }

            @Override
            public Cliente fromString(String s) {
                return null;
            }

        });

    }

    private void cargarServicios() {

        cmbServicios.setItems(
                FXCollections.observableArrayList(
                        servicioDao.listarTodos()));

        cmbServicios.setConverter(new StringConverter<>() {

            @Override
            public String toString(Servicio s) {

                if (s == null) return "";

                return s.getNombre() + "   ($" + s.getPrecio() + ")";
            }

            @Override
            public Servicio fromString(String string) {
                return null;
            }

        });

    }

    private void cargarEstados() {

        cmbEstado.setItems(FXCollections.observableArrayList(
                "Pendiente",
                "Confirmada",
                "Cancelada"
        ));

        cmbEstado.setValue("Pendiente");

    }

    private void configurarTabla() {

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getFecha()
                                .toString()));

        colHora.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getHora()
                                .format(FORMATO_HORA)));

        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getClienteNombre()));

        colServicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getServicio()
                                .getNombre()));

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

    }

    private void cargarCitas() {

        tvCitas.setItems(
                FXCollections.observableArrayList(
                        CitaDao.listarTodos()));

    }

    private void mostrarCitaEnFormulario(Cita cita) {

        citaSeleccionada = cita;

        Cliente cliente =
                clienteDao.buscarPorId(cita.getClienteId());

        if (cliente != null) {

            for (Cliente c : cmbCliente.getItems()) {

                if (c.getId() == cliente.getId()) {

                    cmbCliente.setValue(c);
                    break;

                }

            }

        }

        dpFecha.setValue(cita.getFecha());

        txtHoraCita.setText(
                cita.getHora().format(FORMATO_HORA));

        cmbEstado.setValue(cita.getEstado());

        if (cita.getServicio() != null) {

            for (Servicio s : cmbServicios.getItems()) {

                if (s.getId() == cita.getServicio().getId()) {

                    cmbServicios.setValue(s);
                    break;

                }

            }

        }

    }
    @FXML
    public void handleGuardarCita() {

        Cita cita = construirCitaDesdeFormulario();

        if (cita == null) {
            return;
        }

        boolean exito = citaDao.guardar(cita);

        if (exito) {
            cargarCitas();
            handleLimpiarCita();

            mostrarAlerta(
                    "Éxito",
                    "La cita se registró correctamente.",
                    Alert.AlertType.INFORMATION
            );

        } else {

            mostrarAlerta(
                    "Error",
                    "No fue posible guardar la cita.",
                    Alert.AlertType.ERROR
            );

        }

    }

    @FXML
    public void handleActualizarCita() {

        if (citaSeleccionada == null) {

            mostrarAlerta(
                    "Aviso",
                    "Seleccione una cita para actualizar.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        Cita cita = construirCitaDesdeFormulario();

        if (cita == null) {
            return;
        }

        cita.setId(citaSeleccionada.getId());

        boolean exito = citaDao.actualizar(cita);

        if (exito) {

            cargarCitas();
            handleLimpiarCita();

            mostrarAlerta(
                    "Éxito",
                    "La cita fue actualizada correctamente.",
                    Alert.AlertType.INFORMATION
            );

        } else {

            mostrarAlerta(
                    "Error",
                    "No fue posible actualizar la cita.",
                    Alert.AlertType.ERROR
            );

        }

    }

    @FXML
    public void handleEliminarCita() {

        if (citaSeleccionada == null) {

            mostrarAlerta(
                    "Aviso",
                    "Seleccione una cita para eliminar.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        boolean exito = citaDao.eliminar(citaSeleccionada.getId());

        if (exito) {

            cargarCitas();
            handleLimpiarCita();

            mostrarAlerta(
                    "Éxito",
                    "La cita fue eliminada.",
                    Alert.AlertType.INFORMATION
            );

        } else {

            mostrarAlerta(
                    "Error",
                    "No fue posible eliminar la cita.",
                    Alert.AlertType.ERROR
            );

        }

    }

    @FXML
    public void handleLimpiarCita() {

        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        txtHoraCita.clear();
        cmbServicios.setValue(null);
        cmbEstado.setValue("Pendiente");

        citaSeleccionada = null;

        tvCitas.getSelectionModel().clearSelection();

    }

    private Cita construirCitaDesdeFormulario() {

        Cliente cliente = cmbCliente.getValue();
        Servicio servicio = cmbServicios.getValue();

        LocalDate fecha = dpFecha.getValue();

        String horaTexto = txtHoraCita.getText();

        String estado = cmbEstado.getValue();

        if (cliente == null) {

            mostrarAlerta(
                    "Campo requerido",
                    "Seleccione un cliente.",
                    Alert.AlertType.WARNING
            );

            return null;
        }

        if (servicio == null) {

            mostrarAlerta(
                    "Campo requerido",
                    "Seleccione un servicio.",
                    Alert.AlertType.WARNING
            );

            return null;
        }

        if (fecha == null) {

            mostrarAlerta(
                    "Campo requerido",
                    "Seleccione una fecha.",
                    Alert.AlertType.WARNING
            );

            return null;
        }

        LocalTime hora;

        try {

            hora = LocalTime.parse(horaTexto, FORMATO_HORA);

        } catch (DateTimeParseException e) {

            mostrarAlerta(
                    "Hora inválida",
                    "Ingrese la hora en formato HH:mm.",
                    Alert.AlertType.WARNING
            );

            return null;

        }

        if (estado == null || estado.isBlank()) {
            estado = "Pendiente";
        }

        Cita cita = new Cita();

        cita.setClienteId(cliente.getId());
        cita.setServicioId(servicio.getId());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(estado);
        cita.setServicio(servicio);
        cita.setClienteNombre(cliente.getNombre());

        return cita;

    }

    private void mostrarAlerta(String titulo,
                               String mensaje,
                               Alert.AlertType tipo) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();

    }

}