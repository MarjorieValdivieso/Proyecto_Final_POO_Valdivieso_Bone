package controller;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

public class ReportesController {

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colClienteCita;
    @FXML private TableColumn<Cita, String> colEstado;

    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, String> colNombreServicio;
    @FXML private TableColumn<Servicio, Double> colPrecio;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colNombreCliente;
    @FXML private TableColumn<Cliente, String> colTelefono;

    private final CitaDao citaDao = new CitaDao();
    private final ServicioDao servicioDao = new ServicioDao();
    private final ClienteDao clienteDao = new ClienteDao();

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarReportes();
    }

    private void configurarColumnas() {
        colClienteCita.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colNombreServicio.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    private void cargarReportes() {
        tablaCitas.setItems(FXCollections.observableArrayList(citaDao.listarTodos()));
        tablaServicios.setItems(FXCollections.observableArrayList(servicioDao.listarTodos()));
        tablaClientes.setItems(FXCollections.observableArrayList(clienteDao.listarTodos()));
    }
}
