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

        // Cargar datos
        cbCliente.setItems(FXCollections.observableArrayList(new ClienteDao().listarTodos()));
        cbServicio.setItems(FXCollections.observableArrayList(new ServicioDao().listarTodos()));
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "Confirmada", "Cancelada"));

        tablaCitas.setItems(FXCollections.observableArrayList(new CitaDao().listarTodos()));
    }

    @FXML
    public void handleGuardarCita() {
        Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
        Servicio servicioSeleccionado = cbServicio.getSelectionModel().getSelectedItem();

        Cita cita = new Cita();
        cita.setClienteId(clienteSeleccionado.getId());
        cita.setClienteNombre(clienteSeleccionado.getNombre());
        cita.setServicioId(servicioSeleccionado.getId());
        cita.setServicio(servicioSeleccionado);
        cita.setFecha(dpFecha.getValue());
        cita.setHora(LocalTime.parse(txtHora.getText().trim()));
        cita.setEstado(cbEstado.getSelectionModel().getSelectedItem());

        boolean guardado = new CitaDao().guardar(cita);

        if (guardado) {
            tablaCitas.getItems().add(cita);
        } else {
            System.out.println("No se pudo guardar la cita.");
        }
    }

    @FXML
    public void handleEliminarCita() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selecciona una cita");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes elegir una fila de la tabla para eliminar.");
            alerta.showAndWait();
            return;
        }

        boolean exito = new CitaDao().eliminar(seleccionada.getId());

        if (exito) {
            tablaCitas.getItems().remove(seleccionada);
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo eliminar la cita.");
            alerta.showAndWait();
        }
    }

    @FXML
    public void handleImprimirComprobante() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selecciona una cita");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes elegir una fila de la tabla para generar el comprobante.");
            alerta.showAndWait();
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
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo generar el comprobante.");
            alerta.showAndWait();
            e.printStackTrace();
        }
    }
}