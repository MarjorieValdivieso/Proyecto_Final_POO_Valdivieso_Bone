package controller;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Cita;

import java.time.LocalDate;

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
        lblNumero.setText("" + cita.getId());
        lblFechaEmision.setText(LocalDate.now().toString());
        lblCliente.setText(cita.getClienteNombre());

        lblServicio.setText(cita.getServicio() != null ? cita.getServicio().getNombre() : "-");
        lblFechaCita.setText("" + cita.getFecha());
        lblHora.setText("" + cita.getHora());
        lblEstado.setText(cita.getEstado());

        double subtotal = cita.calcularCosto();
        double iva = subtotal * PORCENTAJE_IVA;
        double total = subtotal + iva;

        lblSubtotal.setText("$" + subtotal);
        lblIva.setText("$" + iva);
        lblTotal.setText("$" + total);
    }

    @FXML
    public void handleImprimir() {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            mostrarAlerta("No se encontró una impresora disponible.");
            return;
        }

        boolean aceptado = job.showPrintDialog(rootFactura.getScene().getWindow());

        if (aceptado) {
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