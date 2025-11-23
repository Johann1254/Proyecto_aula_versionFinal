package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReporteVistaController {

    // Muestra el formulario de filtros para generar el reporte
    @GetMapping("/reporte")
    public String mostrarFormularioReporte(Model model, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("layoutName", esAdmin ? "layout/layout" : "layout/layout_u");

        return "reportes/reporte"; // vista del formulario
    }

    // Muestra el reporte generado (tabla y totales)
    @GetMapping("/reportes")
    public String mostrarReporteGenerado(Model model, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("layoutName", esAdmin ? "layout/layout" : "layout/layout_u");

        return "reportes/reporte"; // vista del reporte generado
    }

    // Alias opcional para /generar-reporte
    @GetMapping("/generar-reporte")
    public String generarReporte(Model model, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("layoutName", esAdmin ? "layout/layout" : "layout/layout_u");

        return "reportes/genera-reporte";
    }
}
