package com.example.controlfacilfx;

// Importaciones de librerías necesarias//
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;

// Clase principal para el menú y la interfaz de usuario//
public class Menu extends Application {

    // Variables de instancia//
    private Scanner scanner;
    private Connection conexion;
    private Stage primaryStage;
    private Scene originalScene; // Variable para almacenar la escena original//

    private ObservableList<Producto> productosList;

    // Constructor//
    public Menu() {
        this.scanner = new Scanner(System.in);
        this.productosList = FXCollections.observableArrayList();
        try {
            this.conexion = ConexionBD.obtenerConexion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método principal//
    public static void main(String[] args) {
        launch(args);
    }

    // Método para iniciar la aplicación JavaFX//
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        GridPane menuPane = menuPrincipal(primaryStage);

        // Cargar la imagen para el fondo
        Image backgroundImage = new Image("linea8.jpg");

        // Crear un objeto BackgroundImage con la imagen cargada
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Crear un objeto Background con el BackgroundImage
        Background background = new Background(backgroundImg);

        // Establecer el fondo del GridPane
        menuPane.setBackground(background);

        // Cargar los estilos generales
        Scene scene = new Scene(menuPane, 900, 700);
        scene.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());

        // Guardar la escena original
        originalScene = scene;

        // Mostrar la escena principal
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menú Principal");
        // Agregar una imagen de icono a la ventana
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/ventas.png")));
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //*************************************** MENU PRINCIPAL *********************************************************//

    // Método para mostrar el menú principal//
    public GridPane menuPrincipal(Stage stage) {
        GridPane menuPane = new GridPane();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setHgap(10);
        menuPane.setVgap(10);

        // Cargar la imagen "controlfacil.png"
        ImageView controlFacilImage = new ImageView(new Image(getClass().getResourceAsStream("/controlfacilfinal2oscuro.png")));
        controlFacilImage.setFitWidth(250); // Ajusta el ancho de la imagen según sea necesario
        controlFacilImage.setFitHeight(250); // Ajusta el alto de la imagen según sea necesario

        // Agregar la imagen al GridPane y centrarla
        menuPane.add(controlFacilImage, 0, 0, 3, 1);
        menuPane.setAlignment(Pos.CENTER);
        GridPane.setHalignment(controlFacilImage, javafx.geometry.HPos.CENTER);
        GridPane.setValignment(controlFacilImage, javafx.geometry.VPos.CENTER);

        // Botón Contacto
        Button contactoButton = new Button("Contacto");
        contactoButton.setOnAction(event -> {
            Contacto contacto = new Contacto("", "");
            contacto.start(new Stage());
        });
        contactoButton.setStyle("-fx-font-size: 20px;");
        ImageView contactoIcon = new ImageView(new Image(getClass().getResourceAsStream("/contacto.png")));
        contactoIcon.setFitWidth(40);
        contactoIcon.setFitHeight(40);
        contactoButton.setGraphic(contactoIcon);
        // Posicionar el botón en la esquina superior derecha


        //dejo los espacios en los nombres de los botones para que tengan el mismo tamaño//
        Button adminProductosButton = new Button("Productos");
        adminProductosButton.setStyle("-fx-font-size: 20px;");
        adminProductosButton.setOnAction(e -> stage.setScene((menuProductos(stage))));
        // Crear ImageView para el icono del botón de productos
        ImageView productosIcon = new ImageView(new Image(getClass().getResourceAsStream("/pruductos.png")));
        productosIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        productosIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        adminProductosButton.setGraphic(productosIcon); // agrega el icono al botón

        Button pedidoButton = new Button(" Pedidos ");
        pedidoButton.setStyle("-fx-font-size: 20px;");
         // Crear ImageView para el icono del botón de pedido
        ImageView pedidoIcon = new ImageView(new Image(getClass().getResourceAsStream("/pedido.png")));
        pedidoIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        pedidoIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        pedidoButton.setGraphic(pedidoIcon); // agrega el icono al botón
        pedidoButton.setOnAction(e -> {
            Pedidos pedidos = new Pedidos(conexion);
            pedidos.start(new Stage()); // Inicia la ventana de venta
        });


        Button ventaButton = new Button("  Ventas  ");
        ventaButton.setStyle("-fx-font-size: 20px;");
        ventaButton.setOnAction(e -> {
            Venta venta = new Venta(conexion);
            venta.start(new Stage()); // Inicia la ventana de venta
        });

        // Crear ImageView para el icono del botón de ventas
        ImageView ventasIcon = new ImageView(new Image(getClass().getResourceAsStream("/ventas.png")));
        ventasIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        ventasIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        ventaButton.setGraphic(ventasIcon); // agrega el icono al botón

        Button listaMetodosPagoButton = new Button("M.de Pago");
        listaMetodosPagoButton.setStyle("-fx-font-size: 20px;");
        listaMetodosPagoButton.setOnAction(e -> stage.setScene(menuMetodosPago(stage)));

      // Crear ImageView para el icono del botón de métodos de pago
        ImageView metodosPagoIcon = new ImageView(new Image(getClass().getResourceAsStream("/metododepago.png")));
        metodosPagoIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        metodosPagoIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        listaMetodosPagoButton.setGraphic(metodosPagoIcon); // agrega el icono al botón

        Button menuClientesButton = new Button("Clientes");
        menuClientesButton.setStyle("-fx-font-size: 20px;");
        menuClientesButton.setOnAction(e -> stage.setScene(menuClientes(stage)));

      // Crear ImageView para el icono del botón de clientes
        ImageView clientesIcon = new ImageView(new Image(getClass().getResourceAsStream("/clientes.png")));
        clientesIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        clientesIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        menuClientesButton.setGraphic(clientesIcon); // agrega el icono al botón

        Button menuRegistroventasButton = new Button("R.Ventas");
        menuRegistroventasButton.setStyle("-fx-font-size: 20px;");
        menuRegistroventasButton.setOnAction(e -> stage.setScene(menuRegistrosVentas(stage)));

      // Crear ImageView para el icono del botón de registro de ventas
        ImageView registroVentasIcon = new ImageView(new Image(getClass().getResourceAsStream("/registroventas.png")));
        registroVentasIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        registroVentasIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        menuRegistroventasButton.setGraphic(registroVentasIcon); // agrega el icono al botón




        Button salirButton = new Button("  Salir  ");
        salirButton.setStyle("-fx-font-size: 20px;");
        salirButton.setOnAction(e -> {
            System.out.println("Saliendo del programa. ¡Hasta luego!");
            stage.close();
        });

      // Crear ImageView para el icono del botón de salir
        ImageView salirIcon = new ImageView(new Image(getClass().getResourceAsStream("/salir.png")));
        salirIcon.setFitWidth(30); // ajusta el ancho del icono según sea necesario
        salirIcon.setFitHeight(30); // ajusta el alto del icono según sea necesario
        salirButton.setGraphic(salirIcon); // agrega el icono al botón

        // Agregar botones al GridPane//
        //menuPane.add(bienvenidaLabel, 0, 0, 3, 1);
        menuPane.add(adminProductosButton, 0, 1);
        menuPane.add(ventaButton, 1, 1);
        menuPane.add(salirButton, 1, 10);
        menuPane.add(listaMetodosPagoButton, 2, 1);
        menuPane.add(menuClientesButton, 0, 3);
        menuPane.add( menuRegistroventasButton, 1, 3);
        menuPane.add( contactoButton, 2, 3);
        menuPane.add(pedidoButton, 2, 4);

        return menuPane;
    }

    public Scene menuRegistrosVentas(Stage stage) {
        // Crear un nuevo GridPane para el menú de registros de ventas
        GridPane menuRegistrosVentasPane = new GridPane();
        menuRegistrosVentasPane.setAlignment(Pos.CENTER);
        menuRegistrosVentasPane.setHgap(10);
        menuRegistrosVentasPane.setVgap(10);

        // Agregar elementos a la interfaz gráfica
        Label registrosVentasLabel = new Label(" Registros de Ventas ");
        registrosVentasLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: black;");
        menuRegistrosVentasPane.add(registrosVentasLabel, 0, 0, 2, 1);

        // Botón para registros diarios
        Button registrosDiariosButton = new Button("Registros Diarios");
        registrosDiariosButton.setOnAction(e -> {
            RegistrosDiarios registrosDiarios = new RegistrosDiarios();
            registrosDiarios.start(new Stage());
        });
        registrosDiariosButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de registros diarios
        ImageView registrosDiariosIcon = new ImageView(new Image(getClass().getResourceAsStream("/ventasdiarias.png")));
        registrosDiariosIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        registrosDiariosIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        registrosDiariosButton.setGraphic(registrosDiariosIcon); // agrega el icono al botón

        menuRegistrosVentasPane.add(registrosDiariosButton, 0, 1);


        // Botón para registros mensuales
        Button registrosMensualesButton = new Button("Registros Mensuales");
        registrosMensualesButton.setOnAction(e -> {
            RegistrosMensuales registrosMensuales = new RegistrosMensuales();
            registrosMensuales.start(new Stage());
        });
        registrosMensualesButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de registros mensuales
        ImageView registrosMensualesIcon = new ImageView(new Image(getClass().getResourceAsStream("/ventasmensuales.png")));
        registrosMensualesIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        registrosMensualesIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        registrosMensualesButton.setGraphic(registrosMensualesIcon); // agrega el icono al botón

        menuRegistrosVentasPane.add(registrosMensualesButton, 1, 1);


        // Botón para volver al Menú Principal
        Button volverButton = new Button("  Menu Principal ");
        volverButton.setOnAction(e -> stage.setScene(originalScene)); // Asignar la escena original al Stage
        volverButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de volver al menú principal
        ImageView volverIcon = new ImageView(new Image(getClass().getResourceAsStream("/menuprincipal.png")));
        volverIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        volverIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        volverButton.setGraphic(volverIcon); // agrega el icono al botón

        menuRegistrosVentasPane.add(volverButton, 0, 2, 2, 1);

        // Cargar la imagen para el fondo
        Image backgroundImage = new Image("linea8.jpg");

        // Crear un objeto BackgroundImage con la imagen cargada
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Crear un objeto Background con el BackgroundImage
        Background background = new Background(backgroundImg);

        // Establecer el fondo del GridPane
        menuRegistrosVentasPane.setBackground(background);

        // Crear una escena más grande
        Scene menuRegistrosVentas = new Scene(menuRegistrosVentasPane, 900, 700); // Ajusta el ancho y alto según tus necesidades

        return menuRegistrosVentas;
    }

    //*************************************** MENU PRODUCTOS *********************************************************//
    public Scene menuProductos(Stage stage) {
        // Crear un nuevo GridPane para el menú de productos
        GridPane menuProductosPane = new GridPane();
        menuProductosPane.setAlignment(Pos.CENTER);
        menuProductosPane.setHgap(10);
        menuProductosPane.setVgap(10);

        // Agregar elementos a la interfaz gráfica
        Label productosLabel = new Label(" Administración de Productos ");
        productosLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #F0F0F0;");
        ImageView productosLabelimg = new ImageView(new Image(getClass().getResourceAsStream("/pruductos.png")));
        productosLabelimg.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        productosLabelimg.setFitHeight(40); // ajusta el alto del icono según sea necesario
        productosLabel.setGraphic(productosLabelimg); // agrega el icono al botón
        menuProductosPane.add(productosLabel, 0, 0, 2, 1);

        // Botón para agregar producto
        Button agregarProductoButton = new Button("Agregar Producto");
        agregarProductoButton.setOnAction(e -> agregarProducto(stage));
        agregarProductoButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de agregar producto
        ImageView agregarProductoIcon = new ImageView(new Image(getClass().getResourceAsStream("/agregar.png")));
        agregarProductoIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        agregarProductoIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        agregarProductoButton.setGraphic(agregarProductoIcon); // agrega el icono al botón

        menuProductosPane.add(agregarProductoButton, 0, 1);

        // Repite el proceso para los demás botones...

        // Botón para ver lista de productos
        Button listaProductosButton = new Button("Lista de Productos");
        listaProductosButton.setOnAction(e -> mostrarListaProductos());
        listaProductosButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de lista de productos
        ImageView listaProductosIcon = new ImageView(new Image(getClass().getResourceAsStream("/lista.png")));
        listaProductosIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        listaProductosIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        listaProductosButton.setGraphic(listaProductosIcon); // agrega el icono al botón

        menuProductosPane.add(listaProductosButton, 1, 1);

        // Botón para exportar a Excel
        Button exportarAExcelButton = new Button("  Exportar a Excel ");
        exportarAExcelButton.setOnAction(e -> exportarAExcel());
        exportarAExcelButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de exportar a Excel
        ImageView exportarAExcelIcon = new ImageView(new Image(getClass().getResourceAsStream("/exportarexcel.png")));
        exportarAExcelIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        exportarAExcelIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        exportarAExcelButton.setGraphic(exportarAExcelIcon); // agrega el icono al botón

        menuProductosPane.add(exportarAExcelButton, 0, 2);

        // Botón para volver al Menú Principal
        Button volverButton = new Button("   Menu Principal   ");
        volverButton.setOnAction(e -> stage.setScene(originalScene)); // Asignar la escena original al Stage
        volverButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de volver al menú principal
        ImageView volverIcon = new ImageView(new Image(getClass().getResourceAsStream("/menuprincipal.png")));
        volverIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        volverIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        volverButton.setGraphic(volverIcon); // agrega el icono al botón

        menuProductosPane.add(volverButton, 1, 2);

        // Cargar la imagen para el fondo
        Image backgroundImage = new Image("linea8.jpg");

        // Crear un objeto BackgroundImage con la imagen cargada
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Crear un objeto Background con el BackgroundImage
        Background background = new Background(backgroundImg);

        // Establecer el fondo del GridPane
        menuProductosPane.setBackground(background);

        // Crear una escena más grande
        Scene menuProductos = new Scene(menuProductosPane, 900, 700); // Ajusta el ancho y alto según tus necesidades

        return menuProductos;
    }


    // Método para agregar un producto//
    private void agregarProducto(Stage stage) {
        // Crear un nuevo Stage para la interfaz gráfica
        Stage agregarproductoStage = new Stage();
        agregarproductoStage.setTitle("Agregar Producto");

        // Crear un GridPane similar al utilizado en otras secciones
        GridPane agregarproductoPane = new GridPane();
        agregarproductoPane.setAlignment(Pos.CENTER);
        agregarproductoPane.setHgap(10);
        agregarproductoPane.setVgap(10);

        // Agregar elementos a la interfaz gráfica
        Label agregarproductoLabel = new Label("Agregar Producto");
        agregarproductoLabel.setStyle("-fx-font-size: 24pt;");
        agregarproductoPane.add(agregarproductoLabel, 0, 0, 2, 1);

        Label nombreLabel = new Label("Nombre:");
        nombreLabel.setStyle("-fx-font-size: 18pt;");
        TextField nombreTextField = new TextField();
        nombreTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(nombreLabel, 0, 1);
        agregarproductoPane.add(nombreTextField, 1, 1);

        Label cantidadLabel = new Label("Cantidad:");
        cantidadLabel.setStyle("-fx-font-size: 18pt;");
        TextField cantidadTextField = new TextField();
        cantidadTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(cantidadLabel, 0, 2);
        agregarproductoPane.add(cantidadTextField, 1, 2);

        Label precioProveedorLabel = new Label("Precio Proveedor:");
        precioProveedorLabel.setStyle("-fx-font-size: 18pt;");
        TextField precioProveedorTextField = new TextField();
        precioProveedorTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(precioProveedorLabel, 0, 3);
        agregarproductoPane.add(precioProveedorTextField, 1, 3);

        Label unidadLabel = new Label("Unidad:");
        unidadLabel.setStyle("-fx-font-size: 18pt;");
        TextField unidadTextField = new TextField();
        unidadTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(unidadLabel, 0, 4);
        agregarproductoPane.add(unidadTextField, 1, 4);

        Label precioLabel = new Label("Precio:");
        precioLabel.setStyle("-fx-font-size: 18pt;");
        TextField precioTextField = new TextField();
        precioTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(precioLabel, 0, 5);
        agregarproductoPane.add(precioTextField, 1, 5);

        Label codigodebarraLabel = new Label("Código de Barra:");
        codigodebarraLabel.setStyle("-fx-font-size: 18pt;");
        TextField codigodebarraTextField = new TextField();
        codigodebarraTextField.setStyle("-fx-font-size: 18pt;");
        agregarproductoPane.add(codigodebarraLabel, 0, 6);
        agregarproductoPane.add(codigodebarraTextField, 1, 6);

        Button agregarButton = new Button("Agregar");
        agregarButton.setStyle("-fx-font-size: 18pt;");
        agregarButton.setOnAction(e -> {
            try {
                // Validar los campos de texto antes de agregar el producto
                String nombre = nombreTextField.getText();
                if (nombre.isEmpty()) {
                    throw new IllegalArgumentException("Ingrese un nombre válido.");
                }

                double cantidad = Double.parseDouble(cantidadTextField.getText());

                double precioProveedor = Double.parseDouble(precioProveedorTextField.getText());

                String unidad = unidadTextField.getText();
                if (unidad.isEmpty()) {
                    throw new IllegalArgumentException("Ingrese una unidad válida.");
                }

                double precio = Double.parseDouble(precioTextField.getText());

                String codigodebarra = codigodebarraTextField.getText();
                if (codigodebarra.isEmpty()) {
                    throw new IllegalArgumentException("Ingrese un código de barra válido.");
                }

                // Insertar el nuevo producto en la base de datos sin especificar el ID
                String consulta = "INSERT INTO productos (nombre, cantidad, unidad, precioproveedor, precio, codigodebarra) VALUES (?, ?, ?, ?, ?,?)";
                try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                    statement.setString(1, nombre);
                    statement.setDouble(2, cantidad);
                    statement.setString(3, unidad);
                    statement.setDouble(4, precioProveedor);
                    statement.setDouble(5, precio);
                    statement.setString(6, codigodebarra);
                    statement.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                System.out.println("Producto agregado con éxito.");
                agregarproductoStage.close(); // Cerrar la ventana después de agregar el producto
                stage.setScene(originalScene); // Volver a la escena original
            } catch (NumberFormatException ex) {
                String mensaje = ex.getMessage();
                String valorIncorrecto = mensaje.substring(mensaje.lastIndexOf(":") + 2); // Obtiene el valor incorrecto del mensaje de la excepción
                if (mensaje.contains("For input string")) {
                    mensaje = "Formato  inválido. El valor ingresado '" + valorIncorrecto + "' no es un valor válido.";
                } else {
                    mensaje = "Error de formato de número.";
                }
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de formato");
                alert.setHeaderText("Formato de número inválido");
                alert.setContentText(mensaje);
                alert.getDialogPane().setPrefWidth(450);
                alert.getDialogPane().setStyle("-fx-font-size: 18pt;");
                alert.showAndWait();
            } catch (IllegalArgumentException ex) {
                // Mostrar alerta si hay campos de texto vacíos para cada campo individualmente
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Campo vacío");
                alert.setHeaderText("Campo obligatorio vacío");
                alert.setContentText(ex.getMessage());
                alert.getDialogPane().setPrefWidth(450);
                alert.getDialogPane().setStyle("-fx-font-size: 18pt;");
                alert.showAndWait();
            }
        });

        agregarproductoPane.add(agregarButton, 1, 7);

        // Crear una escena
        Scene agregarproductoScene = new Scene(agregarproductoPane, 900, 700);

        // Mostrar la nueva ventana
        agregarproductoStage.setScene(agregarproductoScene);
        agregarproductoStage.show();
    }



    //Con esto invoco a la clase ListaProductos//
    private void mostrarListaProductos() {
        ListaProductos listaProductos = new ListaProductos(conexion);
        listaProductos.mostrarVentana();
    }

    //Método para exportar los productos a Excel desde la base de datos//
    public void exportarAExcel() {
        // Ruta de la carpeta "exportaciones" dentro del proyecto
        String carpetaExportaciones = "exportaciones/";

        ExportarAExcel exportador = new ExportarAExcel(this.conexion);

        // Construir la ruta completa del archivo Excel en el directorio del proyecto
        Path filePath = Paths.get(carpetaExportaciones, "productos_exportados.xlsx");

        // Exportar los productos a Excel
        final String finalFilePath = exportador.exportarAExcel(filePath);

        // Crear una nueva ventana de diálogo
        Stage stage = new Stage();
        stage.setTitle("Éxito");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Crear un contenedor VBox para el contenido
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // Crear un enlace de hipertexto con la ruta del archivo
        Hyperlink hyperlink = new Hyperlink("Ruta del archivo: " + filePath);
        hyperlink.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white ");

        hyperlink.setOnAction(e -> {
            // Abrir el archivo con la aplicación predeterminada del sistema
            try {
                Desktop.getDesktop().open(filePath.toFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                // Manejar la excepción según tus necesidades
            }
        });

        // Agregar el enlace al contenedor
        vbox.getChildren().add(hyperlink);

        // Crear un botón de cierre
        Button closeButton = new Button("Cerrar");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setStyle("-fx-alignment: CENTER; ");
        closeButton.getStyleClass().add("styled-button");

        HBox hbox = new HBox(closeButton);
        hbox.setAlignment(Pos.CENTER);

        // Agregar el botón de cierre al contenedor
        vbox.getChildren().add(hbox); // Agregamos el hbox que contiene el botón "Cerrar"

        // Crear una escena con el contenedor y establecerla en la ventana
        Scene scene = new Scene(vbox, 700, 200); // Ajustar el ancho y alto según tus necesidades
        scene.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        vbox.setStyle("-fx-background-color:#191819;");
        // Deshabilitar la capacidad de cambiar el tamaño de la ventana
        stage.setResizable(false);

        // Mostrar la ventana y esperar hasta que se cierre
        stage.showAndWait();
    }


    //**************************************** MENU MÉTODOS DE PAGO **************************************************//

    // Método para mostrar el menú de métodos de pago//
    public Scene menuMetodosPago(Stage stage) {
        GridPane menuMetodosPagoPane = new GridPane();
        menuMetodosPagoPane.setAlignment(Pos.CENTER);
        menuMetodosPagoPane.setHgap(10);
        menuMetodosPagoPane.setVgap(20); // Aumenta el espacio vertical entre elementos

        // Agregar elementos a la interfaz gráfica
        Label menuMetodosPagoLabel = new Label("Métodos de Pago");
        menuMetodosPagoLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: black;"); // Aumenta el tamaño de la fuente
        menuMetodosPagoPane.add(menuMetodosPagoLabel, 0, 0, 2, 1);

        // Botón para agregar método de pago
        Button agregarMPButton = new Button("  Agregar M.P  ");
        agregarMPButton.setStyle("-fx-font-size: 20px;"); // Aumenta el tamaño de la fuente del botón
        agregarMPButton.setOnAction(e -> {
            // Llamar al método agregarMetodoPago de la clase Menu
            agregarMetodoPago(stage);
        });

        // Crear ImageView para el icono del botón de agregar método de pago
        ImageView agregarMPIcon = new ImageView(new Image(getClass().getResourceAsStream("/agregar.png")));
        agregarMPIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        agregarMPIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        agregarMPButton.setGraphic(agregarMPIcon); // agrega el icono al botón

        menuMetodosPagoPane.add(agregarMPButton, 0, 1);

        // Botón para mostrar lista de métodos de pago
        Button listaMPButton = new Button("  Lista de M.P  ");
        listaMPButton.setStyle("-fx-font-size: 20px;"); // Aumenta el tamaño de la fuente del botón
        listaMPButton.setOnAction(e -> {
            // Crear una instancia de ListaMetodosPago
            ListaMetodosPago listaMetodosPago = new ListaMetodosPago(conexion);
            // Mostrar la ventana
            listaMetodosPago.mostrarVentana();
        });

        // Crear ImageView para el icono del botón de lista de métodos de pago
        ImageView listaMPIcon = new ImageView(new Image(getClass().getResourceAsStream("/lista.png")));
        listaMPIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        listaMPIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        listaMPButton.setGraphic(listaMPIcon); // agrega el icono al botón

        menuMetodosPagoPane.add(listaMPButton, 1, 1);

        Button volverButton = new Button("Menu Principal");
        volverButton.setStyle("-fx-font-size: 20px;"); // Aumenta el tamaño de la fuente del botón
        volverButton.setOnAction(e -> stage.setScene(originalScene)); // Asignar la escena original al Stage

      // Crear ImageView para el icono del botón de volver
        ImageView volverIcon = new ImageView(new Image(getClass().getResourceAsStream("/menuprincipal.png")));
        volverIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        volverIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        volverButton.setGraphic(volverIcon); // agrega el icono al botón

      // Agregar botón de volver al GridPane
        menuMetodosPagoPane.add(volverButton, 1, 2); // Ajusta la posición del botón de volver


        Button exportarAExcelButton = new Button("Exportar a Excel");
        exportarAExcelButton.setOnAction(e -> exportarMetodosPagoAExcel()); // Llamando al nuevo método
        exportarAExcelButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de exportar a Excel
        ImageView exportarAExcelIcon = new ImageView(new Image(getClass().getResourceAsStream("/exportarexcel.png")));
        exportarAExcelIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        exportarAExcelIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        exportarAExcelButton.setGraphic(exportarAExcelIcon); // agrega el icono al botón

        menuMetodosPagoPane.add(exportarAExcelButton, 0, 2);

        // Cargar la imagen para el fondo
        Image backgroundImage = new Image("linea8.jpg");

        // Crear un objeto BackgroundImage con la imagen cargada
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Crear un objeto Background con el BackgroundImage
        Background background = new Background(backgroundImg);

        // Establecer el fondo del GridPane
        menuMetodosPagoPane.setBackground(background);

        Scene menuMetodosPago = new Scene(menuMetodosPagoPane, 900, 700); // Ajusta el tamaño de la escena
        menuMetodosPago.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());

        return menuMetodosPago;
    }



    // Método para agregar un nuevo método de pago//
    private void agregarMetodoPago(Stage stage) {
        // Crear un nuevo Stage para la interfaz gráfica
        Stage agregarMetodoPagoStage = new Stage();
        agregarMetodoPagoStage.setTitle("Agregar Método de Pago");

        // Crear un GridPane similar al utilizado en otras secciones
        GridPane agregarMetodoPagoPane = new GridPane();
        agregarMetodoPagoPane.setAlignment(Pos.CENTER);
        agregarMetodoPagoPane.setHgap(10);
        agregarMetodoPagoPane.setVgap(10);

        // Agregar elementos a la interfaz gráfica
        Label agregarMetodoPagoLabel = new Label("Agregar Nuevo Método de Pago");
        agregarMetodoPagoLabel.setStyle("-fx-font-size: 24pt;");
        agregarMetodoPagoPane.add(agregarMetodoPagoLabel, 0, 0, 2, 1);

        Label nombreLabel = new Label("Nombre:");
        nombreLabel.setStyle("-fx-font-size: 18pt;");
        TextField nombreTextField = new TextField();
        nombreTextField.setStyle("-fx-font-size: 18pt;");
        agregarMetodoPagoPane.add(nombreLabel, 0, 1);
        agregarMetodoPagoPane.add(nombreTextField, 1, 1);

        Label tipoLabel = new Label("Tipo:");
        tipoLabel.setStyle("-fx-font-size: 18pt;");
        TextField tipoTextField = new TextField();
        tipoTextField.setStyle("-fx-font-size: 18pt;");
        agregarMetodoPagoPane.add(tipoLabel, 0, 2);
        agregarMetodoPagoPane.add(tipoTextField, 1, 2);

        Label porcentajeLabel = new Label("Porcentaje:");
        porcentajeLabel.setStyle("-fx-font-size: 18pt;");
        TextField porcentajeTextField = new TextField();
        porcentajeTextField.setStyle("-fx-font-size: 18pt;");
        agregarMetodoPagoPane.add(porcentajeLabel, 0, 3);
        agregarMetodoPagoPane.add(porcentajeTextField, 1, 3);

        Button agregarButton = new Button("Agregar");
        agregarButton.setStyle("-fx-font-size: 18pt;");
        agregarButton.setOnAction(e -> {
            // Lógica para agregar un nuevo método de pago a la base de datos
            String nombre = nombreTextField.getText();
            String tipo = tipoTextField.getText();
            double porcentaje = Double.parseDouble(porcentajeTextField.getText());

            // Insertar el nuevo método de pago en la base de datos sin especificar el ID
            String consulta = "INSERT INTO metodosdepago (nombre, tipo, porcentaje) VALUES (?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setString(1, nombre);
                statement.setString(2, tipo);
                statement.setDouble(3, porcentaje);
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Método de pago agregado con éxito.");
            agregarMetodoPagoStage.close(); // Cerrar la ventana después de agregar el método de pago
            stage.setScene(originalScene); // Volver a la escena original
        });

        agregarMetodoPagoPane.add(agregarButton, 1, 4);

        // Crear una escena
        Scene agregarMetodoPagoScene = new Scene(agregarMetodoPagoPane, 900, 700);

        // Mostrar la nueva ventana
        agregarMetodoPagoStage.setScene(agregarMetodoPagoScene);
        agregarMetodoPagoStage.show();
    }
    //Método para exportar los métodos de pago a Excel desde la base de datos//
    private void exportarMetodosPagoAExcel() {
        // Ruta de la carpeta "exportaciones" dentro del proyecto
        String carpetaExportaciones = "C:/ControlFacil/exportaciones/";

        ExportarAExcelMetodosPago exportador = new ExportarAExcelMetodosPago(this.conexion);

        // Construir la ruta completa del archivo Excel en el directorio del proyecto
        Path filePath = Paths.get(carpetaExportaciones, "metodos_pago_exportados.xlsx");

        // Exportar los métodos de pago a Excel
        final String finalFilePath = exportador.exportarMetodosPagoAExcel(filePath);

        // Crear una nueva ventana de diálogo
        Stage stage = new Stage();
        stage.setTitle("Éxito");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Crear un contenedor VBox para el contenido
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // Crear un enlace de hipertexto con la ruta del archivo
        Hyperlink hyperlink = new Hyperlink("Ruta del archivo: " + filePath);
        hyperlink.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white ");

        hyperlink.setOnAction(e -> {
            // Abrir el archivo con la aplicación predeterminada del sistema
            try {
                Desktop.getDesktop().open(filePath.toFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                // Manejar la excepción según tus necesidades
            }
        });

        // Agregar el enlace al contenedor
        vbox.getChildren().add(hyperlink);

        // Crear un botón de cierre
        Button closeButton = new Button("Cerrar");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setStyle("-fx-alignment: CENTER; ");
        closeButton.getStyleClass().add("styled-button");

        HBox hbox = new HBox(closeButton);
        hbox.setAlignment(Pos.CENTER);

        // Agregar el botón de cierre al contenedor
        vbox.getChildren().add(hbox); // Agregamos el hbox que contiene el botón "Cerrar"

        // Crear una escena con el contenedor y establecerla en la ventana
        Scene scene = new Scene(vbox, 700, 200); // Ajustar el ancho y alto según tus necesidades
        scene.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        vbox.setStyle("-fx-background-color:#191819;");
        // Deshabilitar la capacidad de cambiar el tamaño de la ventana
        stage.setResizable(false);

        // Mostrar la ventana y esperar hasta que se cierre
        stage.showAndWait();
    }


    //******************************************* MENU CLIENTES **********************************************************//

    // Método para mostrar el menú de la sección clientes//
    public Scene menuClientes(Stage stage) {
        GridPane menuClientesPane = new GridPane();
        menuClientesPane.setAlignment(Pos.CENTER);
        menuClientesPane.setHgap(10);
        menuClientesPane.setVgap(20);

        // Etiqueta para el título
        Label menuClientesLabel = new Label("Clientes");
        menuClientesLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: black;");
        menuClientesPane.add(menuClientesLabel, 0, 0, 2, 1);

        // Botón para agregar cliente
        Button agregarClienteButton = new Button("Agregar Cliente");
        agregarClienteButton.setStyle("-fx-font-size: 20px;");
        agregarClienteButton.setOnAction(e -> agregarCliente(stage)); // Aquí se llama al método agregarCliente

        // Crear ImageView para el icono del botón de agregar cliente
        ImageView agregarClienteIcon = new ImageView(new Image(getClass().getResourceAsStream("/agregar.png")));
        agregarClienteIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        agregarClienteIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        agregarClienteButton.setGraphic(agregarClienteIcon); // agrega el icono al botón

        menuClientesPane.add(agregarClienteButton, 0, 1);

        // Botón para mostrar lista de clientes
        Button listaClientesButton = new Button("Lista de Clientes");
        listaClientesButton.setStyle("-fx-font-size: 20px;");
        listaClientesButton.setOnAction(e -> {
            // Lógica para mostrar la lista de clientes
            ListaClientes listaClientes = new ListaClientes(conexion);
            listaClientes.mostrarVentana();
        });

        // Crear ImageView para el icono del botón de lista de clientes
        ImageView listaClientesIcon = new ImageView(new Image(getClass().getResourceAsStream("/lista.png")));
        listaClientesIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        listaClientesIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        listaClientesButton.setGraphic(listaClientesIcon); // agrega el icono al botón

        menuClientesPane.add(listaClientesButton, 1, 1);

        // Botón para volver al menú principal
        Button volverButton = new Button("Menú Principal");
        volverButton.setStyle("-fx-font-size: 20px;");
        volverButton.setOnAction(e -> stage.setScene(originalScene));

        // Crear ImageView para el icono del botón de volver al menú principal
        ImageView volverIcon = new ImageView(new Image(getClass().getResourceAsStream("/menuprincipal.png")));
        volverIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        volverIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        volverButton.setGraphic(volverIcon); // agrega el icono al botón

        menuClientesPane.add(volverButton, 1, 2);

        Button exportarClientesAExcelButton = new Button("Exportar a Excel");
        exportarClientesAExcelButton.setOnAction(e -> exportarClientesAExcel());
        exportarClientesAExcelButton.setStyle("-fx-font-size: 20px;");

        // Crear ImageView para el icono del botón de exportar a Excel
        ImageView exportarClientesAExcelIcon = new ImageView(new Image(getClass().getResourceAsStream("/exportarexcel.png")));
        exportarClientesAExcelIcon.setFitWidth(40); // ajusta el ancho del icono según sea necesario
        exportarClientesAExcelIcon.setFitHeight(40); // ajusta el alto del icono según sea necesario
        exportarClientesAExcelButton.setGraphic(exportarClientesAExcelIcon); // agrega el icono al botón

        menuClientesPane.add(exportarClientesAExcelButton, 0, 2);

        // Cargar la imagen para el fondo
        Image backgroundImage = new Image("linea8.jpg");

        // Crear un objeto BackgroundImage con la imagen cargada
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Crear un objeto Background con el BackgroundImage
        Background background = new Background(backgroundImg);

        // Establecer el fondo del GridPane
        menuClientesPane.setBackground(background);

        Scene menuClientesScene = new Scene(menuClientesPane, 900, 700);
        menuClientesScene.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());

        return menuClientesScene;
    }

    // Método para agregar un nuevo cliente//
    private void agregarCliente(Stage stage) {
        // Crear un nuevo Stage para la interfaz gráfica
        Stage agregarClienteStage = new Stage();
        agregarClienteStage.setTitle("Agregar Cliente");

        // Crear un GridPane similar al utilizado en otras secciones
        GridPane agregarClientePane = new GridPane();
        agregarClientePane.setAlignment(Pos.CENTER);
        agregarClientePane.setHgap(10);
        agregarClientePane.setVgap(10);

        // Agregar elementos a la interfaz gráfica
        Label agregarClienteLabel = new Label("Agregar Nuevo Cliente");
        agregarClienteLabel.setStyle("-fx-font-size: 24pt;");
        agregarClientePane.add(agregarClienteLabel, 0, 0, 2, 1);

        Label nombreLabel = new Label("Nombre:");
        nombreLabel.setStyle("-fx-font-size: 18pt;");
        TextField nombreTextField = new TextField();
        nombreTextField.setStyle("-fx-font-size: 18pt;");
        agregarClientePane.add(nombreLabel, 0, 1);
        agregarClientePane.add(nombreTextField, 1, 1);

        Label apellidoLabel = new Label("Apellido:");
        apellidoLabel.setStyle("-fx-font-size: 18pt;");
        TextField apellidoTextField = new TextField();
        apellidoTextField.setStyle("-fx-font-size: 18pt;");
        agregarClientePane.add(apellidoLabel, 0, 2);
        agregarClientePane.add(apellidoTextField, 1, 2);

        Label documentoLabel = new Label("Documento:");
        documentoLabel.setStyle("-fx-font-size: 18pt;");
        TextField documentoTextField = new TextField();
        documentoTextField.setStyle("-fx-font-size: 18pt;");
        agregarClientePane.add(documentoLabel, 0, 3);
        agregarClientePane.add(documentoTextField, 1, 3);

        Label direccionLabel = new Label("Dirección:");
        direccionLabel.setStyle("-fx-font-size: 18pt;");
        TextField direccionTextField = new TextField();
        direccionTextField.setStyle("-fx-font-size: 18pt;");
        agregarClientePane.add(direccionLabel, 0, 4);
        agregarClientePane.add(direccionTextField, 1, 4);

        Label telefonoLabel = new Label("Teléfono:");
        telefonoLabel.setStyle("-fx-font-size: 18pt;");
        TextField telefonoTextField = new TextField();
        telefonoTextField.setStyle("-fx-font-size: 18pt;");
        agregarClientePane.add(telefonoLabel, 0, 5);
        agregarClientePane.add(telefonoTextField, 1, 5);

        Button agregarButton = new Button("Agregar");
        agregarButton.setStyle("-fx-font-size: 18pt;");
        agregarButton.setOnAction(e -> {
            // Lógica para agregar un nuevo cliente a la base de datos
            String nombre = nombreTextField.getText();
            String apellido = apellidoTextField.getText();
            int documento = Integer.parseInt(documentoTextField.getText());
            String direccion = direccionTextField.getText();
            int telefono = Integer.parseInt(telefonoTextField.getText());

            // Insertar el nuevo cliente en la base de datos sin especificar el ID
            String consulta = "INSERT INTO clientes (nombre, apellido, documento, direccion, telefono) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(consulta)) {
                statement.setString(1, nombre);
                statement.setString(2, apellido);
                statement.setInt(3, documento);
                statement.setString(4, direccion);
                statement.setInt(5, telefono);
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Cliente agregado con éxito.");
            agregarClienteStage.close(); // Cerrar la ventana después de agregar el cliente
            stage.setScene(originalScene); // Volver a la escena original
        });

        agregarClientePane.add(agregarButton, 1, 6);

        // Crear una escena
        Scene agregarClienteScene = new Scene(agregarClientePane, 900, 700);

        // Mostrar la nueva ventana
        agregarClienteStage.setScene(agregarClienteScene);
        agregarClienteStage.show();
    }
    //Método para exportar los clientes a Excel desde la base de datos//
    private void exportarClientesAExcel() {
        // Ruta de la carpeta "exportaciones" dentro del proyecto
        Path carpetaExportaciones = Paths.get("C:/ControlFacil/exportaciones/");

        ExportarAExcelClientes exportador = new ExportarAExcelClientes(this.conexion);

        // Concatena la ruta de la carpeta con el nombre del archivo
        Path filePath = carpetaExportaciones.resolve("clientes_exportados.xlsx");

        // Exporta los clientes a Excel
        final String finalFilePath = exportador.exportarClientesAExcel(filePath);

        Stage stage = new Stage();
        stage.setTitle("Éxito");
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Hyperlink hyperlink = new Hyperlink("Ruta del archivo: " + filePath);
        hyperlink.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white ");

        hyperlink.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(filePath.toFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        vbox.getChildren().add(hyperlink);

        Button closeButton = new Button("Cerrar");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setStyle("-fx-alignment: CENTER; ");
        closeButton.getStyleClass().add("styled-button");

        HBox hbox = new HBox(closeButton);
        hbox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(hbox); // Agregamos el hbox que contiene el botón "Cerrar"

        Scene scene = new Scene(vbox, 700, 200);
        scene.getStylesheets().add(MenuStyle.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        vbox.setStyle("-fx-background-color:#191819;");
        stage.setResizable(false);

        stage.showAndWait();
    }

}
