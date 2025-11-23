package com.example.demo.service.impl;

import java.util.Locale;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.entity.DashboardDTO;
import com.example.demo.entity.MovimientoDTO;
import com.example.demo.model.mongo.Colegio;
import com.example.demo.model.mongo.DetalleEntrada;
import com.example.demo.model.mongo.DetalleSalida;
import com.example.demo.model.mongo.Entrada;
import com.example.demo.model.mongo.Salida;
import com.example.demo.repos.mongo.ColegioRepository;
import com.example.demo.repos.mongo.EntradaRepository;
import com.example.demo.repos.mongo.InsumoRepository;
import com.example.demo.repos.mongo.SalidaRepository;
import com.example.demo.repos.mongo.UniformeRepository;
import com.example.demo.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

        private final InsumoRepository insumoRepository;
        private final EntradaRepository entradaRepository;
        private final SalidaRepository salidaRepository;
        private final ColegioRepository colegioRepository;
        private final UniformeRepository uniformeRepository;

        public DashboardServiceImpl(InsumoRepository insumoRepository,
                        EntradaRepository entradaRepository,
                        SalidaRepository salidaRepository, ColegioRepository colegioRepository,
                        UniformeRepository uniformeRepository) {
                this.insumoRepository = insumoRepository;
                this.entradaRepository = entradaRepository;
                this.salidaRepository = salidaRepository;
                this.colegioRepository = colegioRepository;
                this.uniformeRepository = uniformeRepository;
        }

        @Override
        public DashboardDTO generarResumen() {
                int totalInsumos = (int) insumoRepository.count(); // o cast si devuelve long
                int totalEntradas = calcularTotalEntradas();
                int totalSalidas = calcularTotalSalidas();
                int insumosBajoStock = insumoRepository.findByStockLessThan(1).size();
                Map<String, Integer> entradasPorMes = calcularEntradasPorMes();
                Map<String, Integer> salidasPorMes = calcularSalidasPorMes();
                long totalUniformes = uniformeRepository.count();
                long totalColegios = colegioRepository.count();

                List<UniformesPorColegio> resultado = uniformeRepository.contarUniformesPorColegio();

                Map<String, Integer> grafico = new LinkedHashMap<>();

                for (UniformesPorColegio item : resultado) {
                        grafico.put(
                                        item.get_id(), // id del colegio
                                        item.getTotal());
                }

                return new DashboardDTO(
                                totalInsumos,
                                totalEntradas,
                                totalSalidas,
                                insumosBajoStock,
                                entradasPorMes,
                                salidasPorMes, totalUniformes,
                                totalColegios,
                                List.of(), // por ahora vacío
                                grafico);
        }

        // metodo para obtener el total de entradas sin sumar las cantidades
        private int calcularTotalEntradas() {
                return (int) entradaRepository.count();
        }

        private int calcularTotalSalidas() {
                return (int) salidaRepository.count();
        }

        private Map<String, Integer> calcularEntradasPorMes() {
                List<Entrada> entradas = entradaRepository.findAll();
                return entradas.stream()
                                .collect(Collectors.groupingBy(
                                                e -> e.getFecha().getMonth().getDisplayName(TextStyle.SHORT,
                                                                new Locale("es")),
                                                TreeMap::new,
                                                Collectors.summingInt(e -> e.getDetalles().stream()
                                                                .mapToInt(DetalleEntrada::getCantidad)
                                                                .sum())));
        }

        private Map<String, Integer> calcularSalidasPorMes() {
                List<Salida> salidas = salidaRepository.findAll();
                return salidas.stream()
                                .collect(Collectors.groupingBy(
                                                s -> s.getFecha().getMonth().getDisplayName(TextStyle.SHORT,
                                                                new Locale("es")),
                                                TreeMap::new,
                                                Collectors.summingInt(s -> s.getDetalles().stream()
                                                                .mapToInt(DetalleSalida::getCantidad)
                                                                .sum())));
        }

        @Override
        public Map<String, Long> contarUniformesPorColegio() {

                Map<String, Long> resumen = new LinkedHashMap<>();

                List<Colegio> colegios = colegioRepository.findAll();

                for (Colegio colegio : colegios) {
                        long cantidad = uniformeRepository.countByColegioId(colegio.getId());
                        resumen.put(colegio.getNombre(), cantidad);
                }

                return resumen;
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
                                                                e.getFecha().toString())));

                // Últimas 5 salidas
                salidaRepository.findTop5ByOrderByFechaDesc()
                                .forEach(s -> movimientos.add(
                                                new MovimientoDTO(
                                                                "Salida de insumos",
                                                                s.getFecha().toString())));

                // Ordenar por fecha (si quieres lo hacemos después bien fino)
                return movimientos.stream()
                                .limit(5)
                                .toList();
        }

        

}
