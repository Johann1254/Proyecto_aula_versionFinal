package com.example.demo.model.mongo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "uniformes")
public class Uniforme {

    @Id
    private String id;

    private String prenda;
    private String tipo;
    private String talla;
    private String genero;
    private String colegioId;  // Referencia al colegio (solo ID)

    private List<InsumoRequerido> insumos;  // Lista de insumos necesarios

    // --- Clase interna para los insumos requeridos ---
    public static class InsumoRequerido {
        private String nombre;
        private String unidadMedida;
        private double cantidad;

        public InsumoRequerido() {}

        public InsumoRequerido(String nombre, String unidadMedida, double cantidad) {
            this.nombre = nombre;
            this.unidadMedida = unidadMedida;
            this.cantidad = cantidad;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUnidadMedida() {
            return unidadMedida;
        }

        public void setUnidadMedida(String unidadMedida) {
            this.unidadMedida = unidadMedida;
        }

        public double getCantidad() {
            return cantidad;
        }

        public void setCantidad(double cantidad) {
            this.cantidad = cantidad;
        }
    }

    // --- Getters y Setters del uniforme ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrenda() {
        return prenda;
    }

    public void setPrenda(String prenda) {
        this.prenda = prenda;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getColegioId() {
        return colegioId;
    }

    public void setColegioId(String colegioId) {
        this.colegioId = colegioId;
    }

    public List<InsumoRequerido> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<InsumoRequerido> insumos) {
        this.insumos = insumos;
    }
}
