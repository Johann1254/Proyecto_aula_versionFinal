package com.example.demo.controller;

import com.example.demo.model.mongo.Proveedor;
import com.example.demo.repos.mongo.ProveedorRepository;
import com.example.demo.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorApiController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public Map<String, Object> listarProveedores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Proveedor> pagina = proveedorRepository.findAll(pageable);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("data", pagina.getContent());
        respuesta.put("recordsTotal", pagina.getTotalElements());
        respuesta.put("recordsFiltered", pagina.getTotalElements()); // sin filtros por ahora

        return respuesta;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerProveedor(@PathVariable String id) {
        Optional<Proveedor> proveedorOpt = proveedorService.obtenerPorId(id);
        return proveedorOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearProveedor(@RequestBody Proveedor proveedor) {
        try {
            return ResponseEntity.ok(proveedorService.guardarProveedor(proveedor));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Proveedor_duplicado"))
                return ResponseEntity.status(409).body("Proveedor_duplicado");
            if(e.getMessage().equals("NIT_duplicado"))
                return ResponseEntity.status(409).body("NIT_duplicado");
            return ResponseEntity.status(500).body("error");
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable String id, @RequestBody Proveedor proveedor) {
        proveedor.setId(id);
        Proveedor actualizado = proveedorService.actualizarProveedor(proveedor);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable String id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
