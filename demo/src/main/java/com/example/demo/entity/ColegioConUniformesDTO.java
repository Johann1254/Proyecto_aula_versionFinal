package com.example.demo.entity;

import java.util.List;

import com.example.demo.model.mongo.Uniforme;

public class ColegioConUniformesDTO {
    private String nombre;
    private String direccion;
    private List<Uniforme> uniformes;

    public ColegioConUniformesDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Uniforme> getUniformes() {
        return uniformes;
    }

    public void setUniformes(List<Uniforme> uniformes) {
        this.uniformes = uniformes;
    }
}
