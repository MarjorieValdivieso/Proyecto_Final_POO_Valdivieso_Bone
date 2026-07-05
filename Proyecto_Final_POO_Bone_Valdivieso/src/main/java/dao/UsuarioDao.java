package dao;

import db.Conexion;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    public Usuario autenticarUsuario(String nombreUsuario, String clave, String rol) {
        Connection con = Conexion.conectar();
        if (con == null) return null;

        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND clave = ? AND rol = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, clave);
            ps.setString(3, rol);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setClave(rs.getString("clave"));
                usuario.setRol(rs.getString("rol"));
                return usuario;
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL al autenticar: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}