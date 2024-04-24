package com.example.controlfacilfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class RegistrosMensuales extends Application {
    private TextField textFieldMes;
    private TextField textFieldAño;
    private TableView<VentaDiaria> tableView;
    private Label totalLabel;

    @Override
    public void start(Stage primaryStage) {
        // Configurar el diseño de la interfaz de usuario
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // Mensaje de instrucción
        Label labelInstruccion = new Label("Por favor, escriba el mes y el año:");
        labelInstruccion.setStyle("-fx-font-size: 18px;");
        root.getChildren().add(labelInstruccion);

        // Crear controles para ingresar mes y año
        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);

        Label labelMes = new Label("Mes:");
        labelMes.setStyle("-fx-font-size: 16px;");
        textFieldMes = new TextField();
        textFieldMes.setPrefWidth(150);
        textFieldMes.setStyle("-fx-font-size: 16px;");
        textFieldMes.setPromptText("Ej. 1, 2, ..., 12");

        Label labelAño = new Label("Año:");
        labelAño.setStyle("-fx-font-size: 16px;");
        textFieldAño = new TextField();
        textFieldAño.setPrefWidth(150);
        textFieldAño.setStyle("-fx-font-size: 16px;");
        textFieldAño.setPromptText("Ej. 2022, 2023, ...");

        // Agregar controles al diseño
        dateGrid.addRow(0, labelMes, textFieldMes, labelAño, textFieldAño);
        root.getChildren().add(dateGrid);

        // Crear un botón para obtener los registros del mes seleccionado
        Button button = new Button("Ver Registros del Mes");
        button.setStyle("-fx-font-size: 18px;");
        button.setOnAction(e -> mostrarRegistrosDelMes());
        root.getChildren().add(button);

        // TableView para mostrar los registros de ventas diarios
        tableView = new TableView<>();
        tableView.setPrefHeight(500); // Ajuste de altura
        configurarTabla();
        root.getChildren().add(tableView);

        // Label para mostrar la suma total de ventas del mes
        totalLabel = new Label("TOTAL: $");
        totalLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: green;");
        root.getChildren().add(totalLabel);

        // Crear la escena y mostrarla
        Scene scene = new Scene(root, 700, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registros de Ventas Mensuales");
        primaryStage.show();
    }

    // Método para configurar la tabla
    private void configurarTabla() {
        TableColumn<VentaDiaria, Date> fechaColumn = new TableColumn<>("Fecha");
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setPrefWidth(200);
        fechaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<VentaDiaria, Double> totalColumn = new TableColumn<>("Total del Día");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setPrefWidth(200);
        totalColumn.setStyle("-fx-font-size: 16px;");

        tableView.getColumns().addAll(fechaColumn, totalColumn);
    }

    // Método para mostrar los registros de ventas del mes seleccionado
    private void mostrarRegistrosDelMes() {
        try {
            int mes = Integer.parseInt(textFieldMes.getText());
            int año = Integer.parseInt(textFieldAño.getText());
            LocalDate fechaInicio = LocalDate.of(año, mes, 1);
            LocalDate fechaFin = fechaInicio.plusMonths(1).minusDays(1);

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/control_facil", "root", "");
            PreparedStatement statement = conn.prepareStatement("SELECT fecha, SUM(precio_final) AS total FROM registrodeventas WHERE fecha BETWEEN ? AND ? GROUP BY fecha");
            statement.setDate(1, Date.valueOf(fechaInicio));
            statement.setDate(2, Date.valueOf(fechaFin));

            ResultSet resultSet = statement.executeQuery();
            ObservableList<VentaDiaria> ventasDiarias = FXCollections.observableArrayList();
            double sumaTotal = 0;
            while (resultSet.next()) {
                ventasDiarias.add(new VentaDiaria(resultSet.getDate("fecha"), resultSet.getDouble("total")));
                sumaTotal += resultSet.getDouble("total");
            }
            tableView.setItems(ventasDiarias);
            totalLabel.setText("TOTAL: $" + sumaTotal);

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            System.out.println("Error: Por favor, ingrese un mes y año válidos.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
