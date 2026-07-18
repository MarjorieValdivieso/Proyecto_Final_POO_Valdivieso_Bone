package controller;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Cita;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FacturaController {

    @FXML private VBox rootFactura;

    @FXML private Label lblNumero;
    @FXML private Label lblFechaEmision;
    @FXML private Label lblCliente;

    @FXML private Label lblServicio;
    @FXML private Label lblFechaCita;
    @FXML private Label lblHora;
    @FXML private Label lblEstado;

    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    private static final double PORCENTAJE_IVA = 0.15;

    public void setCita(Cita cita) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        lblNumero.setText(String.format("%04d", cita.getId()));
        lblFechaEmision.setText(LocalDate.now().format(formatoFecha));
        lblCliente.setText(cita.getClienteNombre());

        lblServicio.setText(cita.getServicio() != null ? cita.getServicio().getNombre() : "-");
        lblFechaCita.setText(cita.getFecha() != null ? cita.getFecha().format(formatoFecha) : "-");
        lblHora.setText(cita.getHora() != null ? cita.getHora().toString() : "-");
        lblEstado.setText(cita.getEstado());

        double subtotal = cita.calcularCosto();
        double iva = subtotal * PORCENTAJE_IVA;
        double total = subtotal + iva;

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblIva.setText(String.format("$%.2f", iva));
        lblTotal.setText(String.format("$%.2f", total));
    }

    @FXML
    public void handleImprimir() {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(rootFactura.getScene().getWindow())) {
            boolean impreso = job.printPage(rootFactura);
            if (impreso) {
                job.endJob();
            } else {
                mostrarAlerta("No se pudo imprimir el comprobante.");
            }
        }
    }

    @FXML
    public void handleCerrar() {
        rootFactura.getScene().getWindow().hide();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}