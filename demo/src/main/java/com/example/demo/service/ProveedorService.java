package com.example.demo.service;

import com.example.demo.model.mongo.Proveedor;
import com.example.demo.repos.mongo.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> obtenerPorId(String id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor guardarProveedor(Proveedor proveedor) {
        if(proveedorRepository.existsByNombre(proveedor.getNombre())){
            throw new RuntimeException("Proveedor_duplicado");
        }

        if(proveedorRepository.existsByNit(proveedor.getNit())){
            throw new RuntimeException("NIT_duplicado");
        }
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarProveedor(Proveedor proveedor) {
        if (proveedor.getId() == null || proveedor.getId().isEmpty()) {
            throw new IllegalArgumentException("El ID del proveedor es obligatorio");
        }

        Proveedor existente = proveedorRepository.findById(proveedor.getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        existente.setNombre(proveedor.getNombre());
        existente.setTelefono(proveedor.getTelefono());
        existente.setNit(proveedor.getNit());
        existente.setDireccion(proveedor.getDireccion());

        return proveedorRepository.save(existente);
    }

    public void eliminarProveedor(String id) {
        proveedorRepository.deleteById(id);
    }
}
