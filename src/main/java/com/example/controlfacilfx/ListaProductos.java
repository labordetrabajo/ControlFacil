package com.example.controlfacilfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.text.NumberFormat;
import java.text.ParseException;

public class ListaProductos extends Stage {

    // Lista observable de productos
    private ObservableList<Producto> productosList;
    private TableView<Producto> tableView;

    // Campos de texto para filtrar por nombre, precio y cantidad
    private TextField filtroTextField;
    private TextField filtroTextFieldPrecio;
    private TextField filtroTextFieldCantidad;

    private TextField filtroTextFieldCodigodeBarra;

    // Conexión a la base de datos
    private Connection conexion;


    // Método para mostrar la ventana de lista de productos
    public void mostrarVentana() {
        this.show();
    }

    // Constructor de la clase ListaProductos
    public ListaProductos(Connection conexion) {
        this.conexion = conexion;
        initUI();
        obtenerProductosDeBaseDeDatos();
    }

    // Método para inicializar la interfaz de usuario
    private void initUI() {
        setTitle("Lista de Productos");
        setWidth(900);
        setHeight(700);
        // Crear un contenedor VBox para organizar los elementos
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        // Crear etiquetas y campos de texto para filtrar por nombre, precio y cantidad
        Label filtroLabel = new Label("Filtrar por Nombre:");
        filtroLabel.setStyle("-fx-font-size: 16px;");
        filtroTextField = new TextField();
        // Aumentar tamaño de fuente del campo de texto de filtrado
        filtroTextField.setStyle("-fx-font-size: 16px;");
        Label filtroLabelPrecio = new Label("Filtrar por Precio:");
        filtroLabelPrecio.setStyle("-fx-font-size: 16px;");
        filtroTextFieldPrecio = new TextField();
        // Aumentar tamaño de fuente del campo de texto de filtrado
        filtroTextFieldPrecio.setStyle("-fx-font-size: 16px;");
        Label filtroLabelCantidad = new Label("Filtrar por Cantidad:");
        filtroLabelCantidad.setStyle("-fx-font-size: 16px;");

        filtroTextFieldCantidad = new TextField();
        // Aumentar tamaño de fuente del campo de texto de filtrado
        filtroTextFieldCantidad.setStyle("-fx-font-size: 16px;");

        filtroTextFieldCodigodeBarra = new TextField();
        filtroTextFieldCodigodeBarra.setStyle("-fx-font-size: 16px;");
        Label filtroLabelBarra = new Label("Filtrar por Código de Barra:");
        filtroLabelBarra.setStyle("-fx-font-size: 16px;");

        // Configurar la tabla
        configureTable();
        // Agregar todos los elementos al VBox
        vbox.getChildren().addAll(filtroLabel, filtroTextField, filtroLabelPrecio, filtroTextFieldPrecio, filtroLabelCantidad, filtroTextFieldCantidad, filtroLabelBarra, filtroTextFieldCodigodeBarra, tableView);
        // Crear la escena y establecerla en la ventana
        Scene scene = new Scene(vbox);
        setScene(scene);
        // Configurar el filtrado
        configureFiltering();
    }

    // Método para configurar la tabla
    private void configureTable() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        TableColumn<Producto, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
        TableColumn<Producto, Double> cantidadColumn = new TableColumn<>("Cantidad");
        TableColumn<Producto, String> unidadColumn = new TableColumn<>("Unidad");
        TableColumn<Producto, Double> precioProveedorColumn = new TableColumn<>("Precio Proveedor"); // Nueva columna para precioProveedor
        TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
        TableColumn<Producto, String> codigoBarrasColumn = new TableColumn<>("Codigo de Barra");
        TableColumn<Producto, Void> eliminarColumn = new TableColumn<>("Eliminar");
        TableColumn<Producto, Void> editarColumn = new TableColumn<>("Editar");

        // Configurar la propiedad de las columnas
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        cantidadColumn.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        codigoBarrasColumn.setCellValueFactory(cellData -> cellData.getValue().codigoBarrasProperty());
        unidadColumn.setCellValueFactory(cellData -> cellData.getValue().unidadProperty());
        precioProveedorColumn.setCellValueFactory(cellData -> cellData.getValue().precioProveedorProperty().asObject()); // Enlazar con la propiedad precioProveedor

