package com.example.demo.repos.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.model.mongo.Proveedor;

public interface ProveedorRepository extends MongoRepository<Proveedor, String> {

    boolean existsByNombre(String nombre);
    boolean existsByNit(String nit);

}
