package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.entity.DashboardDTO;
import com.example.demo.entity.MovimientoDTO;

public interface DashboardService {

    DashboardDTO generarResumen();

    long contarReportes();

    List<MovimientoDTO> ultimosMovimientos();

    Map<String, Long> contarUniformesPorColegio();
}