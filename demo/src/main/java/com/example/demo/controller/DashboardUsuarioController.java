package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ColegioService;
import com.example.demo.service.UniformeService;

@RestController
@RequestMapping("/api/dashboard-u")
public class DashboardUsuarioController {

    private final ColegioService colegioService;
    private final UniformeService uniformeService;

    public DashboardUsuarioController(ColegioService colegioService,
                                       UniformeService uniformeService) {
        this.colegioService = colegioService;
        this.uniformeService = uniformeService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> resumenUsuario() {
        Map<String, Object> resumen = new HashMap<>();

        long totalColegios = colegioService.obtenerColegios().size();
        long totalUniformes = colegioService.obtenerColegios()
                .stream()
                .mapToLong(c -> uniformeService.obtenerUniformesPorColegio(c.getId()).size())
                .sum();

        resumen.put("totalColegios", totalColegios);
        resumen.put("totalUniformes", totalUniformes);

        return ResponseEntity.ok(resumen);
    }
}