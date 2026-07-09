package controller;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DashboardController {

    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    @FXML private Button btnCitas;
    @FXML private Button btnIrClientes;
    @FXML private Button btnIrServicios;
    @FXML private Button btnReportes;

    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtFechaCita;
    @FXML private TextField txtHoraCita;
    @FXML private ComboBox<Servicio> cmbServicios;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Cita> tvCitas;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colServicio;
    @FXML private TableColumn<Cita, String> colEstado;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final CitaDao citaDao = new CitaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final ServicioDao servicioDao = new ServicioDao();

    private final ObservableList<Cita> listaCitas = FXCollections.observableArrayList();
    private Cita citaSeleccionada;

    // Objeto Usuario recibido desde LoginController
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarServicios();
        cargarEstados();
        cargarCitas();

        tvCitas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                mostrarCitaEnFormulario(seleccionada);
            }
        });
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        lblUsuario.setText("Usuario: " + usuario.getUsuario());
        lblRol.setText(usuario.getRol());

        lblRol.getStyleClass().removeAll("rol-admin", "rol-cajero", "rol-reportes");
        lblRol.getStyleClass().add("badge-rol");

        aplicarPermisosPorRol(usuario.getRol());
    }

    private void aplicarPermisosPorRol(String rol) {
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);
        boolean esCajero = "Cajero".equalsIgnoreCase(rol);
        boolean esReportes = "Reportes".equalsIgnoreCase(rol);

        if (esAdmin) {
            lblRol.getStyleClass().add("rol-admin");
        } else if (esCajero) {
            lblRol.getStyleClass().add("rol-cajero");
        } else if (esReportes) {
            lblRol.getStyleClass().add("rol-reportes");
        }

        setVisibleYManaged(btnReportes, esAdmin || esReportes);
        setVisibleYManaged(btnIrClientes, esAdmin || esCajero);
        setVisibleYManaged(btnIrServicios, esAdmin || esCajero);

        btnCitas.setDisable(esReportes);

        boolean puedeModificar = !esReportes;
        setVisibleYManaged(btnGuardar, puedeModificar);
        setVisibleYManaged(btnActualizar, puedeModificar);
        setVisibleYManaged(btnEliminar, puedeModificar);
        setVisibleYManaged(btnLimpiar, puedeModificar);

        txtNombreCliente.setDisable(esReportes);
        txtTelefono.setDisable(esReportes);
        txtCorreo.setDisable(esReportes);
        txtFechaCita.setDisable(esReportes);
        txtHoraCita.setDisable(esReportes);
        cmbServicios.setDisable(esReportes);
        cmbEstado.setDisable(esReportes);
    }

    private void setVisibleYManaged(Control control, boolean visible) {
        control.setVisible(visible);
        control.setManaged(visible);
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFecha() != null ? data.getValue().getFecha().format(FORMATO_FECHA) : ""));
        colHora.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getHora() != null ? data.getValue().getHora().format(FORMATO_HORA) : ""));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicioNombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tvCitas.setItems(listaCitas);
    }

    private void cargarServicios() {
        List<Servicio> servicios = servicioDao.listarTodos();
        cmbServicios.setItems(FXCollections.observableArrayList(servicios));

        cmbServicios.setConverter(new javafx.util.StringConverter<Servicio>() {
            @Override
            public String toString(Servicio servicio) {
                return servicio != null ? servicio.getNombre() + " ($" + servicio.getPrecio() + ")" : "";
            }

            @Override
            public Servicio fromString(String string) {
                return null;
            }
        });
    }

    private void cargarEstados() {
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Pendiente", "Confirmada", "Completada", "Cancelada"));
    }

    private void cargarCitas() {
        listaCitas.clear();
        listaCitas.addAll(citaDao.listarTodos());
    }

    private void mostrarCitaEnFormulario(Cita cita) {
        citaSeleccionada = cita;

        txtNombreCliente.setText(cita.getClienteNombre());
        txtFechaCita.setText(cita.getFecha() != null ? cita.getFecha().format(FORMATO_FECHA) : "");
        txtHoraCita.setText(cita.getHora() != null ? cita.getHora().format(FORMATO_HORA) : "");
        cmbEstado.setValue(cita.getEstado());

        if (cita.getServicio() != null) {
            for (Servicio s : cmbServicios.getItems()) {
                if (s.getId() == cita.getServicio().getId()) {
                    cmbServicios.setValue(s);
                    break;
                }
            }
        }

        Cliente cliente = clienteDao.listarTodos().stream()
                .filter(c -> c.getId() == cita.getClienteId())
                .findFirst()
                .orElse(null);
        if (cliente != null) {
            txtTelefono.setText(cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo());
        }
    }

    @FXML
    public void handleGuardarCita() {
        Cita cita = construirCitaDesdeFormulario();
        if (cita == null) return;

        boolean exito = citaDao.guardar(cita);

        if (exito) {
            cargarCitas();
            handleLimpiarCita();
            mostrarAlerta("Éxito", "Cita registrada correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar la cita.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleActualizarCita() {
        if (citaSeleccionada == null) {
            mostrarAlerta("Selecciona una cita", "Debes elegir una fila de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        Cita cita = construirCitaDesdeFormulario();
        if (cita == null) return;

        cita.setId(citaSeleccionada.getId());
        boolean exito = citaDao.actualizar(cita);

        if (exito) {
            cargarCitas();
            handleLimpiarCita();
            mostrarAlerta("Éxito", "Cita actualizada correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la cita.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarCita() {
        if (citaSeleccionada == null) {
            mostrarAlerta("Selecciona una cita", "Debes elegir una fila de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar esta cita? Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                boolean exito = citaDao.eliminar(citaSeleccionada.getId());
                if (exito) {
                    cargarCitas();
                    handleLimpiarCita();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la cita.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    public void handleLimpiarCita() {
        txtNombreCliente.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtFechaCita.clear();
        txtHoraCita.clear();
        cmbServicios.setValue(null);
        cmbEstado.setValue(null);
        citaSeleccionada = null;
        tvCitas.getSelectionModel().clearSelection();
    }

    private Cita construirCitaDesdeFormulario() {
        String nombre = txtNombreCliente.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        String fechaTexto = txtFechaCita.getText();
        String horaTexto = txtHoraCita.getText();
        Servicio servicio = cmbServicios.getValue();
        String estado = cmbEstado.getValue();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del cliente es obligatorio.", Alert.AlertType.WARNING);
            return null;
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El teléfono del cliente es obligatorio.", Alert.AlertType.WARNING);
            return null;
        }
        if (servicio == null) {
            mostrarAlerta("Campo requerido", "Selecciona un servicio.", Alert.AlertType.WARNING);
            return null;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto, FORMATO_FECHA);
        } catch (DateTimeParseException | NullPointerException e) {
            mostrarAlerta("Fecha inválida", "Ingresa la fecha en formato DD/MM/AAAA.", Alert.AlertType.WARNING);
            return null;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(horaTexto, FORMATO_HORA);
        } catch (DateTimeParseException | NullPointerException e) {
            mostrarAlerta("Hora inválida", "Ingresa la hora en formato HH:mm (ej. 14:30).", Alert.AlertType.WARNING);
            return null;
        }

        if (estado == null || estado.trim().isEmpty()) {
            estado = "Pendiente";
        }

        int clienteId = obtenerOCrearCliente(nombre, telefono, correo);
        if (clienteId == -1) {
            mostrarAlerta("Error", "No se pudo registrar los datos del cliente.", Alert.AlertType.ERROR);
            return null;
        }

        Cita cita = new Cita(0, clienteId, servicio.getId(), fecha, hora, estado);
        cita.setServicio(servicio);
        cita.setClienteNombre(nombre);
        return cita;
    }

    private int obtenerOCrearCliente(String nombre, String telefono, String correo) {
        Cliente existente = clienteDao.buscarPorTelefono(telefono);
        if (existente != null) {
            return existente.getId();
        }
        return clienteDao.guardarYObtenerId(new Cliente(0, nombre, telefono, correo));
    }

    @FXML
    public void irAClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clientes.fxml"));
            Parent root = loader.load();

            ClienteController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            cambiarEscena(root, "Belleza Elegante - Clientes");
        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo cargar la pantalla de Clientes.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void irAServicios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/servicios.fxml"));
            Parent root = loader.load();

            ServicioController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            cambiarEscena(root, "Belleza Elegante - Servicios");
        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo cargar la pantalla de Servicios.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            cambiarEscena(root, "Belleza Elegante - Iniciar Sesión");
        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo cargar la pantalla de inicio de sesión.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cambiarEscena(Parent root, String titulo) {
        Stage stage = (Stage) lblUsuario.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.centerOnScreen();
        stage.show();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}