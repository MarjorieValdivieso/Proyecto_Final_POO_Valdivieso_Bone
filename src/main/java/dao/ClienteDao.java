package dao;

import db.Conexion;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    public boolean guardar(Cliente cliente) {
        Connection con = Conexion.conectar();
        if (con == null) return false;

        String sql = "INSERT INTO clientes (nombre, telefono, correo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar cliente: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean eliminar(int id) {
        Connection con = Conexion.conectar();
        if (con == null) return false;

        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        Connection con = Conexion.conectar();
        if (con == null) return lista;

        String sql = "SELECT * FROM clientes ORDER BY nombre";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("correo")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }
}