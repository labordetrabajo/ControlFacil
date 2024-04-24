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

public class ExportarAExcelClientes {
    private Connection conexion;

    public ExportarAExcelClientes(Connection conexion) {
        this.conexion = conexion;
    }

    public String exportarClientesAExcel(Path filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Apellido", "Documento", "Dirección", "Teléfono"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            String consulta = "SELECT * FROM clientes";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("nombre"));
                    row.createCell(2).setCellValue(resultSet.getString("apellido"));
                    row.createCell(3).setCellValue(resultSet.getInt("documento"));
                    row.createCell(4).setCellValue(resultSet.getString("direccion"));
                    row.createCell(5).setCellValue(resultSet.getInt("telefono"));
                }

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
}
