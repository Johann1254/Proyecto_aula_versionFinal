package com.example.demo.controller;

import com.example.demo.model.mysql.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 🔹 Obtener todos los usuarios
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    // 🔹 Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);
        return usuarioOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear usuario nuevo
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("usuario_duplicado"))
                return ResponseEntity.status(409).body("usuario_duplicado");

            if (e.getMessage().equals("correo_duplicado"))
                return ResponseEntity.status(409).body("correo_duplicado");

            return ResponseEntity.status(500).body("error");
        }
    }

    // 🔹 Actualizar usuario existente
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioService.actualizarUsuario(usuario, id);
    }

    // 🔹 Eliminar usuario
    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}
