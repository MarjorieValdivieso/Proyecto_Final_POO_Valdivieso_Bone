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

    public Cliente buscarPorTelefono(String telefono) {
        String sql = "SELECT * FROM clientes WHERE telefono = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, telefono);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setCorreo(rs.getString("correo"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por telefono: " + e.getMessage());
        }
        return null;
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setCorreo(rs.getString("correo"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por id: " + e.getMessage());
        }
        return null;
    }

    public int guardarYObtenerId(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, telefono, correo) VALUES (?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar cliente y obtener ID: " + e.getMessage());
        }
        return -1;
    }
}