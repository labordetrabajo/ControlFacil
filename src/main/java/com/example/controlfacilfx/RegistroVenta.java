package com.example.controlfacilfx;

import java.sql.Date;
import java.sql.Time;

public class RegistroVenta {
    private int id;
    private Date fecha;
    private Time hora;
    private String cliente;
    private String productos;
    private double total;
    private String metodoDePago;
    private double porcentaje;
    private double precioFinal;

    // Constructor
    public RegistroVenta() {
        // Constructor vacío
    }

    // Getters y setters para cada atributo
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    // Constructor
    public RegistroVenta(int id, Date fecha, Time hora, String cliente, String productos, double total, String metodoDePago, double porcentaje, double precioFinal) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.cliente = cliente;
        this.productos = productos;
        this.total = total;
        this.metodoDePago = metodoDePago;
        this.porcentaje = porcentaje;
        this.precioFinal = precioFinal;
    }
}
