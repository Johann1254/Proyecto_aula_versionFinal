package com.example.demo.entity;

public class MovimientoDTO {

    private String descripcion;
    private String fecha;

    public MovimientoDTO(String descripcion, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }
}
