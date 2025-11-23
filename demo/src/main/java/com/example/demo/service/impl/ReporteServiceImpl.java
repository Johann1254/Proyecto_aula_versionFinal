package com.example.demo.service.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.entity.FiltroReporteDTO;
import com.example.demo.entity.MovimientoDTO;
import com.example.demo.entity.ReporteDTO;
import com.example.demo.model.mongo.Entrada;
import com.example.demo.model.mongo.Insumo;
import com.example.demo.model.mongo.Salida;
import com.example.demo.repos.mongo.EntradaRepository;
import com.example.demo.repos.mongo.InsumoRepository;
import com.example.demo.repos.mongo.SalidaRepository;
import com.example.demo.service.ReporteService;

@Service
public class ReporteServiceImpl implements ReporteService {

        private final InsumoRepository insumoRepository;
        private final EntradaRepository entradaRepository;
        private final SalidaRepository salidaRepository;

        public ReporteServiceImpl(InsumoRepository insumoRepository,
                        EntradaRepository entradaRepository,
                        SalidaRepository salidaRepository) {
                this.insumoRepository = insumoRepository;
                this.entradaRepository = entradaRepository;
                this.salidaRepository = salidaRepository;
        }

        @Override
        public List<ReporteDTO> generarReporte(FiltroReporteDTO filtro) {
                LocalDate inicio = LocalDate.parse(filtro.getFechaInicio());
                LocalDate fin = LocalDate.parse(filtro.getFechaFin());
                ZonedDateTime inicioUtc = inicio.atStartOfDay(ZoneOffset.UTC);
                ZonedDateTime finUtc = fin.atTime(23, 59, 59).atZone(ZoneOffset.UTC);

                List<Entrada> entradas = entradaRepository.findByFechaBetween(
                                inicioUtc.toLocalDate(), finUtc.toLocalDate());
                List<Salida> salidas = salidaRepository.findByFechaBetween(
                                inicioUtc.toLocalDate(), finUtc.toLocalDate());
                String tipoInforme = filtro.getTipoInforme();

                if ("Informe De Entradas".equalsIgnoreCase(tipoInforme)) {
                        return entradas.stream()
                                        .flatMap(e -> e.getDetalles().stream())
                                        .map(d -> {
                                                Insumo insumo = insumoRepository.findById(d.getInsumoId()).orElse(null);
                                                String nombre = (insumo != null) ? insumo.getNombre() : "Desconocido";
                                                int stock = (insumo != null) ? insumo.getStock() : 0;
                                                return new ReporteDTO(nombre, d.getCantidad(), 0, stock);
                                        })
                                        .collect(Collectors.toList());

                } else if ("Informe De Salidas".equalsIgnoreCase(tipoInforme)) {
                        return salidas.stream()
                                        .flatMap(s -> s.getDetalles().stream())
                                        .map(d -> {
                                                Insumo insumo = insumoRepository.findById(d.getInsumoId()).orElse(null);
                                                String nombre = (insumo != null) ? insumo.getNombre() : "Desconocido";
                                                int stock = (insumo != null) ? insumo.getStock() : 0;
                                                return new ReporteDTO(nombre, 0, d.getCantidad(), stock);
                                        })
                                        .collect(Collectors.toList());

                } else {
                        List<Insumo> insumos = insumoRepository.findAll();

                        return insumos.stream()
                                        .map(insumo -> {
                                                int totalEntradas = entradas.stream()
                                                                .flatMap(e -> e.getDetalles().stream())
                                                                .filter(d -> d.getInsumoId().equals(insumo.getId()))
                                                                .mapToInt(d -> d.getCantidad()).sum();

                                                int totalSalidas = salidas.stream()
                                                                .flatMap(s -> s.getDetalles().stream())
                                                                .filter(d -> d.getInsumoId().equals(insumo.getId()))
                                                                .mapToInt(d -> d.getCantidad()).sum();

                                                return new ReporteDTO(
                                                                insumo.getNombre(),
                                                                totalEntradas,
                                                                totalSalidas,
                                                                insumo.getStock());
                                        })
                                        .filter(r -> {
                                                if (filtro.isSoloMovimiento()) {
                                                        return r.getEntradas() > 0 || r.getSalidas() > 0;
                                                }
                                                return true;
                                        })
                                        .collect(Collectors.toList());
                }
        }

        @Override
public long contarReportes() {
    // Definimos "reportes" como entradas + salidas registradas
    return entradaRepository.count() + salidaRepository.count();
}


@Override
public List<MovimientoDTO> ultimosMovimientos() {

    List<MovimientoDTO> movimientos = new ArrayList<>();

    // Últimas 5 entradas
    entradaRepository.findTop5ByOrderByFechaDesc()
            .forEach(e -> movimientos.add(
                    new MovimientoDTO(
                            "Entrada de insumos",
                            e.getFecha().toString()
                    )
            ));

    // Últimas 5 salidas
    salidaRepository.findTop5ByOrderByFechaDesc()
            .forEach(s -> movimientos.add(
                    new MovimientoDTO(
                            "Salida de insumos",
                            s.getFecha().toString()
                    )
            ));

    // Ordenar por fecha (si quieres lo hacemos después bien fino)
    return movimientos.stream()
            .limit(5)
            .toList();
}

}
