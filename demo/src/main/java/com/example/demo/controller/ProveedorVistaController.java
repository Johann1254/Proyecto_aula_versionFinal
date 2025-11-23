package com.example.demo.controller;

import com.example.demo.model.mongo.Proveedor;
import com.example.demo.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ProveedorVistaController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping("/proveedores")
    public String listarProveedores() {
        return "proveedores/proveedores";
    }

    @GetMapping("/proveedores/nuevo")
    public String nuevoProveedor() {
        return "proveedores/agregar-proveedor";
    }

    @GetMapping("/proveedores/editar")
    public String editarProveedor(@RequestParam("id") String id, Model model) {
        Optional<Proveedor> proveedorOpt = proveedorService.obtenerPorId(id);
        if (proveedorOpt.isPresent()) {
            model.addAttribute("proveedor", proveedorOpt.get());
            return "proveedores/editar-proveedor";
        } else {
            model.addAttribute("errorMensaje", "Proveedor no encontrado");
            return "error";
        }
    }
}
