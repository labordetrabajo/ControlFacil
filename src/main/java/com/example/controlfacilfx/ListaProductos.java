package com.example.controlfacilfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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
        setWidth(1050);
        setHeight(700);

        // Crear un GridPane para organizar los elementos
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));

        // Crear etiquetas y campos de texto para filtrar por nombre, precio y cantidad
        Label filtroLabel = new Label("Nombre:");
        filtroLabel.setStyle("-fx-font-size: 16px;");
        filtroTextField = new TextField();
        filtroTextField.setStyle("-fx-font-size: 16px;");
        filtroTextField.setMaxWidth(180);

        Label filtroLabelPrecio = new Label("Precio:");
        filtroLabelPrecio.setStyle("-fx-font-size: 16px;");
        filtroTextFieldPrecio = new TextField();
        filtroTextFieldPrecio.setMaxWidth(180);
        filtroTextFieldPrecio.setStyle("-fx-font-size: 16px;");

        Label filtroLabelCantidad = new Label("Cantidad:");
        filtroLabelCantidad.setStyle("-fx-font-size: 16px;");
        filtroTextFieldCantidad = new TextField();
        filtroTextFieldCantidad.setMaxWidth(180);
        filtroTextFieldCantidad.setStyle("-fx-font-size: 16px;");

        Label filtroLabelBarra = new Label("Cód.Barra:");
        filtroLabelBarra.setStyle("-fx-font-size: 16px;");
        filtroTextFieldCodigodeBarra = new TextField();
        filtroTextFieldCodigodeBarra.setMaxWidth(180);
        filtroTextFieldCodigodeBarra.setStyle("-fx-font-size: 16px;");

        // Configurar la tabla
        configureTable();

        // Agregar elementos al GridPane
        // Agregar elementos al GridPane
        gridPane.add(filtroLabel, 0, 0);
        gridPane.add(filtroTextField, 1, 0);

        Label labelPrecioProveedorSuma = new Label("Proveedor");
        labelPrecioProveedorSuma.setStyle("-fx-font-size: 16px;");
        gridPane.add(labelPrecioProveedorSuma, 2, 0);
        // Crear un Insets personalizado solo para el Label de Precio Proveedor Suma
        Insets margenPersonalizado = new Insets(0, 0, 0, -400); // Solo ajusta el margen izquierdo
        // Aplicar el margen personalizado solo al Label de Precio Proveedor Suma
        GridPane.setMargin(labelPrecioProveedorSuma, margenPersonalizado);
        Button miBoton = new Button("");
        miBoton.setStyle("-fx-font-size: 16px;");
         // Crear ImageView para la imagen del botón
        ImageView imagenBoton = new ImageView(new Image(getClass().getResourceAsStream("/masmenos.png")));
        imagenBoton.setFitWidth(30); // ajusta el ancho de la imagen según sea necesario
        imagenBoton.setFitHeight(30); // ajusta el alto de la imagen según sea necesario
        miBoton.setGraphic(imagenBoton); // agrega la imagen al botón
        // Agregar el botón al GridPane
        gridPane.add(miBoton, 3, 0);
        // Aplicar un margen personalizado al botón para reducir el espacio
        Insets margenPersonalizado2 = new Insets(0, 0, 0, -325); // Ajusta el margen izquierdo del botón
        GridPane.setMargin(miBoton, margenPersonalizado2);
        miBoton.setOnAction(event -> {
            // Crear un cuadro de diálogo de entrada para que el usuario escriba el porcentaje
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar/Restar Porcentaje");
            dialog.setHeaderText("Ingrese el porcentaje a sumar/restar al precio proveedor:");
            dialog.setContentText("Porcentaje:");

            // Mostrar el cuadro de diálogo y esperar a que el usuario ingrese el porcentaje
            Optional<String> result = dialog.showAndWait();

            // Procesar la entrada del usuario si está presente
            result.ifPresent(porcentaje -> {
                try {
                    // Convertir la entrada del usuario a un valor numérico
                    double porcentajeDouble = Double.parseDouble(porcentaje);

                    // Iterar sobre la lista de productos y ajustar el precio proveedor
                    for (Producto producto : productosList) {
                        // Obtener el precio proveedor actual del producto
                        double precioProveedorActual = producto.getPrecioProveedor();

                        // Calcular el nuevo precio proveedor sumando o restando el porcentaje
                        double nuevoPrecioProveedor = precioProveedorActual * (1 + porcentajeDouble / 100);

                        // Actualizar el precio proveedor del producto
                        producto.setPrecioProveedor(nuevoPrecioProveedor);
                    }

                    // Actualizar la tabla para reflejar los cambios
                    tableView.refresh();
                } catch (NumberFormatException e) {
                    // Manejar el caso en el que el usuario ingrese un valor no numérico
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Entrada no válida");
                    alert.setContentText("Por favor, ingrese un valor numérico válido para el porcentaje.");
                    alert.showAndWait();
                }
                // Llamar al método para guardar los cambios en la base de datos
                guardarCambiosEnBaseDeDatos();
            });
        });

        Label labelPrecioSuma = new Label("Precio Vent.");
        labelPrecioSuma.setStyle("-fx-font-size: 16px;");
        gridPane.add(labelPrecioSuma, 2, 1);
        // Crear un Insets personalizado solo para el Label de Precio Proveedor Suma
        Insets margenPersonalizadoprecio = new Insets(0, 0, 0, -400); // Solo ajusta el margen izquierdo
        // Aplicar el margen personalizado solo al Label de Precio Proveedor Suma
        GridPane.setMargin(labelPrecioSuma, margenPersonalizadoprecio);
        Button miBotonprecio = new Button("");
        miBotonprecio.setStyle("-fx-font-size: 16px;");
        // Crear ImageView para la imagen del botón
        ImageView imagenBoton2 = new ImageView(new Image(getClass().getResourceAsStream("/masmenos.png")));
        imagenBoton2.setFitWidth(30); // ajusta el ancho de la imagen según sea necesario
        imagenBoton2.setFitHeight(30); // ajusta el alto de la imagen según sea necesario
        miBotonprecio.setGraphic(imagenBoton2); // agrega la imagen al botón
        // Agregar el botón al GridPane
        gridPane.add(miBotonprecio, 3, 1);
        // Aplicar un margen personalizado al botón para reducir el espacio
        Insets margenPersonalizado3 = new Insets(0, 0, 0, -325); // Ajusta el margen izquierdo del botón
        GridPane.setMargin( miBotonprecio, margenPersonalizado3);
        miBotonprecio.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar/Restar Porcentaje");
            dialog.setHeaderText("Ingrese el porcentaje a sumar/restar al precio lista:");
            dialog.setContentText("Porcentaje:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(porcentaje -> {
                try {
                    double porcentajeDouble = Double.parseDouble(porcentaje);

                    for (Producto producto : productosList) {
                        double precioActual = producto.getPrecio();
                        double nuevoPrecio = precioActual * (1 + porcentajeDouble / 100);
                        producto.setPrecio(nuevoPrecio); // Actualiza el precio, no el precio proveedor
                    }

                    tableView.refresh();
                    guardarCambiosEnBaseDeDatosPrecio(); // Guarda los cambios en la base de datos
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Entrada no válida");
                    alert.setContentText("Por favor, ingrese un valor numérico válido para el porcentaje.");
                    alert.showAndWait();
                }
            });
        });

        Label labelPreciofamilia = new Label("Filtro Prov.");
        labelPreciofamilia.setStyle("-fx-font-size: 16px;");
        gridPane.add(labelPreciofamilia, 2, 2);
        // Crear un Insets personalizado solo para el Label de Precio Proveedor Suma
        Insets margenPersonalizadopreciofamilia = new Insets(0, 0, 0, -400); // Solo ajusta el margen izquierdo
        // Aplicar el margen personalizado solo al Label de Precio Proveedor Suma
        GridPane.setMargin(labelPreciofamilia,margenPersonalizadopreciofamilia);
        Button miBotonpreciofamilia = new Button("");
        miBotonpreciofamilia.setStyle("-fx-font-size: 16px;");
        // Crear ImageView para la imagen del botón
        ImageView imagenBotonfamilia = new ImageView(new Image(getClass().getResourceAsStream("/masmenos.png")));
        imagenBotonfamilia.setFitWidth(30); // ajusta el ancho de la imagen según sea necesario
        imagenBotonfamilia.setFitHeight(30); // ajusta el alto de la imagen según sea necesario
        miBotonpreciofamilia.setGraphic(imagenBotonfamilia); // agrega la imagen al botón
        // Agregar el botón al GridPane
        gridPane.add(miBotonpreciofamilia, 3, 2);
        // Aplicar un margen personalizado al botón para reducir el espacio
        Insets margenPersonalizado4 = new Insets(0, 0, 0, -325); // Ajusta el margen izquierdo del botón
        GridPane.setMargin( miBotonpreciofamilia, margenPersonalizado4);
        miBotonpreciofamilia.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar/Restar Porcentaje");
            dialog.setHeaderText("Ingrese el porcentaje a sumar/restar al precio proveedor de los productos filtrados en la tabla:");
            dialog.setContentText("Porcentaje:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(porcentaje -> {
                try {
                    double porcentajeDouble = Double.parseDouble(porcentaje);

                    for (Producto producto : tableView.getItems()) { // Itera sobre los productos filtrados
                        double precioProveedorActual = producto.getPrecioProveedor();
                        double nuevoPrecioProveedor = precioProveedorActual * (1 + porcentajeDouble / 100);
                        producto.setPrecioProveedor(nuevoPrecioProveedor);
                    }

                    tableView.refresh();
                    guardarCambiosEnBaseDeDatosPrecioFamilia(); // Guarda los cambios en la base de datos
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Entrada no válida");
                    alert.setContentText("Por favor, ingrese un valor numérico válido para el porcentaje.");
                    alert.showAndWait();
                }
            });
        });

        Label labelPreciofamiliaPrec = new Label("Filtro Prec.");
        labelPreciofamiliaPrec.setStyle("-fx-font-size: 16px;");
        gridPane.add(labelPreciofamiliaPrec, 2, 3);
        // Crear un Insets personalizado solo para el Label de Precio Proveedor Suma
        Insets margenPersonalizadopreciofamiliaprec = new Insets(0, 0, 0, -400); // Solo ajusta el margen izquierdo
        // Aplicar el margen personalizado solo al Label de Precio Proveedor Suma
        GridPane.setMargin(labelPreciofamiliaPrec,margenPersonalizadopreciofamiliaprec);
        Button miBotonpreciofamiliaprec = new Button("");
        miBotonpreciofamiliaprec.setStyle("-fx-font-size: 16px;");
        // Crear ImageView para la imagen del botón
        ImageView imagenBotonfamiliaprec = new ImageView(new Image(getClass().getResourceAsStream("/masmenos.png")));
        imagenBotonfamiliaprec.setFitWidth(30); // ajusta el ancho de la imagen según sea necesario
        imagenBotonfamiliaprec.setFitHeight(30); // ajusta el alto de la imagen según sea necesario
        miBotonpreciofamiliaprec.setGraphic(imagenBotonfamiliaprec); // agrega la imagen al botón
        // Agregar el botón al GridPane
        gridPane.add(miBotonpreciofamiliaprec, 3, 3);
        // Aplicar un margen personalizado al botón para reducir el espacio
        Insets margenPersonalizado5 = new Insets(0, 0, 0, -325); // Ajusta el margen izquierdo del botón
        GridPane.setMargin( miBotonpreciofamiliaprec, margenPersonalizado5);
        miBotonpreciofamiliaprec.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar/Restar Precio");
            dialog.setHeaderText("Ingrese el porcentaje a sumar/restar al precio de los productos filtrados en la tabla:");
            dialog.setContentText("Porcentaje:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(porcentaje -> {
                try {
                    double porcentajeDouble = Double.parseDouble(porcentaje);

                    // Convertir el porcentaje a una fracción decimal
                    double porcentajeDecimal = porcentajeDouble / 100;

                    for (Producto producto : tableView.getItems()) { // Itera sobre los productos filtrados
                        double precioActual = producto.getPrecio();
                        double nuevoPrecio = precioActual * (1 + porcentajeDecimal);
                        producto.setPrecio(nuevoPrecio);
                    }

                    tableView.refresh();
                    guardarCambiosEnBaseDeDatosPrecioFamiliaPrec(); // Guarda los cambios en la base de datos
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Entrada no válida");
                    alert.setContentText("Por favor, ingrese un valor numérico válido para el porcentaje.");
                    alert.showAndWait();
                }
            });
        });


        gridPane.add(filtroLabelPrecio, 0, 1);
        gridPane.add(filtroTextFieldPrecio, 1, 1);

        gridPane.add(filtroLabelCantidad, 0, 2);
        gridPane.add(filtroTextFieldCantidad, 1, 2);

        gridPane.add(filtroLabelBarra, 0, 3);
        gridPane.add(filtroTextFieldCodigodeBarra, 1, 3);

        gridPane.add(tableView, 1, 4);
        Insets margenTabla = new Insets(0, 0, 0, -85); // Ajusta el margen izquierdo de la tabla
        GridPane.setMargin(tableView, margenTabla);
        // Desactivar el crecimiento automático de las columnas
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Establecer un ancho explícito para la tabla
        tableView.setPrefWidth(1000); // ajusta el ancho según sea necesario

        // Desactivar el crecimiento automático de las columnas
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Agregar la tabla al GridPane





        // Crear la escena y establecerla en la ventana
        Scene scene = new Scene(gridPane);
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
        TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio Venta");
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

    // Método para guardar los cambios en la base de datos del precio proveedor
    private void guardarCambiosEnBaseDeDatos() {
        try {
            String consultaActualizarPreciosProveedor = "UPDATE productos SET precioproveedor = ? WHERE id = ?";
            try (PreparedStatement statementActualizarPreciosProveedor = conexion.prepareStatement(consultaActualizarPreciosProveedor)) {
                // Iterar sobre la lista de productos y actualizar los precios proveedores en la base de datos
                for (Producto producto : productosList) {
                    statementActualizarPreciosProveedor.setDouble(1, producto.getPrecioProveedor());
                    statementActualizarPreciosProveedor.setInt(2, producto.getId());
                    statementActualizarPreciosProveedor.executeUpdate();
                }
                System.out.println("Precios proveedores actualizados en la base de datos.");
            }
        } catch (SQLException e) {
            // Manejar la excepción de forma adecuada
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar los precios proveedores");
            alert.setHeaderText("Ocurrió un error al intentar actualizar los precios proveedores en la base de datos.");
            alert.showAndWait();
        }
    }

    private void guardarCambiosEnBaseDeDatosPrecio() {
        try {
            String consultaActualizarPrecios = "UPDATE productos SET precio = ? WHERE id = ?";
            try (PreparedStatement statementActualizarPrecios = conexion.prepareStatement(consultaActualizarPrecios)) {
                for (Producto producto : productosList) {
                    statementActualizarPrecios.setDouble(1, producto.getPrecio());
                    statementActualizarPrecios.setInt(2, producto.getId());
                    statementActualizarPrecios.executeUpdate();
                }
                System.out.println("Precios actualizados en la base de datos.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar los precios ");
            alert.setHeaderText("Ocurrió un error al intentar actualizar los precios en la base de datos.");
            alert.showAndWait();
        }
    }

    private void guardarCambiosEnBaseDeDatosPrecioFamilia() {
        try {
            String consultaActualizarPreciosProveedor = "UPDATE productos SET precioproveedor = ? WHERE nombre LIKE ?"; // Solo actualiza los precios proveedores de los productos filtrados
            try (PreparedStatement statementActualizarPreciosProveedor = conexion.prepareStatement(consultaActualizarPreciosProveedor)) {
                for (Producto producto : tableView.getItems()) {
                    statementActualizarPreciosProveedor.setDouble(1, producto.getPrecioProveedor());
                    statementActualizarPreciosProveedor.setString(2, "%" + filtroTextField.getText() + "%"); // Utiliza el filtro de nombre para actualizar solo los productos filtrados
                    statementActualizarPreciosProveedor.executeUpdate();
                }
                System.out.println("Precios proveedores actualizados en la base de datos para los productos filtrados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar los precios proveedores");
            alert.setHeaderText("Ocurrió un error al intentar actualizar los precios proveedores en la base de datos.");
            alert.showAndWait();
        }
    }

    private void guardarCambiosEnBaseDeDatosPrecioFamiliaPrec() {
        try {
            String consultaActualizarPrecios = "UPDATE productos SET precio = ? WHERE nombre LIKE ?"; // Solo actualiza los precios de los productos filtrados
            try (PreparedStatement statementActualizarPrecios = conexion.prepareStatement(consultaActualizarPrecios)) {
                for (Producto producto : tableView.getItems()) {
                    statementActualizarPrecios.setDouble(1, producto.getPrecio());
                    statementActualizarPrecios.setString(2, "%" + filtroTextField.getText() + "%"); // Utiliza el filtro de nombre para actualizar solo los productos filtrados
                    statementActualizarPrecios.executeUpdate();
                }
                System.out.println("Precios actualizados en la base de datos para los productos filtrados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al actualizar los precios");
            alert.setHeaderText("Ocurrió un error al intentar actualizar los precios en la base de datos.");
            alert.showAndWait();
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
