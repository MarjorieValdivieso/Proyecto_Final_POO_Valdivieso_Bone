import model.Facturable;
import model.Servicio;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita implements Facturable {
    private int id;
    private int clienteId;
    private int servicioId;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    private Servicio servicio;

    public Cita() {}

    public Cita(int id, int clienteId, int servicioId, LocalDate fecha, LocalTime hora, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.servicioId = servicioId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    @Override
    public double calcularCosto() {
        if (servicio != null) {
            return servicio.getPrecio();
        }
        return 0.0;
    }
}