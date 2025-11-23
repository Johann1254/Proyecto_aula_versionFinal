package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ColegioVistaController {


    @GetMapping("/colegio/registrar-colegio")
    public String registrarColegio() {
        return "colegio-uniforme/registrar-colegio"; // templates/registrar.html
    }

    @GetMapping("/calculadora")
    public String calculadora() {
        return "calculadora/calculadora"; // templates/calculadora.html
    }

    @GetMapping("/colegio/listar")
    public String listarColegios() {
        return "colegio-uniforme/colegio-listar"; // templates/colegio-uniforme/colegio-listar.html
    }

    @GetMapping("/colegio/editar/{id}")
    public String editarColegio(@PathVariable String id, Model model) {
        model.addAttribute("id", id);
        return "colegio-uniforme/editar_colegio";
    }

}
