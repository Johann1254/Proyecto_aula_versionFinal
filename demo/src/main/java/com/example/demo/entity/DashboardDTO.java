package com.example.demo.entity;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private int totalInsumos;
    private int totalEntradas;
    private int totalSalidas;
    private int insumosBajoStock;
    private Map<String, Integer> entradasPorMes;
    private Map<String, Integer> salidasPorMes;

    private long totalUniformes;
    private long totalColegios;
    private List<MovimientoDTO> movimientos;
    private Map<String, Integer> graficoUniformes;

    public DashboardDTO(
            int totalInsumos,
            int totalEntradas,
            int totalSalidas,
            int insumosBajoStock,
            Map<String, Integer> entradasPorMes,
            Map<String, Integer> salidasPorMes,
            long totalUniformes,
            long totalColegios,
            List<MovimientoDTO> movimientos,
            Map<String, Integer> graficoUniformes) {

        this.totalInsumos = totalInsumos;
        this.totalEntradas = totalEntradas;
        this.totalSalidas = totalSalidas;
        this.insumosBajoStock = insumosBajoStock;
        this.entradasPorMes = entradasPorMes;
        this.salidasPorMes = salidasPorMes;
        this.totalUniformes = totalUniformes;
        this.totalColegios = totalColegios;
        this.movimientos = movimientos;
        this.graficoUniformes = graficoUniformes;
    }

    public long getTotalUniformes() {
        return totalUniformes;
    }

    public void setTotalUniformes(long totalUniformes) {
        this.totalUniformes = totalUniformes;
    }

    public long getTotalColegios() {
        return totalColegios;
    }

    public void setTotalColegios(long totalColegios) {
        this.totalColegios = totalColegios;
    }

    public List<MovimientoDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoDTO> movimientos) {
        this.movimientos = movimientos;
    }

    public Map<String, Integer> getGraficoUniformes() {
        return graficoUniformes;
    }

    public void setGraficoUniformes(Map<String, Integer> graficoUniformes) {
        this.graficoUniformes = graficoUniformes;
    }

    public int getTotalInsumos() {
        return totalInsumos;
    }

    public void setTotalInsumos(int totalInsumos) {
        this.totalInsumos = totalInsumos;
    }

    public int getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(int totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public int getTotalSalidas() {
        return totalSalidas;
    }

    public void setTotalSalidas(int totalSalidas) {
        this.totalSalidas = totalSalidas;
    }

    public int getInsumosBajoStock() {
        return insumosBajoStock;
    }

    public void setInsumosBajoStock(int insumosBajoStock) {
        this.insumosBajoStock = insumosBajoStock;
    }

    public Map<String, Integer> getEntradasPorMes() {
        return entradasPorMes;
    }

    public void setEntradasPorMes(Map<String, Integer> entradasPorMes) {
        this.entradasPorMes = entradasPorMes;
    }

    public Map<String, Integer> getSalidasPorMes() {
        return salidasPorMes;
    }

    public void setSalidasPorMes(Map<String, Integer> salidasPorMes) {
        this.salidasPorMes = salidasPorMes;
    }

}
