package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.InsumoPrediccionDTO;
import com.example.demo.model.mongo.Insumo;
import com.example.demo.repos.mongo.InsumoRepository;
import com.example.demo.service.PrediccionService;

@Controller
@RequestMapping("/prediccion")
public class PrediccionVistaController {

    @Autowired
    private InsumoRepository insumoRepository;

    @Autowired
    private PrediccionService prediccionService;

    @GetMapping
    public String listarPredicciones(Model model) {

        List<Insumo> insumos = insumoRepository.findAll();
        List<InsumoPrediccionDTO> predicciones = new ArrayList<>();

        for (Insumo insumo : insumos) {

            // CONSTRUIR DTO
            InsumoPrediccionDTO dto = prediccionService.construirDTO(insumo);

            try {
                String pred = prediccionService.predecirAgotamiento(dto);
                dto.setPrediccion(pred);

            } catch (Exception e) {
                dto.setPrediccion("Error");
                System.out.println("Error prediciendo insumo " + insumo.getNombre() + ": " + e.getMessage());
            }

            predicciones.add(dto);
        }

        model.addAttribute("predicciones", predicciones);
        return "prediccion/listar";
    }
}
