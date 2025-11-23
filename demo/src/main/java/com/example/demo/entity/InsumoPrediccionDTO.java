package com.example.demo.entity;

public class InsumoPrediccionDTO {

    private String nombreInsumo;
    private int anio;
    private int mes;
    private int entradasMes;
    private int salidasMes;
    private double consumoPromedio3m;
    private int stockActual;

    private String prediccion;

    // este NO se envía al modelo, el modelo lo predice:
    private String agotado; // SI / NO

    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getEntradasMes() {
        return entradasMes;
    }

    public void setEntradasMes(int entradasMes) {
        this.entradasMes = entradasMes;
    }

    public int getSalidasMes() {
        return salidasMes;
    }

    public void setSalidasMes(int salidasMes) {
        this.salidasMes = salidasMes;
    }

    public double getConsumoPromedio3m() {
        return consumoPromedio3m;
    }

    public void setConsumoPromedio3m(double consumoPromedio3m) {
        this.consumoPromedio3m = consumoPromedio3m;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public String getAgotado() {
        return agotado;
    }

    public void setAgotado(String agotado) {
        this.agotado = agotado;
    }

    public InsumoPrediccionDTO(String nombreInsumo, int anio, int mes, int entradasMes,
            int salidasMes, double consumoPromedio3m, int stockActual, String agotado) {
        this.nombreInsumo = nombreInsumo;
        this.anio = anio;
        this.mes = mes;
        this.entradasMes = entradasMes;
        this.salidasMes = salidasMes;
        this.consumoPromedio3m = consumoPromedio3m;
        this.stockActual = stockActual;
        this.agotado = agotado;
    }

    public InsumoPrediccionDTO() {
    }

    public String getPrediccion() {
        return prediccion;
    }

    public void setPrediccion(String prediccion) {
        this.prediccion = prediccion;
    }

}