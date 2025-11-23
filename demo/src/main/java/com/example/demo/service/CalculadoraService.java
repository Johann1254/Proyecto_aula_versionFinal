package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.mongo.Insumo;
import com.example.demo.model.mongo.Uniforme;
import com.example.demo.repos.mongo.InsumoRepository;
import com.example.demo.repos.mongo.UniformeRepository;

import java.util.*;

@Service
public class CalculadoraService {

    private final UniformeRepository uniformeRepository;
    private final InsumoRepository insumoRepository;

    public CalculadoraService(UniformeRepository uniformeRepository, InsumoRepository insumoRepository) {
        this.uniformeRepository = uniformeRepository;
        this.insumoRepository = insumoRepository;
    }

    public Map<String, Object> verificarDisponibilidad(String idUniforme, int cantidad) {

        Map<String, Object> resultado = new HashMap<>();

        Uniforme uniforme = uniformeRepository.findById(idUniforme).orElse(null);
        if (uniforme == null) {
            resultado.put("error", "Uniforme no encontrado");
            return resultado;
        }

        List<Uniforme.InsumoRequerido> insumosRequeridos = uniforme.getInsumos();
        if (insumosRequeridos == null || insumosRequeridos.isEmpty()) {
            resultado.put("error", "El uniforme no tiene insumos asociados");
            return resultado;
        }

        List<Map<String, Object>> detalles = new ArrayList<>();
        boolean haySuficiente = true;

        for (Uniforme.InsumoRequerido insumoReq : insumosRequeridos) {

            String nombreInsumo = insumoReq.getNombre();
            double stockNecesario = insumoReq.getCantidad() * cantidad;

            Insumo insumo = insumoRepository.findByNombre(nombreInsumo).orElse(null);

            if (insumo == null) {

                detalles.add(Map.of(
                        "insumo", nombreInsumo,
                        "estado", "❌ No existe en inventario",
                        "stockActual", 0,
                        "stockNecesario", stockNecesario,
                        "stockRestante", 0
                ));

                haySuficiente = false;
                continue;
            }

            double stockActual = insumo.getStock();
            double stockRestante = stockActual - stockNecesario;

            boolean suficiente = stockRestante >= 0;

            if (!suficiente) haySuficiente = false;

            detalles.add(Map.of(
                    "insumo", nombreInsumo,
                    "estado", suficiente ? "✅ Disponible" : "⚠️ Insuficiente",
                    "stockActual", stockActual,
                    "stockNecesario", stockNecesario,
                    "stockRestante", Math.max(stockRestante, 0)
            ));
        }

        resultado.put("uniforme", uniforme.getPrenda());
        resultado.put("cantidad", cantidad);
        resultado.put("disponible", haySuficiente);
        resultado.put("detalles", detalles);

        return resultado;
    }
}