        eliminarColumn.setCellFactory(param -> new TableCell<Producto, Void>() {
            private final Button btnEliminar = new Button("X");

            {
                btnEliminar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar eliminación");
                    alert.setHeaderText("¿Está seguro de eliminar el producto?");
                    alert.setContentText("Esta acción no se puede deshacer.");

                    ButtonType buttonTypeSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
                    ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonTypeSi) {
                        try {
                            String consultaEliminar = "DELETE FROM productos WHERE id = ?";
                            try (PreparedStatement statementEliminar = conexion.prepareStatement(consultaEliminar)) {
                                statementEliminar.setInt(1, producto.getId());
                                int filasAfectadas = statementEliminar.executeUpdate();

                                if (filasAfectadas > 0) {
                                    productosList.remove(producto);
                                    System.out.println("Producto eliminado con éxito.");
                                } else {
                                    System.out.println("Producto no encontrado con el ID proporcionado.");
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

        editarColumn.setCellFactory(param -> new TableCell<Producto, Void>() {
            private final Button btnEditar = new Button("Editar");

            {
                btnEditar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());

                    if (btnEditar.getText().equals("Editar")) {
                        btnEditar.setText("Guardar");
                        tableView.edit(getIndex(), nombreColumn);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmar cambios");
                        alert.setHeaderText("¿Está seguro de guardar los cambios?");
                        alert.setContentText("Esta acción no se puede deshacer.");

                        ButtonType buttonTypeSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
                        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

                        alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == buttonTypeSi) {
                            btnEditar.setText("Editar");
                            guardarCambios(producto);
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

        nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        unidadColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        precioProveedorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        precioColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        codigoBarrasColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.getColumns().addAll(idColumn, nombreColumn, cantidadColumn, unidadColumn, precioProveedorColumn,precioColumn, codigoBarrasColumn, eliminarColumn, editarColumn);
        tableView.setEditable(true);
        tableView.setPrefSize(800, 500);

        // Aumentar tamaño de fuente en la tabla
        tableView.setStyle("-fx-font-size: 16px;");
    }


    // Método para configurar el filtrado
    private void configureFiltering() {
        // Escuchar cambios en los campos de texto de filtrado y actualizar la tabla en consecuencia
        filtroTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });

        filtroTextFieldPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });

        filtroTextFieldCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });

        filtroTextFieldCodigodeBarra.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarFiltro();
        });
    }

    // Método para actualizar el filtrado y mostrar los productos que coinciden
    // con los criterios de búsqueda
    private void actualizarFiltro() {
        // Obtener los valores de los campos de texto de filtrado
        String filtroNombre = filtroTextField.getText().toLowerCase().trim();
        String filtroPrecio = filtroTextFieldPrecio.getText().toLowerCase().trim();
        String filtroCantidad = filtroTextFieldCantidad.getText().toLowerCase().trim();
        String filtroCodigodeBarra=  filtroTextFieldCodigodeBarra.getText().toLowerCase().trim();
        // Aplicar el filtrado a la lista de productos y actualizar la tabla
        tableView.setItems(
                productosList.filtered(producto ->
                        producto.getNombre().toLowerCase().contains(filtroNombre) &&
                                String.valueOf(producto.getPrecio()).toLowerCase().contains(filtroPrecio) &&
                                String.valueOf(producto.getCantidad()).toLowerCase().contains(filtroCantidad) &&
          //El código de barra no tiene que ser transformado a string debido a la propiedad del objeto
                                producto.getCodigoBarras().toLowerCase().contains(filtroCodigodeBarra)
                )
        );
        tableView.refresh();
    }

    // Método para guardar los cambios realizados en la edición de un producto
    private void guardarCambios(Producto producto) {
        try {
            String consultaActualizar = "UPDATE productos SET nombre = ?, cantidad = ?, precio = ?, unidad = ?, precioproveedor = ? , codigodebarra=?  WHERE id = ?";
            try (PreparedStatement statementActualizar = conexion.prepareStatement(consultaActualizar)) {
                statementActualizar.setString(1, producto.getNombre());
                statementActualizar.setDouble(2, producto.getCantidad());
                statementActualizar.setDouble(3, producto.getPrecio());
                statementActualizar.setString(4, producto.getUnidad());
                statementActualizar.setDouble(5, producto.getPrecioProveedor());
                statementActualizar.setString(6, producto.getCodigoBarras());
                statementActualizar.setInt(7, producto.getId());

                int filasAfectadas = statementActualizar.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Producto actualizado con éxito.");
                    actualizarTabla();
                } else {
                    System.out.println("Error al actualizar el producto.");
                }
            }
        } catch (SQLException e) {
            // Manejar la excepción de forma adecuada, por ejemplo:
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar el producto");
            alert.setContentText("Ocurrió un error al intentar actualizar el producto.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    private void actualizarTabla() {
        try {
            String consultaObtenerProductos = "SELECT * FROM productos";
            try (PreparedStatement statementObtenerProductos = conexion.prepareStatement(consultaObtenerProductos)) {
                ResultSet resultSet = statementObtenerProductos.executeQuery();
                productosList.clear();

                while (resultSet.next()) {
                    Producto producto = new Producto(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getDouble("cantidad"),
                            resultSet.getDouble("precio"),
                            resultSet.getDouble("precioproveedor"),
                            resultSet.getString("unidad"),
                            resultSet.getString("codigodebarra")

                    );

                    productosList.add(producto);
                }

                tableView.setItems(productosList);
                tableView.refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción de forma adecuada
        }
    }

    // Método para obtener los productos de la base de datos y cargarlos en la lista observable
    private void obtenerProductosDeBaseDeDatos() {
        try {
            String consulta = "SELECT * FROM productos";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();
                productosList = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    Producto producto = new Producto(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getDouble("cantidad"),
                            resultSet.getDouble("precio"),
                            resultSet.getDouble("precioproveedor"),
                            resultSet.getString("unidad"),
                            resultSet.getString("codigodebarra")


                    );

                    productosList.add(producto);
                }

                tableView.setItems(productosList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
