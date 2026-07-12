package controller;

import dao.CitaDao;
import dao.ClienteDao;
import dao.ServicioDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Cita;
import model.Cliente;
import model.Servicio;
import model.Usuario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportesController {

    @FXML private Label lblTotalCitas;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalServicios;
    @FXML private Label lblIngresos;

    @FXML private PieChart chartEstados;
    @FXML private BarChart<String, Number> chartServicios;
    @FXML private CategoryAxis ejeServicios;
    @FXML private NumberAxis ejeCantidad;

    @FXML private Button btnVolver;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnExportar;

    private final CitaDao citaDao = new CitaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final ServicioDao servicioDao = new ServicioDao();

    private Usuario usuarioActual;

    private List<Cita> citas;
    private List<Cliente> clientes;
    private List<Servicio> servicios;
    private double ingresos;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {
        cargarDatos();
        cargarTarjetas();
        cargarGraficoEstados();
        cargarGraficoServicios();
    }

    private void cargarDatos() {
        citas = citaDao.listarTodos();
        clientes = clienteDao.listarTodos();
        servicios = servicioDao.listarTodos();

        ingresos = 0.0;
        for (Cita c : citas) {
            if ("Completada".equalsIgnoreCase(c.getEstado())) {
                ingresos += c.calcularCosto();
            }
        }
    }

    private void cargarTarjetas() {
        lblTotalCitas.setText(String.valueOf(citas.size()));
        lblTotalClientes.setText(String.valueOf(clientes.size()));
        lblTotalServicios.setText(String.valueOf(servicios.size()));
        lblIngresos.setText(String.format("$%.2f", ingresos));
    }

    private Map<String, Integer> contarPorEstado() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Cita c : citas) {
            String estado = c.getEstado() != null ? c.getEstado() : "Sin estado";
            conteo.merge(estado, 1, Integer::sum);
        }
        return conteo;
    }

    private Map<String, Integer> contarPorServicio() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Cita c : citas) {
            String nombre = (c.getServicio() != null) ? c.getServicio().getNombre() : "N/A";
            conteo.merge(nombre, 1, Integer::sum);
        }
        return conteo;
    }

    private void cargarGraficoEstados() {
        Map<String, Integer> conteo = contarPorEstado();
        javafx.collections.ObservableList<PieChart.Data> datos = javafx.collections.FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            datos.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        chartEstados.setData(datos);
    }

    private void cargarGraficoServicios() {
        Map<String, Integer> conteo = contarPorServicio();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Citas por servicio");

        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartServicios.getData().clear();
        chartServicios.getData().add(serie);
    }

    @FXML
    public void handleExportarReporte() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte");
        fileChooser.setInitialFileName("reporte_belleza_elegante.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));

        Stage stage = (Stage) btnExportar.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) return;

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write("=== REPORTE - BELLEZA ELEGANTE ===\n\n");
            writer.write("Total de citas: " + citas.size() + "\n");
            writer.write("Total de clientes: " + clientes.size() + "\n");
            writer.write("Total de servicios: " + servicios.size() + "\n");
            writer.write(String.format("Ingresos (citas completadas): $%.2f\n\n", ingresos));

            writer.write("--- Citas por estado ---\n");
            for (Map.Entry<String, Integer> entry : contarPorEstado().entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            writer.write("\n--- Citas por servicio ---\n");
            for (Map.Entry<String, Integer> entry : contarPorServicio().entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            mostrarAlerta("Éxito", "Reporte exportado correctamente.", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar el reporte.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void irADashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            cambiarEscena(root, "Belleza Elegante - Panel de Control");
        } catch (IOException e) {
            mostrarAlerta("Error del Sistema", "No se pudo volver al Dashboard.", Alert.AlertType.ERROR);
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
            mostrarAlerta("Error del Sistema", "No se pudo cerrar sesión.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cambiarEscena(Parent root, String titulo) {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
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