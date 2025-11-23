package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.mongo.Uniforme;
import com.example.demo.repos.mongo.ColegioRepository;
import com.example.demo.repos.mongo.UniformeRepository;

@Service
public class UniformeService {

    private final UniformeRepository uniformeRepository;
    private final ColegioRepository colegioRepository;

    public UniformeService(UniformeRepository uniformeRepository, ColegioRepository colegioRepository) {
        this.uniformeRepository = uniformeRepository;
        this.colegioRepository = colegioRepository;
    }

    public Uniforme registrarUniforme(String colegioId, Uniforme uniforme) {
        if (!colegioRepository.existsById(colegioId)) {
            throw new RuntimeException("Colegio no encontrado");
        }

        uniforme.setColegioId(colegioId); // corregido: antes se usaba setId()
        return uniformeRepository.save(uniforme);
    }

    public List<Uniforme> obtenerUniformesPorColegio(String colegioId) {
        return uniformeRepository.findByColegioId(colegioId);
    }

    public Optional<Uniforme> obtenerUniformePorId(String id) { // ✅ Optional
        return uniformeRepository.findById(id);
    }

    public long contarUniformes() {
        return uniformeRepository.count();
    }

}