package dao;

import db.Conexion;
import model.Cita;
import model.Servicio;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDao {

    public boolean guardar(Cita cita) {
        String sql = "INSERT INTO citas (cliente_id, servicio_id, fecha, hora, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cita.getClienteId());
            ps.setInt(2, cita.getServicioId());
            ps.setDate(3, Date.valueOf(cita.getFecha()));
            ps.setTime(4, Time.valueOf(cita.getHora()));
            ps.setString(5, cita.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET cliente_id = ?, servicio_id = ?, fecha = ?, hora = ?, estado = ? WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cita.getClienteId());
            ps.setInt(2, cita.getServicioId());
            ps.setDate(3, Date.valueOf(cita.getFecha()));
            ps.setTime(4, Time.valueOf(cita.getHora()));
            ps.setString(5, cita.getEstado());
            ps.setInt(6, cita.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }


    public List<Cita> listarTodos() {
        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT c.id, c.cliente_id, c.servicio_id, c.fecha, c.hora, c.estado, " +
                "cl.nombre AS cliente_nombre, " +
                "s.nombre AS servicio_nombre, s.precio AS servicio_precio, s.duracion AS servicio_duracion " +
                "FROM citas c " +
                "JOIN clientes cl ON cl.id = c.cliente_id " +
                "JOIN servicio s ON s.id = c.servicio_id " +
                "ORDER BY c.fecha, c.hora";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setId(rs.getInt("id"));
                cita.setClienteId(rs.getInt("cliente_id"));
                cita.setServicioId(rs.getInt("servicio_id"));
                cita.setFecha(rs.getDate("fecha").toLocalDate());
                cita.setHora(rs.getTime("hora").toLocalTime());
                cita.setEstado(rs.getString("estado"));
                cita.setClienteNombre(rs.getString("cliente_nombre"));

                Servicio servicio = new Servicio(
                        rs.getInt("servicio_id"),
                        rs.getString("servicio_nombre"),
                        rs.getDouble("servicio_precio"),
                        rs.getString("servicio_duracion")
                );
                cita.setServicio(servicio);

                lista.add(cita);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar citas: " + e.getMessage());
        }

        return lista;
    }

}
