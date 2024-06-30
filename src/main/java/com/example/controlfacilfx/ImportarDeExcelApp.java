package com.example.controlfacilfx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportarDeExcelApp extends Application {

    private Connection conexion;

    // Constructor para inicializar la conexión
    public ImportarDeExcelApp() throws SQLException {
        // Inicializar la conexión a la base de datos aquí
        conexion = ConexionBD.obtenerConexion();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Importar desde Excel");

        // Crear y configurar etiquetas y botones con tamaños de texto más grandes usando CSS en línea
        Label lblSelectFile = new Label("Selecciona un archivo Excel para importar:");
        lblSelectFile.setStyle("-fx-font-size: 18px;");

        Button btnSelectFile = new Button("Seleccionar Archivo Excel");
        btnSelectFile.setStyle("-fx-font-size: 16px;");
        btnSelectFile.setPrefSize(250, 50);

        Label lblImport = new Label("Importa los datos del archivo seleccionado:");
        lblImport.setStyle("-fx-font-size: 18px;");

        Button btnImport = new Button("Importar Datos");
        btnImport.setStyle("-fx-font-size: 16px;");
        btnImport.setPrefSize(250, 50);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        btnSelectFile.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Path filePath = selectedFile.toPath();
                btnImport.setOnAction(ev -> {
                    try {
                        importarDeExcel(filePath);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Importación Exitosa", "Datos importados desde Excel con éxito.");
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Importación", "Hubo un error al importar los datos.");
                    }
                });
            }
        });

        VBox vbox = new VBox(20, lblSelectFile, btnSelectFile, lblImport, btnImport);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Cerrar la conexión a la base de datos aquí
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    public void importarDeExcel(Path filePath) throws IOException, SQLException {
        try (FileInputStream file = new FileInputStream(filePath.toFile());
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            String consulta = "INSERT INTO productos (nombre, cantidad, unidad, precioproveedor, precio, codigodebarra) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        // Saltar la fila de encabezado
                        continue;
                    }

                    String nombre = getCellValueAsString(row.getCell(0));
                    double cantidad = getCellValueAsDouble(row.getCell(1));
                    String unidad = getCellValueAsString(row.getCell(2));
                    double precioproveedor = getCellValueAsDouble(row.getCell(3));
                    double precio = getCellValueAsDouble(row.getCell(4));
                    String codigodebarra = getCellValueAsString(row.getCell(5));

                    statement.setString(1, nombre);
                    statement.setDouble(2, cantidad);
                    statement.setString(3, unidad);
                    statement.setDouble(4, precioproveedor);
                    statement.setDouble(5, precio);
                    statement.setString(6, codigodebarra);

                    statement.addBatch();
                }

                statement.executeBatch();
                System.out.println("Datos importados desde Excel con éxito.");
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) {
            return 0;
        }
        switch (cell.getCellType()) {
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue() ? 1 : 0;
            case FORMULA:
                return cell.getNumericCellValue();
            case BLANK:
                return 0;
            default:
                return 0;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
