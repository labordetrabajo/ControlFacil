package com.example.controlfacilfx;

import javafx.beans.property.*;

// Clase para manejar los productos
public class Producto {

    // Propiedades de JavaFX
    private final IntegerProperty id;
    private final StringProperty nombre;
    private final DoubleProperty cantidad;
    private final DoubleProperty precio;
    private final StringProperty unidad;
    private final DoubleProperty precioProveedor; // Nueva propiedad para el precio del proveedor
    private final StringProperty codigoBarras; // Nueva propiedad para el código de barras

    // Constructor
    public Producto(int id, String nombre, double cantidad, double precio, double precioProveedor, String unidad, String codigoBarras) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
        this.precioProveedor = new SimpleDoubleProperty(precioProveedor);
        this.unidad = new SimpleStringProperty(unidad);
        this.codigoBarras = new SimpleStringProperty(codigoBarras);
    }

    // Métodos getter y setter para acceder y modificar las propiedades
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public double getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(double cantidad) {
        this.cantidad.set(cantidad);
    }

    public DoubleProperty cantidadProperty() {
        return cantidad;
    }

    public double getPrecio() {
        return precio.get();
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public double getPrecioProveedor() {
        return precioProveedor.get();
    }

    public void setPrecioProveedor(double precioProveedor) {
        this.precioProveedor.set(precioProveedor);
    }

    public DoubleProperty precioProveedorProperty() {
        return precioProveedor;
    }

    public String getUnidad() {
        return unidad.get();
    }

    public void setUnidad(String unidad) {
        this.unidad.set(unidad);
    }

    public StringProperty unidadProperty() {
        return unidad;
    }

    public String getCodigoBarras() {
        return codigoBarras.get();
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras.set(codigoBarras);
    }

    public StringProperty codigoBarrasProperty() {
        return codigoBarras;
    }
}
