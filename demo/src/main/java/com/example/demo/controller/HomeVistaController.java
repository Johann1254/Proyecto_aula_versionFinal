package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ColegioService;
import com.example.demo.service.DashboardService;
import com.example.demo.service.ReporteService;
import com.example.demo.service.UniformeService;

@Controller
public class HomeVistaController {

    @Autowired
    UniformeService uniformeService;

    @Autowired
    ColegioService colegioService;

    @Autowired
    ReporteService reporteService;

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping({ "/dashboard", "/home" })
    public String index() {
        return "home/home";
    }

    @GetMapping("/home_u")
    public String userHome(Model model) {

        model.addAttribute("totalUniformes", uniformeService.contarUniformes());
        model.addAttribute("totalColegios", colegioService.contarColegios());
        model.addAttribute("movimientos", reporteService.ultimosMovimientos());

        Map<String, Long> graficoUniformes = dashboardService.contarUniformesPorColegio();
        model.addAttribute("graficoUniformes", graficoUniformes);

        return "home/home_u";
    }

}
