package com.example.controlfacilfx;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteManager {
    private Connection conexion;

    public ClienteManager(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarCliente(Cliente cliente) {
        try {
            String consulta = "INSERT INTO clientes (nombre, apellido, documento, direccion, telefono) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setString(1, cliente.getNombre());
                statement.setString(2, cliente.getApellido());
                statement.setInt(3, cliente.getDocumento());
                statement.setString(4, cliente.getDireccion());
                statement.setInt(5, cliente.getTelefono());
                statement.executeUpdate();
            }
            System.out.println("Cliente agregado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarCliente(int idCliente) {
        try {
            String consulta = "DELETE FROM clientes WHERE id = ?";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setInt(1, idCliente);
                int filasAfectadas = statement.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Cliente eliminado con éxito.");
                } else {
                    System.out.println("Cliente no encontrado con el ID proporcionado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Cliente> obtenerClientes() {
        List<Cliente> listaClientes = new ArrayList<>();
        try {
            String consulta = "SELECT * FROM clientes";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Cliente cliente = new Cliente(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellido"),
                            resultSet.getInt("documento"),
                            resultSet.getString("direccion"),
                            resultSet.getInt("telefono")
                    );
                    listaClientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaClientes;
    }

    // Puedes agregar métodos adicionales según tus necesidades

}
