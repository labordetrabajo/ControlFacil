package com.example.controlfacilfx;

import javafx.beans.property.*;

public class Cliente {
    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty apellido;
    private final IntegerProperty documento;
    private final StringProperty direccion;
    private final IntegerProperty telefono;

    public Cliente(int id, String nombre, String apellido, int documento, String direccion, int telefono) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.documento = new SimpleIntegerProperty(documento);
        this.direccion = new SimpleStringProperty(direccion);
        this.telefono = new SimpleIntegerProperty(telefono);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getApellido() {
        return apellido.get();
    }

    public StringProperty apellidoProperty() {
        return apellido;
    }

    public int getDocumento() {
        return documento.get();
    }

    public IntegerProperty documentoProperty() {
        return documento;
    }

    public String getDireccion() {
        return direccion.get();
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    public int getTelefono() {
        return telefono.get();
    }
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setApellido(String apellido) {
        this.apellido.set(apellido);
    }

    public void setDocumento(int documento) {
        this.documento.set(documento);
    }

    public void setDireccion(String direccion) {
        this.direccion.set(direccion);
    }

    public void setTelefono(int telefono) {
        this.telefono.set(telefono);
    }



    public IntegerProperty telefonoProperty() {
        return telefono;
    }
}
