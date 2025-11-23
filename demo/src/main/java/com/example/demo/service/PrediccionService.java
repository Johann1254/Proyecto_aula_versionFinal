package com.example.demo.service;

import com.example.demo.entity.InsumoPrediccionDTO;
import com.example.demo.model.mongo.DetalleEntrada;
import com.example.demo.model.mongo.DetalleSalida;
import com.example.demo.model.mongo.Insumo;

import com.example.demo.repos.mongo.EntradaRepository;
import com.example.demo.repos.mongo.SalidaRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import java.time.LocalDate;

@Service
public class PrediccionService {

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private SalidaRepository salidaRepository;

    private Classifier modelo;
    private Instances dataModel;

    @PostConstruct
    public void cargarModelo() throws Exception {

        // === Cargar modelo ===
        InputStream is = getClass().getResourceAsStream("/modeloj48.model");
        ObjectInputStream ois = new ObjectInputStream(is);
        modelo = (Classifier) ois.readObject();
        ois.close();

        // === Cargar ARFF (el filtrado final) ===
        InputStream arffStream = getClass().getResourceAsStream(
            "/prediccion_agotamiento_con_id2.csv.arff"
        );

        dataModel = new Instances(new BufferedReader(new InputStreamReader(arffStream)));
        dataModel.setClassIndex(dataModel.numAttributes() - 1);
    }

    // =======================================================
    //   Construir DTO automáticamente
    // =======================================================
    public InsumoPrediccionDTO construirDTO(Insumo insumo) {

        LocalDate hoy = LocalDate.now();
        int anio = hoy.getYear();
        int mes = hoy.getMonthValue();

        InsumoPrediccionDTO dto = new InsumoPrediccionDTO();
        dto.setNombreInsumo(insumo.getNombre());
        dto.setAnio(anio);
        dto.setMes(mes);
        dto.setEntradasMes(calcularEntradas(insumo.getId(), anio, mes));
        dto.setSalidasMes(calcularSalidas(insumo.getId(), anio, mes));
        dto.setConsumoPromedio3m(calcularConsumo3Meses(insumo.getId(), hoy));
        dto.setStockActual(insumo.getStock());

        return dto;
    }

    // =======================================================
    //   Predicción
    // =======================================================
    public String predecirAgotamiento(InsumoPrediccionDTO dto) throws Exception {

        Instance inst = new DenseInstance(dataModel.numAttributes());
        inst.setDataset(dataModel);

        // Usar setValueSafe para evitar errores por missing o atributos inexistentes
        setValueSafe(inst, "anio", dto.getAnio());
        setValueSafe(inst, "mes", dto.getMes());
        setValueSafe(inst, "entradas_mes", dto.getEntradasMes());
        setValueSafe(inst, "salidas_mes", dto.getSalidasMes());
        setValueSafe(inst, "consumo_promedio_3m", dto.getConsumoPromedio3m());
        setValueSafe(inst, "stock_actual", dto.getStockActual());

        double resultado = modelo.classifyInstance(inst);

        return dataModel.classAttribute().value((int) resultado);
    }

    // =======================================================
    //   Método seguro para asignación de valores a la Instance
    // =======================================================
    private void setValueSafe(Instance inst, String attribute, double v) {
        Attribute a = inst.dataset().attribute(attribute);

        if (a == null) {
            System.out.println("Atributo no encontrado en ARFF: " + attribute);
            return;
        }

        if (Double.isNaN(v))
            inst.setMissing(a);
        else
            inst.setValue(a, v);
    }

    // =======================================================
    //   Cálculos
    // =======================================================
    private int calcularEntradas(String insumoId, int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        return entradaRepository.findByFechaBetween(inicio, fin).stream()
                .flatMap(e -> e.getDetalles().stream())
                .filter(d -> d.getInsumoId().equals(insumoId))
                .mapToInt(DetalleEntrada::getCantidad)
                .sum();
    }

    private int calcularSalidas(String insumoId, int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        return salidaRepository.findByFechaBetween(inicio, fin).stream()
                .flatMap(s -> s.getDetalles().stream())
                .filter(d -> d.getInsumoId().equals(insumoId))
                .mapToInt(DetalleSalida::getCantidad)
                .sum();
    }

    private double calcularConsumo3Meses(String insumoId, LocalDate referencia) {

        int total = 0;

        for (int i = 1; i <= 3; i++) {
            LocalDate fecha = referencia.minusMonths(i);
            int salidas = calcularSalidas(insumoId, fecha.getYear(), fecha.getMonthValue());
            total += salidas;
        }

        return total / 3.0;
    }
}
