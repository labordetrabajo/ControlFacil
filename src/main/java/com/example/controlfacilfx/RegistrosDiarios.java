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

public class RegistrosDiarios extends Application {
    private TextField textFieldDia;
    private TextField textFieldMes;
    private TextField textFieldAño;
    private TableView<RegistroVenta> tableView;

    private Label totalValueLabel; // Declarar totalValueLabel como variable de instancia

    // Métodos para la conexión a la base de datos
    private Connection conectarBaseDatos() throws SQLException {
        // Establecer conexión a la base de datos
        return DriverManager.getConnection("jdbc:mysql://localhost:3308/control_facil", "root", "");
    }

    private ResultSet obtenerRegistrosVentaPorFecha(LocalDate fecha) throws SQLException {
        Connection conn = conectarBaseDatos();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM registrodeventas WHERE fecha = ?");
        statement.setDate(1, Date.valueOf(fecha));
        return statement.executeQuery();
    }

    @Override
    public void start(Stage primaryStage) {
        // Configurar el diseño de la interfaz de usuario
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        // Mensaje de instrucción
        Label labelInstruccion = new Label("Por favor, ingrese la fecha del registro que desea ver:");
        labelInstruccion.setStyle("-fx-font-size: 18px;");
        root.getChildren().add(labelInstruccion);

        // Crear controles para ingresar día, mes y año
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

        // Agregar controles al diseño
        dateGrid.addRow(0, labelDia, textFieldDia, labelMes, textFieldMes, labelAño, textFieldAño);
        root.getChildren().add(dateGrid);

        // Crear un botón para obtener la fecha ingresada
        Button button = new Button("Ver Registro");
        button.setStyle("-fx-font-size: 18px;");
        button.setOnAction(e -> mostrarRegistrosPorFecha());

        // Agregar el botón al diseño
        root.getChildren().add(button);

        // TableView para mostrar los registros de venta
        tableView = new TableView<>();
        tableView.setPrefHeight(500); // Ajuste de altura
        configurarTabla();
        root.getChildren().add(tableView);

        // Crear la escena y mostrarla
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registros de Ventas Diarios");
        primaryStage.show();

        // Crear un Label para mostrar la suma total
        Label totalLabel = new Label("TOTAL: $");
        totalLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: green;");
        totalValueLabel = new Label(""); // Inicializar totalValueLabel
        totalValueLabel.setStyle("-fx-font-size: 30px;");
        HBox totalBox = new HBox(10, totalLabel, totalValueLabel);
        totalBox.setPadding(new Insets(10));

        root.getChildren().add(totalBox); // Agregar el HBox al diseño
    }

    // Método para configurar la tabla
    private void configurarTabla() {
        TableColumn<RegistroVenta, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);
        idColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, Date> fechaColumn = new TableColumn<>("Fecha");
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setPrefWidth(150);
        fechaColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, Time> horaColumn = new TableColumn<>("Hora");
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));
        horaColumn.setPrefWidth(100);
        horaColumn.setStyle("-fx-font-size: 16px;");


        TableColumn<RegistroVenta, String> clienteColumn = new TableColumn<>("Cliente");
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        clienteColumn.setPrefWidth(200);
        clienteColumn.setStyle("-fx-font-size: 16px;");

        // Personalizar cómo se muestra el contenido de la columna del cliente
        clienteColumn.setCellFactory(column -> {
            return new TableCell<RegistroVenta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        // Dividir el texto en id, nombre y apellido
                        String[] parts = item.split("Nombre:");
                        if (parts.length > 1) {
                            String idPart = parts[0];
                            String[] nameParts = parts[1].split("Apellido:");
                            String nombre = nameParts[0];
                            String apellido = nameParts[1];
                            // Formatear la presentación
                            setText(idPart.trim() + "  " + nombre.trim() + " " + apellido.trim());
                        } else {
                            setText(item); // Si no hay coincidencias, mostrar el texto original
                        }
                    }
                }
            };
        });


        TableColumn<RegistroVenta, String> productosColumn = new TableColumn<>("Productos");
        productosColumn.setCellValueFactory(new PropertyValueFactory<>("productos"));
        productosColumn.setPrefWidth(300);
        productosColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setPrefWidth(100);
        totalColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, String> metodoPagoColumn = new TableColumn<>("Método de Pago");
        metodoPagoColumn.setCellValueFactory(new PropertyValueFactory<>("metodoDePago")); // Aquí es donde necesitas cambiar
        metodoPagoColumn.setPrefWidth(150);
        metodoPagoColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, Double> porcentajeColumn = new TableColumn<>("Porcentaje");
        porcentajeColumn.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        porcentajeColumn.setPrefWidth(100);
        porcentajeColumn.setStyle("-fx-font-size: 16px;");

        TableColumn<RegistroVenta, Double> precioFinalColumn = new TableColumn<>("Precio Final");
        precioFinalColumn.setCellValueFactory(new PropertyValueFactory<>("precioFinal"));
        precioFinalColumn.setPrefWidth(120);
        precioFinalColumn.setStyle("-fx-font-size: 16px;");

        tableView.getColumns().addAll(idColumn, fechaColumn, horaColumn, clienteColumn, productosColumn,
                totalColumn, metodoPagoColumn, porcentajeColumn, precioFinalColumn);


    }

    // Método para calcular la suma total del precio final
    private double calcularSumaTotal(ObservableList<RegistroVenta> registros) {
        double sumaTotal = 0;
        for (RegistroVenta registro : registros) {
            sumaTotal += registro.getPrecioFinal();
        }
        return sumaTotal;
    }


    // Método para mostrar los registros de ventas por la fecha ingresada
    // Método para mostrar los registros de ventas por la fecha ingresada
    private void mostrarRegistrosPorFecha() {
        try {
            int dia = Integer.parseInt(textFieldDia.getText());
            int mes = Integer.parseInt(textFieldMes.getText());
            int año = Integer.parseInt(textFieldAño.getText());
            LocalDate fechaIngresada = LocalDate.of(año, mes, dia);
            ResultSet resultSet = obtenerRegistrosVentaPorFecha(fechaIngresada);
            ObservableList<RegistroVenta> registros = FXCollections.observableArrayList();
            while (resultSet.next()) {
                registros.add(new RegistroVenta(
                        resultSet.getInt("id"),
                        resultSet.getDate("fecha"),
                        resultSet.getTime("hora"),
                        resultSet.getString("cliente"),
                        resultSet.getString("productos"),
                        resultSet.getDouble("total"),
                        resultSet.getString("metododepago"),
                        resultSet.getDouble("porcentaje"),
                        resultSet.getDouble("precio_final")
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
