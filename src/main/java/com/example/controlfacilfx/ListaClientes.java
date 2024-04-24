package com.example.controlfacilfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ListaClientes extends Stage {

    private ObservableList<Cliente> clientesList;
    private TableView<Cliente> tableView;

    private TextField filtroTextFieldNombre;
    private TextField filtroTextFieldApellido;

    private Connection conexion;

    public void mostrarVentana() {
        this.show();
    }

    public ListaClientes(Connection conexion) {
        this.conexion = conexion;
        initUI();
        obtenerClientesDeBaseDeDatos();
    }

    private void initUI() {
        setTitle("Lista de Clientes");
        setWidth(900);
        setHeight(700);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Label filtroLabelNombre = new Label("Filtrar por nombre:");
        filtroLabelNombre.setStyle("-fx-font-size: 16px;");
        filtroTextFieldNombre = new TextField();
        Label filtroLabelApellido = new Label("Filtrar por apellido:");
        filtroLabelApellido.setStyle("-fx-font-size: 16px;");
        filtroTextFieldApellido = new TextField();

        configureTable();

        vbox.getChildren().addAll(filtroLabelNombre, filtroTextFieldNombre, filtroLabelApellido, filtroTextFieldApellido, tableView);

        Scene scene = new Scene(vbox);
        setScene(scene);

        configureFiltering();
    }

    private void configureTable() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        TableColumn<Cliente, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Cliente, String> nombreColumn = new TableColumn<>("Nombre");
        TableColumn<Cliente, String> apellidoColumn = new TableColumn<>("Apellido");
        TableColumn<Cliente, Integer> documentoColumn = new TableColumn<>("Documento");
        TableColumn<Cliente, String> direccionColumn = new TableColumn<>("Dirección");
        TableColumn<Cliente, Integer> telefonoColumn = new TableColumn<>("Teléfono");
        TableColumn<Cliente, Void> editarColumn = new TableColumn<>("Editar");
        TableColumn<Cliente, Void> eliminarColumn = new TableColumn<>("Eliminar");

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        apellidoColumn.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        documentoColumn.setCellValueFactory(cellData -> cellData.getValue().documentoProperty().asObject());
        direccionColumn.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        telefonoColumn.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty().asObject());

        nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        apellidoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        documentoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        direccionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        telefonoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        editarColumn.setCellFactory(param -> new TableCell<Cliente, Void>() {
            private final Button btnEditar = new Button("Editar");

            {
                btnEditar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());

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
                            guardarCambios(cliente);
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

        eliminarColumn.setCellFactory(param -> new TableCell<Cliente, Void>() {
            private final Button btnEliminar = new Button("X");

            {
                btnEliminar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar eliminación");
                    alert.setHeaderText("¿Está seguro de eliminar el cliente?");
                    alert.setContentText("Esta acción no se puede deshacer.");

                    ButtonType buttonTypeSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
                    ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonTypeSi) {
                        try {
                            String consultaEliminar = "DELETE FROM clientes WHERE id = ?";
                            try (PreparedStatement statementEliminar = conexion.prepareStatement(consultaEliminar)) {
                                statementEliminar.setInt(1, cliente.getId());
                                int filasAfectadas = statementEliminar.executeUpdate();

                                if (filasAfectadas > 0) {
                                    clientesList.remove(cliente);
                                    System.out.println("Cliente eliminado con éxito.");
                                } else {
                                    System.out.println("Cliente no encontrado con el ID proporcionado.");
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

        tableView.getColumns().addAll(idColumn, nombreColumn, apellidoColumn, documentoColumn, direccionColumn, telefonoColumn, editarColumn, eliminarColumn);
        tableView.setEditable(false);
        tableView.setPrefSize(800, 500);

        // Aumentar tamaño de fuente en la tabla
        tableView.setStyle("-fx-font-size: 16px;");
    }

    private void configureFiltering() {
        filtroTextFieldNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });

        filtroTextFieldApellido.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });
    }

    private void actualizarFiltro() {
        String filtroNombre = filtroTextFieldNombre.getText().toLowerCase().trim();
        String filtroApellido = filtroTextFieldApellido.getText().toLowerCase().trim();

        tableView.setItems(
                clientesList.filtered(cliente ->
                        cliente.getNombre().toLowerCase().contains(filtroNombre) &&
                                cliente.getApellido().toLowerCase().contains(filtroApellido)
                )
        );
        tableView.refresh();
    }

    private void obtenerClientesDeBaseDeDatos() {
        try {
            String consulta = "SELECT * FROM clientes";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();
                clientesList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    Cliente cliente = new Cliente(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellido"),
                            resultSet.getInt("documento"),
                            resultSet.getString("direccion"),
                            resultSet.getInt("telefono")
                    );

                    clientesList.add(cliente);
                }

                tableView.setItems(clientesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void guardarCambios(Cliente cliente) {
        try {
            String consultaActualizar = "UPDATE clientes SET nombre = ?, apellido = ?, documento = ?, direccion = ?, telefono = ? WHERE id = ?";
            try (PreparedStatement statementActualizar = conexion.prepareStatement(consultaActualizar)) {
                statementActualizar.setString(1, cliente.getNombre());
                statementActualizar.setString(2, cliente.getApellido());
                statementActualizar.setInt(3, cliente.getDocumento());
                statementActualizar.setString(4, cliente.getDireccion());
                statementActualizar.setInt(5, cliente.getTelefono());
                statementActualizar.setInt(6, cliente.getId());

                int filasAfectadas = statementActualizar.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Cliente actualizado con éxito.");
                } else {
                    System.out.println("Error al actualizar el cliente: no se encontró el ID.");
                }
            }
        } catch (SQLException e) {
            // Manejar la excepción de forma adecuada
            e.printStackTrace();
        }
    }
}
