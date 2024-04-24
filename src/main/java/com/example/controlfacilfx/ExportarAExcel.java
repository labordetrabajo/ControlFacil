package com.example.controlfacilfx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportarAExcel {
    private Connection conexion;

    public ExportarAExcel(Connection conexion) {
        this.conexion = conexion;
    }

    public String exportarAExcel(Path filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cantidad", "Unidad", "Precio"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            String consulta = "SELECT * FROM productos";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("nombre"));
                    row.createCell(2).setCellValue(resultSet.getInt("cantidad"));
                    row.createCell(3).setCellValue(resultSet.getString("unidad"));
                    row.createCell(4).setCellValue(resultSet.getDouble("precio"));
                }

                try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
                    workbook.write(fileOut);
                }

                System.out.println("Datos de productos exportados a Excel con éxito. Ruta del archivo: " + filePath);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath.toString();
    }

    public static void main(String[] args) {
        try {
            Connection conexion = ConexionBD.obtenerConexion();
            ExportarAExcel exportador = new ExportarAExcel(conexion);
            exportador.exportarAExcel(Paths.get("productos.xlsx"));
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
