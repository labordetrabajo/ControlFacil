package com.example.controlfacilfx;

/************************ IMPORTACIONES ************************/

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Venta extends Application {
    private ArrayList<Producto> listaProductos;
    private ArrayList<Producto> productosAgregados;
    private Connection conexion;
    private TableView<Producto> tablaProductos;
    private TableView<Producto> tablaCarrito;
    private Label totalLabel;
    private ChoiceBox<String> metodoPagoChoiceBox;
    private ArrayList<Producto> productosEliminados;

    // Declarar etiquetas para mostrar los detalles del cliente
    private Label idClienteLabel;
    private Label direccionClienteLabel;
    private Label telefonoClienteLabel;
    private Label documentoClienteLabel;
    private Label porcentajeLabel;
    private Label finalLabel;
    private GridPane gridPane; // Declarar gridPane como una variable de instancia

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realizar Venta");
     //*********************************************** CONFIGURACIÓN SCENE **************************************//
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        // Ajustamos el tamaño de la fuente de los elementos del GridPane
        gridPane.setStyle("-fx-font-size: 18;");

        Scene scene = new Scene(gridPane, 900, 700); // Ajuste del tamaño de la escena
        primaryStage.setScene(scene);
        primaryStage.show();
        //**********************************************  BÚSQUEDA PRODUCTO *****************************************//
        Label nombreLabel = new Label("Producto:");
        gridPane.add(nombreLabel, 0, 0);

        TextField nombreTextField = new TextField();
        nombreTextField.setMaxWidth(100); // Establece el ancho máximo del TextField
        nombreTextField.setPrefWidth(100); // Establece el ancho preferido del TextField
        gridPane.add(nombreTextField, 1, 0);

        Button buscarButton = new Button("");
        buscarButton.setOnAction(e -> buscarProducto(nombreTextField.getText()));

        // Crear ImageView para el icono del botón de búsqueda
        ImageView buscarIcon = new ImageView(new Image(getClass().getResourceAsStream("/buscar.png")));
        buscarIcon.setFitWidth(35); // ajusta el ancho del icono según sea necesario
        buscarIcon.setFitHeight(35); // ajusta el alto del icono según sea necesario

        // Agregar el icono al botón
        buscarButton.setGraphic(buscarIcon);

        gridPane.add(buscarButton, 2, 0);

        nombreTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarProducto(nombreTextField.getText());
            }
        });
        //****************************************** BÚSQUEDA CLIENTE ***************************************************//
        // Crear el TextField para buscar clientes
        TextField buscarClienteTextField = new TextField();
        buscarClienteTextField.setPromptText("Buscar Cliente"); // Texto de sugerencia
        buscarClienteTextField.setMaxWidth(200); // Establecer un ancho específico
        buscarClienteTextField.setAlignment(Pos.CENTER_LEFT); // Alinear el texto a la izquierda

     // Texto "Cliente" al lado del TextField
        Label clienteLabel = new Label("Cliente");
        clienteLabel.setLabelFor(buscarClienteTextField);

     // Botón de búsqueda
        Button buscarClienteButton = new Button("");
        buscarClienteButton.setOnAction(event -> {
            String clienteIngresado = buscarClienteTextField.getText();
            if (!clienteIngresado.isEmpty()) {
                mostrarDetallesCliente(clienteIngresado);
            }
        });

        // Cargar la misma imagen que tiene el botón buscar
        ImageView buscarClienteIcon = new ImageView(new Image(getClass().getResourceAsStream("/buscar.png")));
        buscarClienteIcon.setFitWidth(35); // ajustar el ancho del icono según sea necesario
        buscarClienteIcon.setFitHeight(35); // ajustar el alto del icono según sea necesario

     // Asignar el icono al botón buscarClienteButton
        buscarClienteButton.setGraphic(buscarClienteIcon);

     // Realizar la búsqueda cuando se presiona "Enter" en el TextField
        buscarClienteTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String clienteIngresado = buscarClienteTextField.getText();
                if (!clienteIngresado.isEmpty()) {
                    mostrarDetallesCliente(clienteIngresado);
                }
            }
        });

     // Dentro del método start()
        HBox buscarBox = new HBox(10);
        buscarBox.getChildren().addAll(clienteLabel, buscarClienteTextField, buscarClienteButton);
        buscarBox.setAlignment(Pos.CENTER_LEFT);

      // Agregar el contenedor HBox al GridPane
        gridPane.add(buscarBox, 3, 0);

     // Inicializar etiquetas
        idClienteLabel = new Label();
        direccionClienteLabel = new Label();
        telefonoClienteLabel = new Label();
        documentoClienteLabel = new Label();

     // Agregar etiquetas al GridPane
        gridPane.add(idClienteLabel, 3, 1);
        gridPane.add(direccionClienteLabel, 3, 2);
        gridPane.add(telefonoClienteLabel, 3, 3);
        gridPane.add(documentoClienteLabel, 3, 4);


        //****************************************** BUSQUEDA CLIENTE ***************************************************//
        tablaProductos = new TableView<>();
        TableColumn<Producto, Integer> idColumna = new TableColumn<>("ID");
        idColumna.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Producto, String> nombreColumna = new TableColumn<>("Nombre");
        nombreColumna.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreColumna.setMinWidth(150); // Ajustamos el ancho según necesidades
        TableColumn<Producto, Double> cantidadColumna = new TableColumn<>("Cantidad");
        cantidadColumna.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        // Formatear la celda de cantidad para la tabla de productos
        cantidadColumna.setCellFactory(column -> {
            return new TableCell<Producto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Formatear el número para eliminar decimales si es un número entero
                        if (item == Math.floor(item)) {
                            setText(String.valueOf(item.intValue()));
                        } else {
                            setText(String.format("%.2f", item)); // Ajustar la precisión según tus necesidades
                        }
                    }
                }
            };
        });
        TableColumn<Producto, String> unidadColumna = new TableColumn<>("Unidad");
        unidadColumna.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        TableColumn<Producto, Double> precioColumna = new TableColumn<>("Precio");
        precioColumna.setCellValueFactory(new PropertyValueFactory<>("precio"));
        precioColumna.setCellFactory(column -> {
            return new TableCell<Producto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Verificar si el precio es un número entero
                        if (item == Math.floor(item)) {
                            // Si es entero, mostrarlo como un entero
                            setText(String.valueOf(item.intValue()));
                        } else {
                            // Si no es entero, mostrarlo con dos decimales
                            setText(String.format("%.2f", item));
                        }
                    }
                }
            };
        });
        tablaProductos.getColumns().addAll(idColumna, nombreColumna, cantidadColumna, unidadColumna, precioColumna);

        tablaProductos.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                agregarAlCarrito();
            } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
                Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
                if (productoSeleccionado != null) {
                    tablaProductos.setRowFactory(tv -> new TableRow<Producto>() {
                        @Override
                        protected void updateItem(Producto item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setStyle("");
                            } else {
                                if (item.equals(productoSeleccionado)) {
                                    setStyle("-fx-background-color:  #faf9f8;");
                                } else {
                                    setStyle("");
                                }
                            }
                        }
                    });
                }
            }
        });

        gridPane.add(tablaProductos, 0, 1, 3, 1);

        //******************************************* CARRITO DE COMPRAS ********************************************//

        // Agregar el encabezado para el carrito de compras
        Label carritoLabel = new Label("Carrito de Compras");
        carritoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        gridPane.add(carritoLabel, 0, 2, 3, 1);

        // Crear la tabla para el carrito de compras
        tablaCarrito = new TableView<>();
        TableColumn<Producto, String> nombreCarritoColumna = new TableColumn<>("Nombre");
        nombreCarritoColumna.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreCarritoColumna.setMinWidth(150); // Ajustamos el ancho según necesidades
        TableColumn<Producto, Double> cantidadCarritoColumna = new TableColumn<>("Cantidad");
        cantidadCarritoColumna.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        // Formatear la celda de cantidad para la tabla del carrito de compras
        cantidadCarritoColumna.setCellFactory(column -> {
            return new TableCell<Producto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Formatear el número para eliminar decimales si es un número entero
                        if (item == Math.floor(item)) {
                            setText(String.valueOf(item.intValue()));
                        } else {
                            setText(String.format("%.2f", item)); // Ajustar la precisión según tus necesidades
                        }
                    }
                }
            };
        });
        TableColumn<Producto, Double> precioCarritoColumna = new TableColumn<>("Precio");
        precioCarritoColumna.setCellValueFactory(new PropertyValueFactory<>("precio"));
        TableColumn<Producto, String> unidadCarritoColumna = new TableColumn<>("Unidad");
        unidadCarritoColumna.setCellValueFactory(new PropertyValueFactory<>("unidad"));

        // Aquí es donde agregarías el código para formatear los precios en la columna de precio del carrito de compras
        precioCarritoColumna.setCellFactory(column -> {
            return new TableCell<Producto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Verificar si el precio es un número entero
                        if (item == Math.floor(item)) {
                            // Si es entero, mostrarlo como un entero
                            setText(String.valueOf(item.intValue()));
                        } else {
                            // Si no es entero, mostrarlo con dos decimales
                            setText(String.format("%.2f", item));
                        }
                    }
                }
            };
        });

        TableColumn<Producto, Double> totalCarritoColumna = new TableColumn<>("Total");
        totalCarritoColumna.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            double total = producto.getPrecio() * producto.getCantidad();
            return new SimpleDoubleProperty(total).asObject();
        });

        TableColumn<Producto, Void> eliminarColumna = new TableColumn<>("Eliminar");
        eliminarColumna.setCellFactory(param -> new TableCell<Producto, Void>() {
            private final Button btnEliminar = new Button("X");

            {
                btnEliminar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    productosAgregados.remove(producto);
                    tablaCarrito.setItems(FXCollections.observableArrayList(productosAgregados));
                    actualizarTotal();
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

        tablaCarrito.getColumns().addAll(nombreCarritoColumna, cantidadCarritoColumna, unidadCarritoColumna, precioCarritoColumna, totalCarritoColumna, eliminarColumna);
        gridPane.add(tablaCarrito, 0, 3, 3, 1);

        // Crear el label para mostrar el total
        totalLabel = new Label();
        HBox hboxTotal = new HBox(10);
        hboxTotal.setAlignment(Pos.CENTER_RIGHT);
        hboxTotal.getChildren().addAll(new Label("Total: $"), totalLabel);
        gridPane.add(hboxTotal, 2, 4);

        //***************************************************** FIN CARRITO ****************************************//

        //***************************************************** MÉTODO PAGO ****************************************//

        // Crear el ChoiceBox para el método de pago
        Label metodoPagoLabel = new Label("M.P:");
        metodoPagoChoiceBox = new ChoiceBox<>();
        gridPane.add(metodoPagoLabel, 0, 5);
        gridPane.add(metodoPagoChoiceBox, 1, 5);

        HBox hboxMetodoPago = new HBox(10);
        hboxMetodoPago.getChildren().addAll(metodoPagoLabel, metodoPagoChoiceBox);

        // Agregar el HBox al GridPane
        gridPane.add(hboxMetodoPago, 0, 5, 2, 1);

        // Obtener los métodos de pago de la base de datos y cargarlos en el ChoiceBox
        obtenerMetodosPagoDeBaseDeDatos();
        metodoPagoChoiceBox.setOnAction(event -> {
            String metodoSeleccionado = metodoPagoChoiceBox.getSelectionModel().getSelectedItem();
            if (metodoSeleccionado != null) {
                try {
                    double porcentaje = obtenerPorcentajeMetodoPago(metodoSeleccionado);
                    double total = calcularTotalConPorcentaje(porcentaje);

                    // Eliminar solo las etiquetas relacionadas con el porcentaje y el total
                    gridPane.getChildren().removeIf(node -> node instanceof Label &&
                            (((Label) node).getText().startsWith("Porcentaje a") ||
                                    ((Label) node).getText().startsWith("Final:")));

                    // Mostrar porcentaje a sumar o restar debajo del total
                    porcentajeLabel = new Label();
                    if (porcentaje >= 0) {
                        porcentajeLabel.setText("Porcentaje a sumar: " + (porcentaje * 100) + "%");
                    } else {
                        porcentajeLabel.setText("Porcentaje a restar: -" + (Math.abs(porcentaje) * 100) + "%");
                    }
                    GridPane.setConstraints(porcentajeLabel, 2, 5);
                    gridPane.getChildren().add(porcentajeLabel);

                    // Mostrar precio final con la suma o resta aplicada
                    finalLabel = new Label();
                    if (porcentaje >= 0) {
                        finalLabel.setText("Final: $" + String.format("%.2f", total));
                    } else {
                        finalLabel.setText("Final: $" + String.format("%.2f", total));
                    }
                    GridPane.setConstraints(finalLabel, 2, 6);
                    gridPane.getChildren().add(finalLabel);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        //************************************************* FIN MÉTODO PAGO ******************************************//
          //************************************************* BOTÓN FIN ******************************************//

        Button finButton = new Button("Fin");
        finButton.setOnAction(e -> finalizarVenta());
        gridPane.add(finButton, 3, 8);
        // Ajustar el margen derecho del botón "Fin"
        GridPane.setMargin(finButton, new Insets(0, 0, 0, 20)); // Ajusta el margen según tus necesidades
        GridPane.setHalignment(finButton, HPos.RIGHT);
        // Cargar la imagen fin.png
        ImageView finIcon = new ImageView(new Image(getClass().getResourceAsStream("/fin.png")));
        finIcon.setFitWidth(35); // Ajustar el ancho del icono según sea necesario
        finIcon.setFitHeight(35); // Ajustar el alto del icono según sea necesario

        // Asignar el icono al botón finButton
        finButton.setGraphic(finIcon);

        //************************************************* BOTÓN FIN ******************************************//
    } // esta es mi método principal aca sucede absolutamante todo//

    public static void main(String[] args) {
        launch(args);
    }

    public Venta(Connection conexion) {
        this.listaProductos = new ArrayList<>();
        this.productosAgregados = new ArrayList<>();
        this.productosEliminados = new ArrayList<>();
        this.conexion = conexion;
    }

    /****************************************** CARRITO ************************************************************/

    public void buscarProducto(String parteNombreOBarra) {
        try {
            listaProductos.clear();
            String consulta = "SELECT * FROM productos WHERE nombre LIKE ? OR codigodebarra = ?";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setString(1, "%" + parteNombreOBarra + "%");
                statement.setString(2, parteNombreOBarra); // Se establece el mismo valor para buscar por código de barras
                ResultSet resultSet = statement.executeQuery();

                System.out.println("Productos encontrados para la venta:");
                while (resultSet.next()) {
                    mostrarDetalleProducto(resultSet);
                    Producto producto = new Producto(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getDouble("cantidad"),
                            resultSet.getDouble("precio"),
                            resultSet.getDouble("precioproveedor"),
                            resultSet.getString("unidad"),
                            resultSet.getString("codigodebarra")

                    );
                    listaProductos.add(producto);
                }

                tablaProductos.setItems(FXCollections.observableArrayList(listaProductos));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void mostrarDetalleProducto(ResultSet resultSet) throws SQLException {
        System.out.println("ID: " + resultSet.getInt("id"));
        System.out.println("Nombre: " + resultSet.getString("nombre"));
        System.out.println("Cantidad: " + resultSet.getDouble("cantidad"));
        System.out.println("Precio: " + resultSet.getDouble("precio"));
        System.out.println("-------------------------");
    }

    private void agregarAlCarrito() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            TextInputDialog dialog = new TextInputDialog("1.00"); // Valor predeterminado para la cantidad
            dialog.setTitle("Cantidad");
            dialog.setHeaderText("Ingrese la cantidad:");
            dialog.setContentText("Cantidad:");

            dialog.showAndWait().ifPresent(cantidad -> {
                try {
                    double cantidadDouble = Double.parseDouble(cantidad);
                    double cantidadDisponible = obtenerCantidadDisponible(productoSeleccionado.getId());

                    if (cantidadDouble > cantidadDisponible) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia");
                        alert.setHeaderText("Cantidad excedida");
                        alert.setContentText("No hay suficiente cantidad disponible en el inventario.");
                        alert.showAndWait();
                    } else {
                        productosAgregados.add(new Producto(
                                productoSeleccionado.getId(),
                                productoSeleccionado.getNombre(),
                                cantidadDouble,
                                productoSeleccionado.getPrecio(),
                                productoSeleccionado.getPrecioProveedor(),
                                productoSeleccionado.getUnidad(), // Asegúrate de obtener la unidad del producto seleccionado
                                productoSeleccionado.getCodigoBarras()

                        ));
                        mostrarCarrito();
                        tablaCarrito.setItems(FXCollections.observableArrayList(productosAgregados));
                        actualizarTotal();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cantidad Inválida");
                    alert.setContentText("Por favor, ingrese un número válido para la cantidad.");
                    alert.showAndWait();
                }
            });
        }
    }


    private double obtenerCantidadDisponible(int idProducto) {
        double cantidadDisponible = 0;
        try {
            String consulta = "SELECT cantidad FROM productos WHERE id = ?";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setInt(1, idProducto);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    cantidadDisponible = resultSet.getDouble("cantidad");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidadDisponible;
    }

    private void mostrarCarrito() {
        for (Producto productoAgregado : productosAgregados) {
            System.out.println(productoAgregado.getNombre() + " || Cantidad: " + productoAgregado.getCantidad() + " || Precio: $" + productoAgregado.getPrecio());
        }
    }

    private void actualizarTotal() {
        double total = 0.0;
        for (Producto producto : productosAgregados) {
            total += producto.getPrecio() * producto.getCantidad();
        }

        totalLabel.setText(String.format("%.2f", total));
    }

    /********************************************* METODO DE PAGO ***************************************************/

    private void obtenerMetodosPagoDeBaseDeDatos() {
        try {
            String consulta = "SELECT nombre FROM metodosdepago";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    metodoPagoChoiceBox.getItems().add(resultSet.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private double obtenerPorcentajeMetodoPago(String nombreMetodoPago) throws SQLException {
        String consulta = "SELECT porcentaje FROM metodosdepago WHERE nombre = ?";
        try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
            statement.setString(1, nombreMetodoPago);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("porcentaje");
            }
        }
        return 0.0; // Si no se encuentra el porcentaje, retornar 0
    }


    private double calcularTotalConPorcentaje(double porcentaje) {
        double total = 0.0;
        for (Producto producto : productosAgregados) {
            total += producto.getPrecio() * producto.getCantidad();
        }

        // Verificar si el porcentaje es positivo (incremento) o negativo (descuento)
        if (porcentaje >= 0) {
            // Aplicar incremento al total
            return total * (1 + porcentaje);
        } else {
            // Aplicar descuento al total
            return total * (1 - Math.abs(porcentaje));
        }
    }

   /********************************************* BOTÓN FIN (INCLUYE COMPROBANTE) **************************/

   // esta clase permite mandar la información a mi clase Comprobante para poder imprimir el comprobante//
   private void finalizarVenta() {

       String totalString = totalLabel.getText().replace(",", "."); // Reemplazar la coma por el punto
       double total = Double.parseDouble(totalString);

       // Obtener el método de pago seleccionado
       String metodoPagoSeleccionado = metodoPagoChoiceBox.getValue();

       // Obtener los detalles del cliente seleccionado
       String detallesCliente = idClienteLabel.getText();

       // Obtener el porcentaje y el total
       double porcentaje = obtenerPorcentaje();
       double totalVenta = obtenerPrecioFinal();

       // Generar el comprobante independientemente de la respuesta del usuario
       if (porcentaje >= 0) {
           // Aplicar incremento al total
           Comprobante.generarComprobantePDF(productosAgregados, total, metodoPagoSeleccionado, "comprobante", porcentaje, totalVenta, detallesCliente);
       } else {
           // Convertir el porcentaje a negativo y aplicar descuento al total
           double porcentajeNegativo = -Math.abs(porcentaje); // Se utiliza Math.abs para asegurar que sea negativo
           Comprobante.generarComprobantePDF(productosAgregados, total, metodoPagoSeleccionado, "comprobante", porcentajeNegativo, totalVenta, detallesCliente);
       }

       Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
       confirmacion.setTitle("Confirmación");
       confirmacion.setHeaderText("¿Desea imprimir un comprobante?");
       confirmacion.setContentText("Seleccione 'Aceptar' para imprimir el comprobante o 'Cancelar' para finalizar la venta sin imprimir.");

       Optional<ButtonType> result = confirmacion.showAndWait();

       if (result.isPresent() && result.get() == ButtonType.OK) {
           // El usuario ha aceptado imprimir el comprobante
           // Se puede agregar aquí una lógica adicional si es necesario
           // Obtener el nombre del archivo generado
           SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HHmm");
           String nombreArchivo = "C:/ControlFacil/comprobantes/comprobante_" + sdf.format(new Date()) + ".pdf";


           // Imprimir el comprobante
           ImpresoraComprobante.imprimirComprobante(nombreArchivo);
       }

       // Restar las unidades vendidas de los productos en la base de datos
       for (Producto producto : productosAgregados) {
           try {
               String consultaUpdate = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
               try (PreparedStatement statementUpdate = conexion.prepareStatement(consultaUpdate)) {
                   statementUpdate.setDouble(1, producto.getCantidad());
                   statementUpdate.setInt(2, producto.getId());
                   statementUpdate.executeUpdate();
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }

       // Limpiar los productos agregados y actualizar la tabla
       productosEliminados.addAll(productosAgregados);
       productosAgregados.clear();
       tablaCarrito.setItems(FXCollections.observableArrayList(productosAgregados));

       // Limpiar el total
       totalLabel.setText("");

       // Mostrar mensaje de venta finalizada
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle("Venta Finalizada");
       alert.setHeaderText(null);
       alert.setContentText("La venta ha sido finalizada correctamente.");
       alert.showAndWait();

       limpiarInformacionPantalla();

   }

    // Método para limpiar la información en la pantalla
    private void limpiarInformacionPantalla() {
        // Limpiar el campo de búsqueda de cliente
        //buscarClienteTextField.clear();

        // Limpiar las etiquetas de detalles del cliente
        idClienteLabel.setText("");
        direccionClienteLabel.setText("");
        telefonoClienteLabel.setText("");
        documentoClienteLabel.setText("");
        porcentajeLabel.setText("");
        finalLabel.setText("");
        // Restaurar la selección predeterminada del método de pago
        metodoPagoChoiceBox.getSelectionModel().clearSelection();

        // Limpiar cualquier otro componente o información en la pantalla que necesites
    }


    // Método para obtener el porcentaje desde la etiqueta porcentajeLabel manda información a través de botón fin//
    private double obtenerPorcentaje() {
        String porcentajeText = porcentajeLabel.getText();
        // Parsear el texto para obtener el valor numérico
        double porcentaje = Double.parseDouble(porcentajeText.split(":")[1].trim().replace("%", "")) / 100.0;
        return porcentaje;
    }

    private double obtenerPrecioFinal() {
        // Obtiene el texto del label finalLabel
        String textoFinal = finalLabel.getText();

        // Remueve los caracteres no numéricos excepto el punto decimal
        String precioFinalTexto = textoFinal.replaceAll("[^0-9.,]", "");

        // Reemplaza la coma por el punto decimal si es necesario
        precioFinalTexto = precioFinalTexto.replace(",", ".");

        // Convierte la cadena a un double
        double precioFinal = 0.0;
        try {
            precioFinal = Double.parseDouble(precioFinalTexto);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Maneja el error de formato
        }

        return precioFinal;
    }


    /********************************************* fin DE BOTON FIN **************************/
    private void mostrarDetallesCliente(String nombreCompleto) {
        try {
            // Consultar la base de datos para obtener más detalles del cliente
            String consulta = "SELECT * FROM clientes WHERE CONCAT(nombre, ' ', apellido) LIKE ?";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setString(1, "%" + nombreCompleto + "%");
                ResultSet resultSet = statement.executeQuery();

                List<String> nombresCompletos = new ArrayList<>();

                while (resultSet.next()) {
                    // Obtener los detalles del cliente
                    int id = resultSet.getInt("id");
                    String nombre = resultSet.getString("nombre");
                    String apellido = resultSet.getString("apellido");
                    String documento = resultSet.getString("documento");
                    String nombreCompletoResultado = nombre + " " + apellido;

                    // Agregar el nombre completo del cliente a la lista
                    nombresCompletos.add(nombreCompletoResultado);
                }

                // Verificar si se encontraron resultados
                if (!nombresCompletos.isEmpty()) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresCompletos.get(0), nombresCompletos);
                    dialog.setTitle("Seleccionar Cliente");
                    dialog.setHeaderText("Clientes encontrados:");
                    dialog.setContentText("Seleccione el cliente:");

                    // Permitir el redimensionamiento de la ventana
                    dialog.getDialogPane().setPrefSize(600, 400); // Establece el tamaño deseado
                    dialog.getDialogPane().setMinSize(600, 400); // Establece el tamaño mínimo
                    dialog.setResizable(true); // Permitir redimensionamiento

                    // Modificar el estilo de la fuente directamente en línea
                    dialog.getDialogPane().setStyle("-fx-font-size: 16pt;"); // Establece el tamaño de la fuente

                    Optional<String> resultado = dialog.showAndWait();
                    resultado.ifPresent(clienteSeleccionado -> {
                        // Mostrar los detalles del cliente en una ventana de diálogo
                        try {
                            String[] partesNombre = clienteSeleccionado.split(" ");
                            String nombre = partesNombre[0];
                            String apellido = partesNombre[1];

                            String consultaCliente = "SELECT * FROM clientes WHERE nombre = ? AND apellido = ?";
                            try (PreparedStatement statementCliente = conexion.prepareStatement(consultaCliente)) {
                                statementCliente.setString(1, nombre);
                                statementCliente.setString(2, apellido);
                                ResultSet resultSetCliente = statementCliente.executeQuery();

                                // Mostrar los detalles del cliente en una ventana de diálogo
                                if (resultSetCliente.next()) {
                                    int id = resultSetCliente.getInt("id");
                                    String direccion = resultSetCliente.getString("direccion");
                                    int telefono = resultSetCliente.getInt("telefono");
                                    String documento = resultSetCliente.getString("documento");

                                    // Llamar al método para mostrar los detalles del cliente en una ventana de diálogo
                                    mostrarDetallesClienteDialogo(clienteSeleccionado, id, direccion, telefono, documento);

                                    // Mostrar los detalles del cliente debajo del ComboBox
                                    mostrarDetallesClienteDebajoComboBox(clienteSeleccionado, id, direccion, telefono, documento);
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // Mostrar un mensaje si no se encontraron resultados
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Información");
                    alert.setHeaderText(null);
                    alert.setContentText("No se encontraron clientes que coincidan con la búsqueda.");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarDetallesClienteDialogo(String nombreCompleto, int id, String direccion, int telefono, String documento) {
        // Crear una ventana de diálogo para mostrar los detalles del cliente
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Cliente");
        alert.setHeaderText(null);
        alert.setContentText("Nombre: " + nombreCompleto + "\nID: " + id + "\nDirección: " + direccion + "\nTeléfono: " + telefono + "\nDocumento: " + documento);
        alert.showAndWait();
    }

    private void mostrarDetallesClienteDebajoComboBox(String nombreCompleto, int id, String direccion, int telefono, String documento) {
        // Obtener nombre y apellido a partir del nombre completo
        String[] partesNombre = nombreCompleto.split(" ");
        String nombre = partesNombre[0];
        String apellido = partesNombre[1];

        // Concatenar los detalles del cliente en una sola cadena
        String detallesCliente = "ID: " + id + "\nNombre: " + nombre + "\nApellido: " + apellido + "\nDirección: " + direccion + "\nTeléfono: " + telefono + "\nDocumento: " + documento;

        // Establecer la cadena de detalles en el Label
        idClienteLabel.setText(detallesCliente);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
