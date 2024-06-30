package com.example.controlfacilfx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.IntStream;

public class ListaPedidos extends Stage {

    private ObservableList<Pedido> pedidosList;
    private TableView<Pedido> tableView;
    private TextField filtroTextField;
    private Connection conexion;

    private boolean cambiosPendientes = false;
    private DatePicker filtroFechaEntrega;
    private ComboBox<String> filtroEstado;

    // Variables para los controles de mes y año
    private ComboBox<Integer> mesComboBox;
    private ComboBox<Integer> añoComboBox;

    public void mostrarVentana() {
        this.show();
    }

    public ListaPedidos(Connection conexion) {
        this.conexion = conexion;
        initUI();
    }

    private void initUI() {
        setTitle("Lista de Pedidos");
        setWidth(1200);
        setHeight(800);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // Controles para la selección de mes y año
        HBox mesAnoHBox = new HBox();
        mesAnoHBox.setSpacing(10);

        Label mesLabel = new Label("Seleccionar Mes:");
        mesLabel.setStyle("-fx-font-size: 16px;");
        mesComboBox = new ComboBox<>();
        mesComboBox.getItems().setAll(IntStream.rangeClosed(1, 12).boxed().toArray(Integer[]::new));
        mesComboBox.setStyle("-fx-font-size: 16px;");

        Label añoLabel = new Label("Seleccionar Año:");
        añoLabel.setStyle("-fx-font-size: 16px;");
        añoComboBox = new ComboBox<>();
        añoComboBox.getItems().setAll(IntStream.rangeClosed(2024, 2030).boxed().toArray(Integer[]::new));
        añoComboBox.setStyle("-fx-font-size: 16px;");

        // Añadimos los componentes al HBox
        mesAnoHBox.getChildren().addAll(mesLabel, mesComboBox, añoLabel, añoComboBox);

        Button cargarButton = new Button("Cargar Pedidos");
        cargarButton.setStyle("-fx-font-size: 16px;");
        cargarButton.setOnAction(event -> {
            Integer mes = mesComboBox.getValue();
            Integer año = añoComboBox.getValue();
            if (mes != null && año != null) {
                System.out.println("Botón de cargar pedidos presionado.");
                obtenerPedidosDeBaseDeDatos(mes, año);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Selección Incompleta");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, seleccione un mes y un año.");
                alert.showAndWait();
            }
        });

        Label filtroLabel = new Label("Filtrar por cliente:");
        filtroLabel.setStyle("-fx-font-size: 16px;");
        filtroLabel.setMaxWidth(180);  // Ajusta el ancho mínimo
        filtroTextField = new TextField();
        filtroTextField.setStyle("-fx-font-size: 16px;");
        filtroTextField.setMaxWidth(170);

        Label filtroFechaLabel = new Label("Filtrar por fecha de entrega:");
        filtroFechaLabel.setStyle("-fx-font-size: 16px;");
        filtroFechaEntrega = new DatePicker();
        filtroFechaEntrega.setStyle("-fx-font-size: 16px;");

        Label filtroEstadoLabel = new Label("Filtrar por estado:");
        filtroEstadoLabel.setStyle("-fx-font-size: 16px;");
        filtroEstado = new ComboBox<>();
        filtroEstado.setItems(obtenerEstadosDeBaseDeDatos());
        filtroEstado.getItems().add(0, "Todos");
        filtroEstado.setStyle("-fx-font-size: 16px;");

        configureTable();

        // Añadimos todos los componentes al VBox
        vbox.getChildren().addAll(mesAnoHBox, cargarButton, filtroLabel, filtroTextField, filtroFechaLabel, filtroFechaEntrega, filtroEstadoLabel, filtroEstado, tableView);

        Scene scene = new Scene(vbox, 1200, 800);
        setScene(scene);

