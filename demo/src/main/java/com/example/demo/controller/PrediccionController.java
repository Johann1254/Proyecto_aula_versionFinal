package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.InsumoPrediccionDTO;
import com.example.demo.model.mongo.Insumo;
import com.example.demo.repos.mongo.InsumoRepository;
import com.example.demo.service.PrediccionService;

@RestController
@RequestMapping("/api/prediccion")
@CrossOrigin("*")
public class PrediccionController {

    private final PrediccionService prediccionService;
    private final InsumoRepository insumoRepository;

    public PrediccionController(PrediccionService prediccionService,
            InsumoRepository insumoRepository) {
        this.prediccionService = prediccionService;
        this.insumoRepository = insumoRepository;
    }

    @GetMapping("/{idInsumo}")
    public InsumoPrediccionDTO predecir(@PathVariable String idInsumo) throws Exception {

        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        InsumoPrediccionDTO dto = prediccionService.construirDTO(insumo);
        dto.setPrediccion(prediccionService.predecirAgotamiento(dto));

        return dto;
    }
}