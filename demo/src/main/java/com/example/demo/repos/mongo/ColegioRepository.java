package com.example.demo.repos.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.mongo.Colegio;

public interface ColegioRepository extends MongoRepository<Colegio, String> {
    boolean existsByNombreIgnoreCase(String nombre);
}