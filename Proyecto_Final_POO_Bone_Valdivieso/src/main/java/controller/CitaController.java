package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

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

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
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
}