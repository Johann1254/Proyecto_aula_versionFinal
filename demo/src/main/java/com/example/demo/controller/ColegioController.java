package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ColegioConUniformesDTO;
import com.example.demo.model.mongo.Colegio;
import com.example.demo.model.mongo.Insumo;
import com.example.demo.model.mongo.Uniforme;
import com.example.demo.repos.mongo.ColegioRepository;
import com.example.demo.repos.mongo.UniformeRepository;
import com.example.demo.service.ColegioService;
import com.example.demo.service.InsumoService;
import com.example.demo.service.UniformeService;

import java.util.Map;

@RestController
@RequestMapping("/api/colegios")
@CrossOrigin(origins = "*")
public class ColegioController {

    private final ColegioService colegioService;
    private final UniformeService uniformeService;
    private final InsumoService insumoService;

    @Autowired
    private ColegioRepository repo;

    @Autowired
    private UniformeRepository uniformeRepo;

    public ColegioController(ColegioService colegioService, UniformeService uniformeService,
            InsumoService insumoService) {
        this.colegioService = colegioService;
        this.uniformeService = uniformeService;
        this.insumoService = insumoService;
    }

    // -------------------------------------------------------------
    // COLEGIOS
    // -------------------------------------------------------------

    /** Registrar un nuevo colegio */
    @PostMapping
    public ResponseEntity<?> registrarColegio(@RequestBody Colegio colegio) {
        try {
            Colegio nuevo = colegioService.registrarColegio(colegio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Listar todos los colegios */
    @GetMapping
    public ResponseEntity<List<Colegio>> listarColegios() {
        return ResponseEntity.ok(colegioService.obtenerColegios());
    }

    /** Obtener un colegio por ID */
    @GetMapping("/{idColegio}")
    public ResponseEntity<?> obtenerColegio(@PathVariable String idColegio) {
        return colegioService.obtenerColegioPorId(idColegio)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Colegio no encontrado")));
    }

    // -------------------------------------------------------------
    // UNIFORMES
    // -------------------------------------------------------------

    /** Registrar un uniforme asociado a un colegio */
    @PostMapping("/{idColegio}/uniformes")
    public ResponseEntity<?> registrarUniforme(
            @PathVariable String idColegio,
            @RequestBody Uniforme uniforme) {

        if (uniforme.getPrenda() == null || uniforme.getPrenda().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de la prenda es obligatorio."));
        }

        Uniforme nuevoUniforme = uniformeService.registrarUniforme(idColegio, uniforme);
        if (nuevoUniforme == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Colegio no encontrado. No se pudo registrar el uniforme."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUniforme);
    }

    /** Obtener todos los uniformes de un colegio */
    @GetMapping("/{idColegio}/uniformes")
    public ResponseEntity<?> listarUniformesPorColegio(@PathVariable String idColegio) {
        List<Uniforme> uniformes = uniformeService.obtenerUniformesPorColegio(idColegio);
        if (uniformes.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay uniformes registrados para este colegio."));
        }
        return ResponseEntity.ok(uniformes);
    }

    /** Obtener un uniforme específico */
    @GetMapping("/uniformes/{idUniforme}")
    public ResponseEntity<?> obtenerUniformePorId(@PathVariable String idUniforme) {
        return uniformeService.obtenerUniformePorId(idUniforme)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Uniforme no encontrado")));
    }

    // ---------------------------
    // INSUMOS
    // ---------------------------

    // Registrar insumo
    @PostMapping("/insumos")
    public ResponseEntity<?> registrarInsumo(@RequestBody Insumo insumo) {
        Insumo nuevo = insumoService.registrarInsumo(insumo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Listar insumos
    @GetMapping("/insumos")
    public ResponseEntity<List<Insumo>> listarInsumos() {
        return ResponseEntity.ok(insumoService.obtenerTodos());
    }

    // Ruta usada por el frontend
    @GetMapping("/insumos/listar")
    public ResponseEntity<List<Insumo>> listarInsumosAlternativo() {
        return ResponseEntity.ok(insumoService.obtenerTodos());
    }

    /** Actualizar stock de un insumo */
    @PutMapping("/insumos/{id}")
    public ResponseEntity<?> actualizarStock(
            @PathVariable String id,
            @RequestParam int nuevoStock) {

        try {
            insumoService.actualizarStock(id, nuevoStock);
            return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/registrarCompleto")
    public ResponseEntity<?> registrarColegioCompleto(@RequestBody ColegioConUniformesDTO dto) {
        try {
            // 1️⃣ Crear el colegio
            Colegio colegio = new Colegio();
            colegio.setNombre(dto.getNombre());
            colegio.setDireccion(dto.getDireccion());
            Colegio guardado = colegioService.registrarColegio(colegio);

            // 2️⃣ Registrar cada uniforme asociado
            if (dto.getUniformes() != null) {
                for (Uniforme uniforme : dto.getUniformes()) {
                    uniforme.setColegioId(guardado.getId());
                    uniformeService.registrarUniforme(guardado.getId(), uniforme);
                }
            }

            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error registrando colegio completo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{idColegio}")
    public ResponseEntity<?> eliminarColegio(@PathVariable String idColegio) {
        try {
            colegioService.eliminarColegio(idColegio);
            return ResponseEntity.ok(Map.of("mensaje", "Colegio eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{idColegio}/completo")
    public ResponseEntity<?> actualizarColegioCompleto(
            @PathVariable String idColegio,
            @RequestBody ColegioConUniformesDTO dto) {
        try {
            Colegio actualizado = colegioService.actualizarColegioConUniformes(idColegio, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarColegio(
            @PathVariable String id,
            @RequestBody Colegio datos) {

        return repo.findById(id).map(c -> {

            if (datos.getNombre() != null)
                c.setNombre(datos.getNombre());

            if (datos.getDireccion() != null)
                c.setDireccion(datos.getDireccion());

            repo.save(c);
            return ResponseEntity.ok(c);

        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{colegioId}/uniformes")
    public ResponseEntity<?> actualizarUniformes(
            @PathVariable String colegioId,
            @RequestBody List<Uniforme> uniformes) {

        // Borrar uniformes antiguos del colegio
        uniformeRepo.deleteByColegioId(colegioId);

        // Guardar uniformes nuevos
        uniformes.forEach(u -> u.setColegioId(colegioId));
        uniformeRepo.saveAll(uniformes);

        return ResponseEntity.ok("Uniformes actualizados");
    }
}
