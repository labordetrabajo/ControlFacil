package com.example.controlfacilfx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportarAExcelMetodosPago {
    private Connection conexion;

    public ExportarAExcelMetodosPago(Connection conexion) {
        this.conexion = conexion;
    }

    public String exportarMetodosPagoAExcel(Path filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("MetodosPago");

            // Crear la primera fila con los encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Tipo", "Porcentaje"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Obtener datos de la base de datos
            String consulta = "SELECT * FROM metodosdepago";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("nombre"));
                    row.createCell(2).setCellValue(resultSet.getString("tipo"));
                    row.createCell(3).setCellValue(resultSet.getDouble("porcentaje"));
                }

                // Escribir el libro a un archivo
                try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
                    workbook.write(fileOut);
                }

                System.out.println("Datos exportados a Excel con éxito. Ruta del archivo: " + filePath);

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
            ExportarAExcelMetodosPago exportador = new ExportarAExcelMetodosPago(conexion);
            exportador.exportarMetodosPagoAExcel(Paths.get("metodos_pago.xlsx"));
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
