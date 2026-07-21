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
import java.util.Optional;

public class ClienteController {

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente, String> colNombre;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private TableColumn<Cliente, String> colCorreo;

    private final ClienteDao clienteDao = new ClienteDao();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {

        if (usuarioActual == null) return;

        boolean esReportes = "Reportes".equalsIgnoreCase(usuarioActual.getRol());

        txtNombre.setDisable(esReportes);
        txtTelefono.setDisable(esReportes);
        txtCorreo.setDisable(esReportes);

        btnGuardar.setVisible(!esReportes);
        btnGuardar.setManaged(!esReportes);

        btnActualizar.setVisible(!esReportes);
        btnActualizar.setManaged(!esReportes);

        btnEliminar.setVisible(!esReportes);
        btnEliminar.setManaged(!esReportes);
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

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!validarCampos(nombre, telefono, correo)) {
            return;
        }

        if (clienteDao.existePorNombre(nombre)) {
            mostrarAlerta("Duplicado", "Ya existe un cliente con ese nombre.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = clienteDao.guardar(new Cliente(0, nombre, telefono, correo));

        if (exito) {

            cargarClientes();
            limpiarCampos();

            mostrarAlerta("Exito",
                    "Cliente guardado correctamente.",
                    Alert.AlertType.INFORMATION);

        } else {

            mostrarAlerta("Error",
                    "No se pudo guardar el cliente.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleActualizarCliente() {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un cliente",
                    "Debes elegir una fila de la tabla para actualizar.",
                    Alert.AlertType.WARNING);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!validarCampos(nombre, telefono, correo)) {
            return;
        }

        // No duplicados (al actualizar, excluir el propio registro seleccionado)
        for (Cliente c : listaClientes) {
            if (c.getId() != seleccionado.getId() && c.getNombre().equalsIgnoreCase(nombre)) {
                mostrarAlerta("Error", "Ya existe otro cliente con ese nombre.", Alert.AlertType.WARNING);
                return;
            }
        }

        Cliente actualizado = new Cliente(seleccionado.getId(), nombre, telefono, correo);

        boolean exito = clienteDao.actualizar(actualizado);

        if (exito) {

            cargarClientes();
            limpiarCampos();

            mostrarAlerta("Exito",
                    "Cliente actualizado correctamente.",
                    Alert.AlertType.INFORMATION);

        } else {

            mostrarAlerta("Error",
                    "No se pudo actualizar el cliente.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarCliente() {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta("Selecciona un cliente",
                    "Debes elegir una fila de la tabla para eliminar.",
                    Alert.AlertType.WARNING);

            return;
        }

        // Confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar a " + seleccionado.getNombre() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {

            boolean exito = clienteDao.eliminar(seleccionado.getId());

            if (exito) {

                cargarClientes();
                limpiarCampos();

                mostrarAlerta("Exito",
                        "Cliente eliminado correctamente.",
                        Alert.AlertType.INFORMATION);

            } else {

                mostrarAlerta("Error",
                        "No se pudo eliminar el cliente.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarCampos(String nombre, String telefono, String correo) {

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo requerido", "Debe ingresar el nombre.", Alert.AlertType.WARNING);
            return false;
        }

        if (telefono.isEmpty()) {
            mostrarAlerta("Campo requerido", "Debe ingresar el telefono.", Alert.AlertType.WARNING);
            return false;
        }

        if (correo.isEmpty()) {
            mostrarAlerta("Campo requerido", "Debe ingresar el correo.", Alert.AlertType.WARNING);
            return false;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            mostrarAlerta("Error", "El nombre solo debe contener letras.", Alert.AlertType.WARNING);
            return false;
        }

        if (!telefono.matches("[0-9]+")) {
            mostrarAlerta("Error", "El teléfono solo debe contener numeros.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
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