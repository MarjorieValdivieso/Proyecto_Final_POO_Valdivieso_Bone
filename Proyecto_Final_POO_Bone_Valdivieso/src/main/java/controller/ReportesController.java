package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import dao.CitaDao;
import dao.ClienteDao;
import model.Cita;
import model.Usuario;

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

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarDatos();
    }

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        List<Cita> citas = new CitaDao().listarTodos();
        int totalClientes = new ClienteDao().listarTodos().size();

        int pendientes = 0, confirmadas = 0, canceladas = 0;
        Map<String, Integer> citasPorServicio = new HashMap<>();

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
}