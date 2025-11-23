package com.example.demo.repos.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.mongo.UniformeInsumo;

import java.util.List;

public interface UniformeInsumoRepository extends MongoRepository<UniformeInsumo, String> {
    List<UniformeInsumo> findByUniformeId(String uniformeId);
}