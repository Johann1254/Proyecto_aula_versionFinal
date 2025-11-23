package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.service.CalculadoraService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/calculadora")
@CrossOrigin(origins = "*")
public class CalculadoraController {

    private final CalculadoraService calculadoraService;

    public CalculadoraController(CalculadoraService calculadoraService) {
        this.calculadoraService = calculadoraService;
    }

    /** Verificar disponibilidad de insumos */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam String idUniforme,
            @RequestParam int cantidad) {

        try {
            Map<String, Object> resultado = calculadoraService.verificarDisponibilidad(idUniforme, cantidad);

            if (resultado.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
            }

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al verificar: " + e.getMessage()));
        }
    }
}
