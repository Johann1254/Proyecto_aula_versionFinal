package com.example.demo.repos.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.mongo.Uniforme;
import com.example.demo.service.impl.UniformesPorColegio;

public interface UniformeRepository extends MongoRepository<Uniforme, String> {
    void deleteByColegioId(String colegioId);
    
    List<Uniforme> findByColegioId(String colegioId);

    long countByColegioId(String colegioId);

     @Aggregation(pipeline = {
        "{ $group: { _id: '$colegioId', total: { $sum: 1 } } }"
    })
    List<UniformesPorColegio> contarUniformesPorColegio();

}
