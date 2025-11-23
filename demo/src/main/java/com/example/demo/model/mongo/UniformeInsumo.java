package com.example.demo.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "uniforme_insumo")
public class UniformeInsumo {

    @Id
    private String id;
    private Double cantidadBase;
    private String unidadMedida;

    private String uniformeId; 
    private String insumoId;

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getCantidadBase() {
        return cantidadBase;
    }

    public void setCantidadBase(Double cantidadBase) {
        this.cantidadBase = cantidadBase;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getUniformeId() {
        return uniformeId;
    }

    public void setUniformeId(String uniformeId) {
        this.uniformeId = uniformeId;
    }

    public String getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(String insumoId) {
        this.insumoId = insumoId;
    }
}
