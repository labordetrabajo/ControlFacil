package com.example.controlfacilfx;

import java.sql.Date;

public class VentaDiaria {
    private Date fecha;
    private double total;

    public VentaDiaria(Date fecha, double total) {
        this.fecha = fecha;
        this.total = total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