        configureFiltering();
    }

    private static class HyperlinkTableCell extends TableCell<Pedido, Hyperlink> {
        @Override
        protected void updateItem(Hyperlink item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(item);
            }
        }
    }

    private void configureTable() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.getStyleClass().add("table-view");
        tableView.setStyle("-fx-font-size: 15px;");

        TableColumn<Pedido, String> fechaColumn = new TableColumn<>("Fecha");
        TableColumn<Pedido, String> horaColumn = new TableColumn<>("Hora");
        TableColumn<Pedido, String> clienteColumn = new TableColumn<>("Cliente");
        TableColumn<Pedido, String> productosColumn = new TableColumn<>("Productos");
        TableColumn<Pedido, Double> totalColumn = new TableColumn<>("Total");
        TableColumn<Pedido, String> metododepagoColumn = new TableColumn<>("Método de Pago");
        TableColumn<Pedido, Double> porcentajeColumn = new TableColumn<>("Porcentaje");
        TableColumn<Pedido, Double> precioFinalColumn = new TableColumn<>("Precio Final");
        TableColumn<Pedido, String> estadoColumn = new TableColumn<>("Estado");
        TableColumn<Pedido, String> horarioEntregaColumn = new TableColumn<>("Horario de Entrega");
        TableColumn<Pedido, String> fechaEntregaColumn = new TableColumn<>("Fecha de Entrega");
        TableColumn<Pedido, Hyperlink> hipervinculoColumn = new TableColumn<>("Hipervínculo");
        TableColumn<Pedido, String> vehiculoColumn = new TableColumn<>("Vehículo");
        TableColumn<Pedido, String> direccionEntregaColumn = new TableColumn<>("Dirección de Entrega");
        TableColumn<Pedido, Void> eliminarColumn = new TableColumn<>("Eliminar");

        fechaColumn.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        horaColumn.setCellValueFactory(cellData -> cellData.getValue().horaProperty());
        clienteColumn.setCellValueFactory(cellData -> {
            Pedido pedido = cellData.getValue();
            String cliente = pedido.getCliente();
            String nombreApellido = cliente.replaceAll("ID:\\s*\\d+\\s*Nombre:\\s*(\\w+)Apellido:\\s*(\\w+).*", "$1 $2").trim();
            return new SimpleStringProperty(nombreApellido);
        });
        productosColumn.setCellValueFactory(cellData -> cellData.getValue().productosProperty());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());
        metododepagoColumn.setCellValueFactory(cellData -> cellData.getValue().metodoDePagoProperty());
        porcentajeColumn.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        precioFinalColumn.setCellValueFactory(cellData -> cellData.getValue().precioFinalProperty().asObject());
        estadoColumn.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        horarioEntregaColumn.setCellValueFactory(cellData -> cellData.getValue().horarioEntregaProperty());
        fechaEntregaColumn.setCellValueFactory(cellData -> cellData.getValue().fechaEntregaProperty());
        hipervinculoColumn.setCellValueFactory(cellData -> {
            String hipervinculo = cellData.getValue().getHipervinculo();
            Hyperlink hyperlink = new Hyperlink(hipervinculo);
            hyperlink.setOnAction(event -> {
                // Abrir el navegador con el enlace al hacer clic
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(hipervinculo));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
            return new SimpleObjectProperty<>(hyperlink);
        });
        vehiculoColumn.setCellValueFactory(cellData -> cellData.getValue().vehiculoProperty());
        direccionEntregaColumn.setCellValueFactory(cellData -> cellData.getValue().direccionEntregaProperty());

        fechaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        horaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        clienteColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productosColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        totalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        metododepagoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        porcentajeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        precioFinalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        hipervinculoColumn.setCellFactory(new Callback<TableColumn<Pedido, Hyperlink>, TableCell<Pedido, Hyperlink>>() {
            @Override
            public TableCell<Pedido, Hyperlink> call(TableColumn<Pedido, Hyperlink> param) {
                return new HyperlinkTableCell();
            }
        });

        horarioEntregaColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // Permitir edición de horario de entrega
        fechaEntregaColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // Permitir edición de fecha de entrega
        vehiculoColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // Permitir edición de vehículo
        direccionEntregaColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // Permitir edición de dirección de entrega

        ObservableList<String> estadosList = obtenerEstadosDeBaseDeDatos();
        estadoColumn.setCellFactory(ComboBoxTableCell.forTableColumn(estadosList));

        addEditCommitHandler(fechaColumn, "fecha");
        addEditCommitHandler(horaColumn, "hora");
        addEditCommitHandler(clienteColumn, "cliente");
        addEditCommitHandler(productosColumn, "productos");
        addEditCommitHandler(totalColumn, "total");
        addEditCommitHandler(metododepagoColumn, "metododepago");
        addEditCommitHandler(porcentajeColumn, "porcentaje");
        addEditCommitHandler(precioFinalColumn, "precio_final");
        addEditCommitHandler(estadoColumn, "estado");
        addEditCommitHandler(horarioEntregaColumn, "horario_entrega"); // Añadir manejador para horario de entrega
        addEditCommitHandler(fechaEntregaColumn, "fecha_entrega"); // Añadir manejador para fecha de entrega
        addEditCommitHandler(hipervinculoColumn, "hipervinculo"); // Añadir manejador para hipervinculo
        addEditCommitHandler(vehiculoColumn, "vehiculo"); // Añadir manejador para vehículo
        addEditCommitHandler(direccionEntregaColumn, "direccion_entrega"); // Añadir manejador para dirección de entrega

        agregarBotonEliminar(eliminarColumn);

        // Ajuste de ancho de columnas
        clienteColumn.setPrefWidth(100);
        estadoColumn.setPrefWidth(150);
        productosColumn.setPrefWidth(150);
        metododepagoColumn.setPrefWidth(100);
        porcentajeColumn.setPrefWidth(100);
        precioFinalColumn.setPrefWidth(120);
        fechaEntregaColumn.setPrefWidth(100);
        hipervinculoColumn.setPrefWidth(100);
        direccionEntregaColumn.setPrefWidth(120);

        tableView.getColumns().addAll(fechaColumn, horaColumn, clienteColumn, productosColumn, totalColumn, metododepagoColumn, porcentajeColumn, precioFinalColumn, estadoColumn, horarioEntregaColumn, fechaEntregaColumn, hipervinculoColumn, vehiculoColumn, direccionEntregaColumn, eliminarColumn);
        tableView.setEditable(true);
        tableView.setPrefSize(1100, 600);

        tableView.setRowFactory(tv -> new TableRow<Pedido>() {
            @Override
            protected void updateItem(Pedido pedido, boolean empty) {
                super.updateItem(pedido, empty);
                if (pedido == null || empty) {
                    setStyle("");
                } else {
                    switch (pedido.getEstado().toLowerCase()) {
                        case "pendiente":
                            setStyle("-fx-background-color: #ADA605;");
                            break;
                        case "en proceso":
                            setStyle("-fx-background-color: orange;");
                            break;
                        case "listo para retirar":
                            setStyle("-fx-background-color: #C0B912;");
                            break;
                        case "listo para entregar":
                            setStyle("-fx-background-color: violet;");
                            break;
                        case "en camino":
                            setStyle("-fx-background-color: #ADD8E6;"); // Azul suave (LightBlue)
                            break;
                        case "entregado":
                            setStyle("-fx-background-color: #229804;");
                            break;
                        case "finalizado":
                            setStyle("-fx-background-color: gray;");
                            break;
                        case "cancelado":
                            setStyle("-fx-background-color: #FFA07A;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }


    private void obtenerPedidosDeBaseDeDatos(int mes, int año) {
        String consulta = "SELECT * FROM registropedidos WHERE MONTH(STR_TO_DATE(fecha_entrega, '%d/%m/%Y')) = ? AND YEAR(STR_TO_DATE(fecha_entrega, '%d/%m/%Y')) = ?";
        try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
            statement.setInt(1, mes);
            statement.setInt(2, año);

            ResultSet resultSet = statement.executeQuery();
            pedidosList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Pedido pedido = new Pedido(
                        resultSet.getInt("id"),
                        resultSet.getString("fecha"),
                        resultSet.getString("hora"),
                        resultSet.getString("cliente"),
                        resultSet.getString("productos"),
                        resultSet.getDouble("total"),
                        resultSet.getString("metododepago"),
                        resultSet.getDouble("porcentaje"),
                        resultSet.getDouble("precio_final"),
                        resultSet.getString("estado"),
                        resultSet.getString("horario_entrega"),
                        resultSet.getString("fecha_entrega"),
                        resultSet.getString("hipervinculo"),
                        resultSet.getString("vehiculo"),
                        resultSet.getString("direccion_entrega")
                );

                pedidosList.add(pedido);
                System.out.println("Pedido añadido: " + pedido);
            }

            if (pedidosList.isEmpty()) {
                System.out.println("No se encontraron pedidos para el mes: " + mes + " y año: " + año);
            }

            tableView.setItems(pedidosList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> obtenerEstadosDeBaseDeDatos() {
        ObservableList<String> estadosList = FXCollections.observableArrayList();
        try {
            String consulta = "SELECT DISTINCT estado FROM registropedidos";
            try (PreparedStatement statement = conexion.prepareStatement(consulta);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String estado = resultSet.getString("estado");
                    estadosList.add(estado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estadosList;
    }

    private void configureFiltering() {
        filtroTextField.textProperty().addListener((observable, oldValue, newValue) -> filtrarPedidos());
        filtroFechaEntrega.valueProperty().addListener((observable, oldValue, newValue) -> filtrarPedidos());
        filtroEstado.valueProperty().addListener((observable, oldValue, newValue) -> filtrarPedidos());
    }

    private void filtrarPedidos() {
        String filtroCliente = filtroTextField.getText();
        LocalDate filtroFecha = filtroFechaEntrega.getValue();
        String filtroEstadoSeleccionado = filtroEstado.getValue();

        ObservableList<Pedido> pedidosFiltrados = FXCollections.observableArrayList(pedidosList);

        if (filtroCliente != null && !filtroCliente.isEmpty()) {
            pedidosFiltrados = pedidosFiltrados.filtered(pedido -> pedido.getCliente().toLowerCase().contains(filtroCliente.toLowerCase()));
        }

        if (filtroFecha != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy"); // Usar el mismo formato que en la clase Pedido
            String filtroFechaStr = filtroFecha.format(formatter);
            pedidosFiltrados = pedidosFiltrados.filtered(pedido -> {
                LocalDate fechaEntrega = pedido.getFechaEntregaLocalDate();
                if (fechaEntrega != null) {
                    String fechaEntregaStr = fechaEntrega.format(formatter);
                    return fechaEntregaStr.equals(filtroFechaStr);
                }
                return false;
            });
        }

        if (filtroEstadoSeleccionado != null && !filtroEstadoSeleccionado.equals("Todos")) {
            pedidosFiltrados = pedidosFiltrados.filtered(pedido -> pedido.getEstado().equals(filtroEstadoSeleccionado));
        }

        tableView.setItems(pedidosFiltrados);
    }

    private <T> void addEditCommitHandler(TableColumn<Pedido, T> column, String columnName) {
        column.setOnEditCommit(event -> {
            Pedido pedido = event.getRowValue();
            T newValue = event.getNewValue();

            // Mostrar una alerta antes de guardar los cambios
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar cambios");
            alert.setHeaderText("¿Deseas guardar los cambios?");
            alert.setContentText("Si seleccionas 'Aceptar', los cambios se guardarán.");

            ButtonType buttonTypeGuardar = new ButtonType("Aceptar");
            ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeGuardar, buttonTypeCancelar);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeGuardar) {
                // Guardar los cambios
                actualizarPedidoEnBaseDeDatos(pedido.getId(), columnName, newValue);
                // Marcar que hay cambios pendientes
                cambiosPendientes = true;
                // Actualizar el valor en la tabla
                switch (columnName) {
                    case "fecha":
                        pedido.setFecha((String) newValue);
                        break;
                    case "hora":
                        pedido.setHora((String) newValue);
                        break;
                    case "cliente":
                        pedido.setCliente((String) newValue);
                        break;
                    case "productos":
                        pedido.setProductos((String) newValue);
                        break;
                    case "total":
                        pedido.setTotal((Double) newValue);
                        break;
                    case "metododepago":
                        pedido.setMetodoDePago((String) newValue);
                        break;
                    case "porcentaje":
                        pedido.setPorcentaje((Double) newValue);
                        break;
                    case "precio_final":
                        pedido.setPrecioFinal((Double) newValue);
                        break;
                    case "estado":
                        pedido.setEstado((String) newValue);
                        break;
                    case "horario_entrega":
                        pedido.setHorarioEntrega((String) newValue);
                        break;
                    case "fecha_entrega":
                        pedido.setFechaEntrega((String) newValue);
                        break;
                    case "hipervinculo":
                        pedido.setHipervinculo((String) newValue); // Manejar edición del hipervinculo
                        break;
                    case "vehiculo":
                        pedido.setVehiculo((String) newValue);
                        break;
                    case "direccion_entrega":
                        pedido.setDireccionEntrega((String) newValue);
                        break;
                }
            } else {
                // Cancelar los cambios
                tableView.refresh(); // Actualizar la tabla para reflejar el valor anterior
            }
        });
    }

    private <T> void actualizarPedidoEnBaseDeDatos(int id, String columnName, T newValue) {
        try {
            String consulta = "UPDATE registropedidos SET " + columnName + " = ? WHERE id = ?";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setObject(1, newValue);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarBotonEliminar(TableColumn<Pedido, Void> eliminarColumn) {
        eliminarColumn.setCellFactory(param -> new TableCell<>() {
            private final Button eliminarButton = new Button("Eliminar");

            {
                eliminarButton.setOnAction(event -> {
                    Pedido pedido = getTableView().getItems().get(getIndex());

                    // Mostrar una alerta antes de eliminar el pedido
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar eliminación");
                    alert.setHeaderText("¿Deseas eliminar este pedido?");
                    alert.setContentText("Si seleccionas 'Aceptar', el pedido será eliminado.");

                    ButtonType buttonTypeEliminar = new ButtonType("Aceptar");
                    ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeCancelar);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeEliminar) {
                        eliminarPedidoDeBaseDeDatos(pedido.getId());
                        getTableView().getItems().remove(pedido);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(eliminarButton);
                }
            }
        });
    }


    private void eliminarPedidoDeBaseDeDatos(int id) {
        // Mostrar una alerta antes de eliminar el pedido
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Deseas eliminar este pedido?");
        alert.setContentText("Si seleccionas 'Aceptar', el pedido será eliminado.");

        ButtonType buttonTypeEliminar = new ButtonType("Aceptar");
        ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeCancelar);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeEliminar) {
            try {
                String consulta = "DELETE FROM registropedidos WHERE id = ?";
                try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
                // Eliminar la fila de la tabla si se confirma la eliminación en la base de datos
                Pedido pedidoAEliminar = pedidosList.stream().filter(pedido -> pedido.getId() == id).findFirst().orElse(null);
                if (pedidoAEliminar != null) {
                    pedidosList.remove(pedidoAEliminar);
                }
                // Marcar que hay cambios pendientes
                cambiosPendientes = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
