package com.example.controlfacilfx;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Pedido {
    private final IntegerProperty id;
    private final StringProperty fecha;
    private final StringProperty hora;
    private final StringProperty cliente;
    private final StringProperty productos;
    private final DoubleProperty total;
    private final StringProperty metodoDePago;
    private final DoubleProperty porcentaje;
    private final DoubleProperty precioFinal;
    private final StringProperty estado;
    private final StringProperty horarioEntrega;
    private final StringProperty fechaEntrega;
    private final StringProperty hipervinculo;
    private final StringProperty vehiculo;
    private final StringProperty direccionEntrega;

    public Pedido(int id, String fecha, String hora, String cliente, String productos, double total, String metodoDePago, double porcentaje, double precioFinal, String estado, String horarioEntrega, String fechaEntrega, String hipervinculo, String vehiculo, String direccionEntrega) {
        this.id = new SimpleIntegerProperty(id);
        this.fecha = new SimpleStringProperty(fecha);
        this.hora = new SimpleStringProperty(hora);
        this.cliente = new SimpleStringProperty(cliente);
        this.productos = new SimpleStringProperty(productos);
        this.total = new SimpleDoubleProperty(total);
        this.metodoDePago = new SimpleStringProperty(metodoDePago);
        this.porcentaje = new SimpleDoubleProperty(porcentaje);
        this.precioFinal = new SimpleDoubleProperty(precioFinal);
        this.estado = new SimpleStringProperty(estado);
        this.horarioEntrega = new SimpleStringProperty(horarioEntrega);
        this.fechaEntrega = new SimpleStringProperty(fechaEntrega);
        this.hipervinculo = new SimpleStringProperty(hipervinculo);
        this.vehiculo = new SimpleStringProperty(vehiculo);
        this.direccionEntrega = new SimpleStringProperty(direccionEntrega);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getFecha() { return fecha.get(); }
    public void setFecha(String fecha) { this.fecha.set(fecha); }
    public StringProperty fechaProperty() { return fecha; }

    public String getHora() { return hora.get(); }
    public void setHora(String hora) { this.hora.set(hora); }
    public StringProperty horaProperty() { return hora; }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String cliente) { this.cliente.set(cliente); }
    public StringProperty clienteProperty() { return cliente; }

    public String getProductos() { return productos.get(); }
    public void setProductos(String productos) { this.productos.set(productos); }
    public StringProperty productosProperty() { return productos; }

    public double getTotal() { return total.get(); }
    public void setTotal(double total) { this.total.set(total); }
    public DoubleProperty totalProperty() { return total; }

    public String getMetodoDePago() { return metodoDePago.get(); }
    public void setMetodoDePago(String metodoDePago) { this.metodoDePago.set(metodoDePago); }
    public StringProperty metodoDePagoProperty() { return metodoDePago; }

    public double getPorcentaje() { return porcentaje.get(); }
    public void setPorcentaje(double porcentaje) { this.porcentaje.set(porcentaje); }
    public DoubleProperty porcentajeProperty() { return porcentaje; }

    public double getPrecioFinal() { return precioFinal.get(); }
    public void setPrecioFinal(double precioFinal) { this.precioFinal.set(precioFinal); }
    public DoubleProperty precioFinalProperty() { return precioFinal; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    public String getHorarioEntrega() { return horarioEntrega.get(); }
    public void setHorarioEntrega(String horarioEntrega) { this.horarioEntrega.set(horarioEntrega); }
    public StringProperty horarioEntregaProperty() { return horarioEntrega; }

    public String getFechaEntrega() { return fechaEntrega.get(); }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega.set(fechaEntrega); }
    public StringProperty fechaEntregaProperty() { return fechaEntrega; }

    public String getHipervinculo() { return hipervinculo.get(); }
    public void setHipervinculo(String hipervinculo) { this.hipervinculo.set(hipervinculo); }
    public StringProperty hipervinculoProperty() { return hipervinculo; }

    public String getVehiculo() { return vehiculo.get(); }
    public void setVehiculo(String vehiculo) { this.vehiculo.set(vehiculo); }
    public StringProperty vehiculoProperty() { return vehiculo; }

    public String getDireccionEntrega() { return direccionEntrega.get(); }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega.set(direccionEntrega); }
    public StringProperty direccionEntregaProperty() { return direccionEntrega; }
    public LocalDate getFechaEntregaLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            return LocalDate.parse(this.getFechaEntrega(), formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + this.getFechaEntrega());
            return null;
        }
    }
}
