package com.example.demo.repos.mongo;

import java.time.LocalDate;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.mongo.Salida;

@Repository
public interface SalidaRepository extends MongoRepository<Salida, String> {

    @org.springframework.data.mongodb.repository.Query("{ 'fecha': {$gte: ?0, $lte: ?1 } }")
    List<Salida> findByFechaBetween(LocalDate inicio, LocalDate fin); // Método para buscar salidas por rango de
                                                                              // fechas

    List<Salida> findByDescripcionContainingIgnoreCase(String term, Pageable pageable);

    long countByDescripcionContainingIgnoreCase(String term);

    @Aggregation(pipeline = {
            "{ $unwind: '$detalles' }",
            "{ $match: { 'detalles.insumoId': ?0, 'fecha': { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: null, total: { $sum: '$detalles.cantidad' } } }"
    })
    Integer sumSalidasPorInsumoRangoFecha(String insumoId, LocalDate inicio, LocalDate fin);

    List<Salida> findTop5ByOrderByFechaDesc();

}
