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
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportesController {

    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalCitas;
    @FXML private Label lblPendientes;
    @FXML private Label lblConfirmadas;
    @FXML private Label lblCanceladas;
    @FXML private BarChart<String, Number> chartServicios;
    @FXML private Button btnExportar;

    private Usuario usuarioActual;

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
        citasPorServicio = new TreeMap<>();

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
        fileChooser.setTitle("Guardar reporte como TXT");
        fileChooser.setInitialFileName("reporte_belleza_elegante.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt"));

        Stage stage = (Stage) btnExportar.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(generarContenidoReporte());
            mostrarAlerta("Éxito", "El reporte se exportó correctamente en:\n" + archivo.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar el reporte.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String generarContenidoReporte() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("      BELLEZA ELEGANTE - REPORTE\n");
        sb.append("========================================\n");
        sb.append("Generado el: ").append(LocalDateTime.now().format(formato)).append("\n\n");

        sb.append("---------- RESUMEN GENERAL ----------\n");
        sb.append("Total de clientes:   ").append(totalClientes).append("\n");
        sb.append("Total de citas:      ").append(citas.size()).append("\n");
        sb.append("Citas pendientes:    ").append(pendientes).append("\n");
        sb.append("Citas confirmadas:   ").append(confirmadas).append("\n");
        sb.append("Citas canceladas:    ").append(canceladas).append("\n\n");

        sb.append("---------- CITAS POR SERVICIO ----------\n");
        for (Map.Entry<String, Integer> entry : citasPorServicio.entrySet()) {
            sb.append(String.format("%-30s %d%n", entry.getKey() + ":", entry.getValue()));
        }
        sb.append("\n");

        sb.append("---------- DETALLE DE CITAS ----------\n");
        sb.append(String.format("%-25s %-25s %-12s %-8s %-12s%n",
                "Cliente", "Servicio", "Fecha", "Hora", "Estado"));
        sb.append("-------------------------------------------------------------------------------\n");

        for (Cita c : citas) {
            String servicio = c.getServicio() != null ? c.getServicio().getNombre() : "-";
            sb.append(String.format("%-25s %-25s %-12s %-8s %-12s%n",
                    c.getClienteNombre(),
                    servicio,
                    c.getFecha() != null ? c.getFecha().toString() : "-",
                    c.getHora() != null ? c.getHora().toString() : "-",
                    c.getEstado()));
        }

        return sb.toString();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}