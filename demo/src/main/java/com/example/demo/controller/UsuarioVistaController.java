package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.mysql.Usuario;
import com.example.demo.service.UsuarioService;

@Controller
public class UsuarioVistaController {

    @Autowired
    private UsuarioService usuarioService;

    // 🔹 Vista principal del listado de usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios() {
        return "usuarios/usuarios"; // templates/usuarios/usuarios.html
    }

    // 🔹 Vista para registrar un nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario() {
        return "usuarios/agregar-usuario"; // templates/usuarios/agregar-usuario.html
    }

    // 🔹 Vista para editar un usuario existente
    @GetMapping("/usuarios/editar")
    public String editarUsuarioPorParam(@RequestParam("id") Long id, Model model) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);

            if (!usuarioOpt.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }

            model.addAttribute("usuario", usuarioOpt.get());
            return "usuarios/editar-usuario"; // la vista HTML
        } catch (Exception e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMensaje", "No se pudo cargar el usuario: " + e.getMessage());
            return "error"; // vista de error genérica
        }
    }

    
}
