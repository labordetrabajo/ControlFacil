package com.example.controlfacilfx;

import javafx.beans.property.*;

//clase para tener los getter y setter para manejar la base de datos//
public class MetodoPago {
    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty tipo;
    private final DoubleProperty porcentaje;

    // Constructor
    public MetodoPago(int id, String nombre, String tipo, double porcentaje) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.tipo = new SimpleStringProperty(tipo);
        this.porcentaje = new SimpleDoubleProperty(porcentaje);
    }

    // Métodos para obtener propiedades
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public DoubleProperty porcentajeProperty() {
        return porcentaje;
    }

    // Getters y Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }

    public double getPorcentaje() {
        return porcentaje.get();
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje.set(porcentaje);
    }

    // Método toString para imprimir el objeto de manera legible
    @Override
    public String toString() {
        return "MetodoPago{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", porcentaje=" + porcentaje +
                '}';
    }

    // Método principal para probar la clase
    public static void main(String[] args) {
        // Crear un objeto de ejemplo
        MetodoPago metodoPago = new MetodoPago(1, "Tarjeta de crédito", "Crédito", 5.5);

        // Imprimir los detalles del objeto
        System.out.println(metodoPago);
    }
}
