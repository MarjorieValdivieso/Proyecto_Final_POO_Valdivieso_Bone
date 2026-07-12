package dao;

import db.Conexion;
import model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDao {

    public List<Servicio> listarTodos() {

        List<Servicio> lista = new ArrayList<>();

        String sql = "SELECT * FROM servicios ORDER BY id";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Servicio servicio = new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("duracion")
                );

                lista.add(servicio);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean guardar(Servicio servicio) {

        String sql = "INSERT INTO servicios(nombre, precio, duracion) VALUES (?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getNombre());
            ps.setDouble(2, servicio.getPrecio());
            ps.setString(3, servicio.getDuracion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql = "DELETE FROM servicios WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}