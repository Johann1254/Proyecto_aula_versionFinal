package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.ColegioConUniformesDTO;
import com.example.demo.model.mongo.Colegio;
import com.example.demo.model.mongo.Uniforme;
import com.example.demo.repos.mongo.ColegioRepository;
import com.example.demo.repos.mongo.UniformeRepository;

@Service
public class ColegioService {

    private final ColegioRepository colegioRepository;
    private final UniformeRepository uniformeRepository;

    public ColegioService(ColegioRepository colegioRepository, UniformeRepository uniformeRepository) {
        this.colegioRepository = colegioRepository;
        this.uniformeRepository = uniformeRepository;
    }

    public Colegio registrarColegio(Colegio colegio) {
        if (colegioRepository.existsByNombreIgnoreCase(colegio.getNombre())) {
            throw new IllegalArgumentException("Ya existe un colegio con ese nombre.");
        }
        return colegioRepository.save(colegio);
    }

    public List<Colegio> obtenerColegios() {
        return colegioRepository.findAll();
    }

    public Optional<Colegio> obtenerColegioPorId(String id) {
        return colegioRepository.findById(id);
    }

    public void eliminarColegio(String idColegio) {
        if (!colegioRepository.existsById(idColegio)) {
            throw new RuntimeException("Colegio no encontrado");
        }

        // Eliminar uniformes asociados
        uniformeRepository.deleteByColegioId(idColegio);

        // Eliminar colegio
        colegioRepository.deleteById(idColegio);
    }

    public Colegio actualizarColegioConUniformes(String idColegio, ColegioConUniformesDTO dto) {
        Colegio colegio = colegioRepository.findById(idColegio)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado"));

        colegio.setNombre(dto.getNombre());
        colegio.setDireccion(dto.getDireccion());
        colegioRepository.save(colegio);

        // Eliminar uniformes antiguos
        uniformeRepository.deleteByColegioId(idColegio);

        // Registrar nuevos uniformes
        if (dto.getUniformes() != null) {
            for (Uniforme u : dto.getUniformes()) {
                u.setColegioId(idColegio);
                uniformeRepository.save(u);
            }
        }

        return colegio;
    }

    public Colegio actualizarColegio(String id, Colegio datos) {
        return colegioRepository.findById(id).map(c -> {
            c.setNombre(datos.getNombre());
            c.setDireccion(datos.getDireccion());
            c.setUniformesIds(datos.getUniformesIds());
            return colegioRepository.save(c);
        }).orElse(null);
    }

        public long contarColegios() {
        return colegioRepository.count();
    }

}
