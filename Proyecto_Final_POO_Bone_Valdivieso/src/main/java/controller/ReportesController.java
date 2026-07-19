package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import dao.CitaDao;
import dao.ClienteDao;
import model.Cita;
import model.Usuario;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportesController {

    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalCitas;
    @FXML private Label lblPendientes;
    @FXML private Label lblConfirmadas;
    @FXML private Label lblCanceladas;
    @FXML private BarChart<String, Number> chartServicios;
    @FXML private Button btnExportar;

    private Usuario usuarioActual;

    // Guardamos los datos aqui para poder usarlos tambien al exportar
    private List<Cita> citas;
    private int totalClientes;
    private int pendientes;
    private int confirmadas;
    private int canceladas;
    private Map<String, Integer> citasPorServicio;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarDatos();
    }

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        citas = new CitaDao().listarTodos();
        totalClientes = new ClienteDao().listarTodos().size();

        pendientes = 0;
        confirmadas = 0;
        canceladas = 0;
        citasPorServicio = new HashMap<>();

        for (Cita c : citas) {
            String estado = c.getEstado();
            if ("Pendiente".equalsIgnoreCase(estado)) pendientes++;
            else if ("Confirmada".equalsIgnoreCase(estado)) confirmadas++;
            else if ("Cancelada".equalsIgnoreCase(estado)) canceladas++;

            String nombreServicio = c.getServicio() != null ? c.getServicio().getNombre() : "Sin servicio";
            citasPorServicio.merge(nombreServicio, 1, Integer::sum);
        }

        lblTotalClientes.setText(String.valueOf(totalClientes));
        lblTotalCitas.setText(String.valueOf(citas.size()));
        lblPendientes.setText(String.valueOf(pendientes));
        lblConfirmadas.setText(String.valueOf(confirmadas));
        lblCanceladas.setText(String.valueOf(canceladas));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Citas por servicio");
        for (Map.Entry<String, Integer> entry : citasPorServicio.entrySet()) {
            serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartServicios.getData().clear();
        chartServicios.getData().add(serie);
    }

    @FXML
    public void handleExportarReporte() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("reporte.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt"));

        Stage stage = (Stage) btnExportar.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        try {
            PrintWriter writer = new PrintWriter(archivo);

            writer.println("BELLEZA ELEGANTE - REPORTE");
            writer.println("----------------------------------");
            writer.println("Total de clientes: " + totalClientes);
            writer.println("Total de citas: " + citas.size());
            writer.println("Pendientes: " + pendientes);
            writer.println("Confirmadas: " + confirmadas);
            writer.println("Canceladas: " + canceladas);
            writer.println();

            writer.println("Citas por servicio:");
            for (String nombreServicio : citasPorServicio.keySet()) {
                writer.println("- " + nombreServicio + ": " + citasPorServicio.get(nombreServicio));
            }
            writer.println();

            writer.println("Detalle de citas:");
            for (Cita c : citas) {
                String servicio = c.getServicio() != null ? c.getServicio().getNombre() : "-";
                writer.println(c.getClienteNombre() + " | " + servicio + " | "
                        + c.getFecha() + " | " + c.getHora() + " | " + c.getEstado());
            }

            writer.close();

            mostrarAlerta("Éxito", "Reporte exportado correctamente.", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar el reporte.", Alert.AlertType.ERROR);
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