package com.example.controlfacilfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.sql.*;
import java.util.Optional;

public class ListaMetodosPago extends Stage {

    private ObservableList<MetodoPago> metodosPagoList;
    private TableView<MetodoPago> tableView;
    private TextField filtroTextField;
    private Connection conexion;

    public void mostrarVentana() {
        this.show();
    }

    public ListaMetodosPago(Connection conexion) {
        this.conexion = conexion;
        initUI();
        obtenerMetodosPagoDeBaseDeDatos();
    }

    private void initUI() {
        setTitle("Lista de Métodos de Pago");
        setWidth(900);
        setHeight(700);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Label filtroLabel = new Label("Filtrar por nombre:");
        filtroLabel.setStyle("-fx-font-size: 20px;"); // Aumentando el tamaño de la fuente
        filtroTextField = new TextField();


        configureTable();

        vbox.getChildren().addAll(filtroLabel, filtroTextField, tableView);

        Scene scene = new Scene(vbox, 900, 700); // Estableciendo el tamaño de la escena
        setScene(scene);

        configureFiltering();
    }

    private void configureTable() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        TableColumn<MetodoPago, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<MetodoPago, String> nombreColumn = new TableColumn<>("Nombre");
        TableColumn<MetodoPago, String> tipoColumn = new TableColumn<>("Tipo");
        TableColumn<MetodoPago, Double> porcentajeColumn = new TableColumn<>("Porcentaje");
        TableColumn<MetodoPago, Void> editarColumn = new TableColumn<>("Editar");
        TableColumn<MetodoPago, Void> eliminarColumn = new TableColumn<>("Eliminar");

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tipoColumn.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        porcentajeColumn.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());

        nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tipoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        porcentajeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        editarColumn.setCellFactory(param -> new TableCell<MetodoPago, Void>() {
            private final Button btnEditar = new Button("Editar");

            {
                btnEditar.setOnAction(event -> {
                    MetodoPago metodoPago = getTableView().getItems().get(getIndex());

                    if (btnEditar.getText().equals("Editar")) {
                        // Cambiar a modo de edición
                        btnEditar.setText("Guardar");
                        tableView.setEditable(true); // Habilitar la edición
                        tableView.edit(getIndex(), nombreColumn); // Puedes editar la columna del nombre
                    } else {
                        // Mostrar alerta de confirmación antes de guardar
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmar cambios");
                        alert.setHeaderText("¿Está seguro de guardar los cambios?");
                        alert.setContentText("Esta acción no se puede deshacer.");

                        ButtonType buttonTypeSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
                        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

                        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == buttonTypeSi) {
                            // Guardar cambios
                            btnEditar.setText("Editar");
                            tableView.setEditable(false); // Deshabilitar la edición
                            guardarCambios(metodoPago);
                        } else {
                            // No hacer nada, mantenerse en modo de edición
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                }
            }
        });

        eliminarColumn.setCellFactory(param -> new TableCell<MetodoPago, Void>() {
            private final Button btnEliminar = new Button("X");

            {
                btnEliminar.setOnAction(event -> {
                    MetodoPago metodoPago = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar eliminación");
                    alert.setHeaderText("¿Está seguro de eliminar el método de pago?");
                    alert.setContentText("Esta acción no se puede deshacer.");

                    ButtonType buttonTypeSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
                    ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonTypeSi) {
                        try {
                            String consultaEliminar = "DELETE FROM metodosdepago WHERE id = ?";
                            try (PreparedStatement statementEliminar = conexion.prepareStatement(consultaEliminar)) {
                                statementEliminar.setInt(1, metodoPago.getId());
                                int filasAfectadas = statementEliminar.executeUpdate();

                                if (filasAfectadas > 0) {
                                    metodosPagoList.remove(metodoPago);
                                    System.out.println("Método de pago eliminado con éxito.");
                                } else {
                                    System.out.println("Método de pago no encontrado con el ID proporcionado.");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });

        tableView.getColumns().addAll(idColumn, nombreColumn, tipoColumn, porcentajeColumn, editarColumn, eliminarColumn);
        tableView.setEditable(false);
        tableView.setPrefSize(800, 500);

        // Aumentar tamaño de fuente en la tabla
        tableView.setStyle("-fx-font-size: 16px;");
    }

    private void configureFiltering() {
        filtroTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });
    }

    private void actualizarFiltro() {
        String filtroNombre = filtroTextField.getText().toLowerCase().trim();

        tableView.setItems(
                metodosPagoList.filtered(metodoPago ->
                        metodoPago.getNombre().toLowerCase().contains(filtroNombre)
                )
        );
        tableView.refresh();
    }

    private void guardarCambios(MetodoPago metodoPago) {
        try {
            String consultaActualizar = "UPDATE metodosdepago SET nombre = ?, tipo = ?, porcentaje = ? WHERE id = ?";
            try (PreparedStatement statementActualizar = conexion.prepareStatement(consultaActualizar)) {
                statementActualizar.setString(1, metodoPago.getNombre());
                statementActualizar.setString(2, metodoPago.getTipo());
                statementActualizar.setDouble(3, metodoPago.getPorcentaje());
                statementActualizar.setInt(4, metodoPago.getId());

                int filasAfectadas = statementActualizar.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Método de pago actualizado con éxito.");
                    actualizarMetodoPagoEnLista(metodoPago);
                } else {
                    System.out.println("Error al actualizar el método de pago: no se encontró el ID.");
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar el método de pago");
            alert.setContentText("Ocurrió un error al intentar actualizar el método de pago.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void actualizarMetodoPagoEnLista(MetodoPago metodoPagoActualizado) {
        for (MetodoPago mp : metodosPagoList) {
            if (mp.getId() == metodoPagoActualizado.getId()) {
                mp.setNombre(metodoPagoActualizado.getNombre());
                mp.setTipo(metodoPagoActualizado.getTipo());
                mp.setPorcentaje(metodoPagoActualizado.getPorcentaje());
                break;
            }
        }
    }

    private void obtenerMetodosPagoDeBaseDeDatos() {
        try {
            String consulta = "SELECT * FROM metodosdepago";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();
                metodosPagoList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    MetodoPago metodoPago = new MetodoPago(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("tipo"),
                            resultSet.getDouble("porcentaje")
                    );

                    metodosPagoList.add(metodoPago);
                }

                tableView.setItems(metodosPagoList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
