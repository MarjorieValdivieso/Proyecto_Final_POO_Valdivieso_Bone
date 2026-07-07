package dao;

import db.Conexion;
import model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDao {

    public boolean guardar(Servicio servicio) {
        Connection con = Conexion.conectar();
        if (con == null) return false;

        String sql = "INSERT INTO servicios (nombre, precio, duracion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setDouble(2, servicio.getPrecio());
            ps.setString(3, servicio.getDuracion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar servicio: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean eliminar(int id) {
        Connection con = Conexion.conectar();
        if (con == null) return false;

        String sql = "DELETE FROM servicios WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Servicio> listarTodos() {
        List<Servicio> lista = new ArrayList<>();
        Connection con = Conexion.conectar();
        if (con == null) return lista;

        String sql = "SELECT * FROM servicios ORDER BY nombre";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Servicio(rs.getInt("id"), rs.getString("nombre"),
                        rs.getDouble("precio"), rs.getString("duracion")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar servicios: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }
}