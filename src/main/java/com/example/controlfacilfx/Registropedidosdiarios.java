package com.example.controlfacilfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;

public class Registropedidosdiarios extends Application {
    private TextField textFieldDia;
    private TextField textFieldMes;
    private TextField textFieldAño;
    private TableView<Pedido> tableView;

    private Label totalValueLabel;

    private Connection conectarBaseDatos() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3308/control_facil", "root", "");
    }

    private ResultSet obtenerRegistrosPedidoPorFecha(LocalDate fecha) throws SQLException {
        Connection conn = conectarBaseDatos();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM registropedidos WHERE fecha = ?");
        statement.setDate(1, Date.valueOf(fecha));
        return statement.executeQuery();
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        Label labelInstruccion = new Label("Por favor, ingrese la fecha de creación de los pedidos que desea ver:");
        labelInstruccion.setStyle("-fx-font-size: 18px;");
        root.getChildren().add(labelInstruccion);

        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);

        Label labelDia = new Label("Día:");
        labelDia.setStyle("-fx-font-size: 16px;");
        textFieldDia = new TextField();
        textFieldDia.setPrefWidth(100);
        textFieldDia.setPrefHeight(30);
        textFieldDia.setStyle("-fx-font-size: 16px;");
        textFieldDia.setPromptText("DD");

        Label labelMes = new Label("Mes:");
        labelMes.setStyle("-fx-font-size: 16px;");
        textFieldMes = new TextField();
        textFieldMes.setPrefWidth(100);
        textFieldMes.setPrefHeight(30);
        textFieldMes.setStyle("-fx-font-size: 16px;");
        textFieldMes.setPromptText("MM");

        Label labelAño = new Label("Año:");
        labelAño.setStyle("-fx-font-size: 16px;");
        textFieldAño = new TextField();
        textFieldAño.setPrefWidth(120);
        textFieldAño.setPrefHeight(30);
        textFieldAño.setStyle("-fx-font-size: 16px;");
        textFieldAño.setPromptText("AAAA");

        dateGrid.addRow(0, labelDia, textFieldDia, labelMes, textFieldMes, labelAño, textFieldAño);
        root.getChildren().add(dateGrid);

        Button button = new Button("Ver Registro");
        button.setStyle("-fx-font-size: 18px;");
        button.setOnAction(e -> mostrarRegistrosPorFecha());

        root.getChildren().add(button);

        tableView = new TableView<>();
        tableView.setPrefHeight(500);
        configurarTabla();
        root.getChildren().add(tableView);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registros de Pedidos Diarios");
        primaryStage.show();

        Label totalLabel = new Label("TOTAL: $");
        totalLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: green;");
        totalValueLabel = new Label("");
        totalValueLabel.setStyle("-fx-font-size: 30px;");
        HBox totalBox = new HBox(10, totalLabel, totalValueLabel);
        totalBox.setPadding(new Insets(10));

        root.getChildren().add(totalBox);
    }

    private void configurarTabla() {
        TableColumn<Pedido, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);
        idColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> fechaColumn = new TableColumn<>("Fecha");
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setPrefWidth(150);
        fechaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> horaColumn = new TableColumn<>("Hora");
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));
        horaColumn.setPrefWidth(100);
        horaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> clienteColumn = new TableColumn<>("Cliente");
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        clienteColumn.setPrefWidth(200);
        clienteColumn.setStyle("-fx-font-size: 16px;");

        clienteColumn.setCellFactory(column -> {
            return new TableCell<Pedido, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        String[] parts = item.split("Nombre:");
                        if (parts.length > 1) {
                            String idPart = parts[0];
                            String[] nameParts = parts[1].split("Apellido:");
                            String nombre = nameParts[0];
                            String apellido = nameParts[1];
                            setText(idPart.trim() + "  " + nombre.trim() + " " + apellido.trim());
                        } else {
                            setText(item);
                        }
                    }
                }
            };
        });

        TableColumn<Pedido, String> productosColumn = new TableColumn<>("Productos");
        productosColumn.setCellValueFactory(new PropertyValueFactory<>("productos"));
        productosColumn.setPrefWidth(300);
        productosColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setPrefWidth(100);
        totalColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> metodoPagoColumn = new TableColumn<>("Método de Pago");
        metodoPagoColumn.setCellValueFactory(new PropertyValueFactory<>("metodoDePago"));
        metodoPagoColumn.setPrefWidth(150);
        metodoPagoColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, Double> porcentajeColumn = new TableColumn<>("Porcentaje");
        porcentajeColumn.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        porcentajeColumn.setPrefWidth(100);
        porcentajeColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, Double> precioFinalColumn = new TableColumn<>("Precio Final");
        precioFinalColumn.setCellValueFactory(new PropertyValueFactory<>("precioFinal"));
        precioFinalColumn.setPrefWidth(120);
        precioFinalColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> estadoColumn = new TableColumn<>("Estado");
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoColumn.setPrefWidth(100);
        estadoColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> horarioEntregaColumn = new TableColumn<>("Horario Entrega");
        horarioEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("horarioEntrega"));
        horarioEntregaColumn.setPrefWidth(150);
        horarioEntregaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> fechaEntregaColumn = new TableColumn<>("Fecha Entrega");
        fechaEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        fechaEntregaColumn.setPrefWidth(150);
        fechaEntregaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> hipervinculoColumn = new TableColumn<>("Hipervínculo");
        hipervinculoColumn.setCellValueFactory(new PropertyValueFactory<>("hipervinculo"));
        hipervinculoColumn.setPrefWidth(200);
        hipervinculoColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> vehiculoColumn = new TableColumn<>("Vehículo");
        vehiculoColumn.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
        vehiculoColumn.setPrefWidth(150);
        vehiculoColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<Pedido, String> direccionEntregaColumn = new TableColumn<>("Dirección Entrega");
        direccionEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("direccionEntrega"));
        direccionEntregaColumn.setPrefWidth(200);
        direccionEntregaColumn.setStyle("-fx-font-size: 16px;");

        tableView.getColumns().addAll(idColumn, fechaColumn, horaColumn, clienteColumn, productosColumn,
                totalColumn, metodoPagoColumn, porcentajeColumn, precioFinalColumn, estadoColumn, horarioEntregaColumn,
                fechaEntregaColumn, hipervinculoColumn, vehiculoColumn, direccionEntregaColumn);
    }

    private double calcularSumaTotal(ObservableList<Pedido> registros) {
        double sumaTotal = 0;
        for (Pedido pedido : registros) {
            sumaTotal += pedido.getPrecioFinal();
        }
        return sumaTotal;
    }

    private void mostrarRegistrosPorFecha() {
        String dia = textFieldDia.getText();
        String mes = textFieldMes.getText();
        String año = textFieldAño.getText();

        if (dia.isEmpty() || mes.isEmpty() || año.isEmpty()) {
            System.out.println("Error: Todos los campos de fecha son requeridos");
            return;
        }

        try {
            int diaInt = Integer.parseInt(dia);
            int mesInt = Integer.parseInt(mes);
            int añoInt = Integer.parseInt(año);
            LocalDate fecha = LocalDate.of(añoInt, mesInt, diaInt);

            ResultSet resultSet = obtenerRegistrosPedidoPorFecha(fecha);
            ObservableList<Pedido> registros = FXCollections.observableArrayList();
            while (resultSet.next()) {
                registros.add(new Pedido(
                        resultSet.getInt("id"),
                        resultSet.getString("fecha"),
                        resultSet.getString("hora"),
                        resultSet.getString("cliente"),
                        resultSet.getString("productos"),
                        resultSet.getDouble("total"),
                        resultSet.getString("metodoDePago"),
                        resultSet.getDouble("porcentaje"),
                        resultSet.getDouble("precio_final"),
                        resultSet.getString("estado"),
                        resultSet.getString("horario_entrega"),
                        resultSet.getString("fecha_entrega"),
                        resultSet.getString("hipervinculo"),
                        resultSet.getString("vehiculo"),
                        resultSet.getString("direccion_entrega")
                ));
            }
            tableView.setItems(registros);

            // Calcular la suma total y mostrarla en totalValueLabel
            double sumaTotal = calcularSumaTotal(registros);
            totalValueLabel.setText(String.format("%.2f", sumaTotal));

        } catch (NumberFormatException ex) {
            System.out.println("Error: Formato de fecha inválido");
        } catch (DateTimeException ex) {
            System.out.println("Error: Fecha inválida");
        } catch (SQLException ex) {
            System.out.println("Error al obtener registros de la base de datos: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
