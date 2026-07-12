package controller;

import dao.ClienteDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Cliente;
import model.Usuario;

import java.io.IOException;

public class ClienteController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;

    private final ClienteDao clienteDao = new ClienteDao();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }


    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        tablaClientes.setItems(listaClientes);
        cargarClientes();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtNombre.setText(seleccionado.getNombre());
                txtTelefono.setText(seleccionado.getTelefono());
                txtCorreo.setText(seleccionado.getCorreo());
            }
        });
    }

    private void cargarClientes() {
        listaClientes.clear();
        listaClientes.addAll(clienteDao.listarTodos());
    }

    @FXML
    public void handleGuardarCliente() {
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = clienteDao.guardar(new Cliente(0, nombre, telefono, correo));

        if (exito) {
            cargarClientes();
            limpiarCampos();
            mostrarAlerta("Exito", "Cliente guardado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar el cliente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un cliente", "Debes elegir una fila de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = clienteDao.eliminar(seleccionado.getId());

        if (exito) {
            cargarClientes();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el cliente.", Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        tablaClientes.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}