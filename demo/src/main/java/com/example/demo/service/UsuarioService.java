package com.example.demo.service;

import com.example.demo.model.mysql.Usuario;
import com.example.demo.repos.mysql.UsuarioRepository;

// import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Guardar nuevo
    public Usuario guardarUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new RuntimeException("usuario_duplicado");
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("correo_duplicado");
        }
        return usuarioRepository.save(usuario);
    }

    // Actualizar existente
    public Usuario actualizarUsuario(Usuario usuario, Long id) {

        Usuario existente = usuarioRepository.findById(usuario.getId()).orElseThrow();

        if (usuario.getId() == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }

        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
            usuario.setContrasena(existente.getContrasena());
        } else {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }

        if (!existente.getUsuario().equals(usuario.getUsuario()) &&
                usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new RuntimeException("usuario_duplicado");
        }

        if (!existente.getCorreo().equals(usuario.getCorreo()) &&
                usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("correo_duplicado");
        }

        existente.setUsuario(usuario.getUsuario());
        existente.setCorreo(usuario.getCorreo());
        existente.setContrasena(usuario.getContrasena());
        existente.setRol(usuario.getRol());
        existente.setActivo(usuario.isActivo());
        return usuarioRepository.save(usuario);

    }

    // Obtener por ID
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Eliminar
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // @PostConstruct
    // public void init() {

    // crearUsuarioSiNoExiste(
    // "admin",
    // "123",
    // "admin@correo.com",
    // "ADMIN",
    // true
    // );

    // crearUsuarioSiNoExiste(
    // "usuario",
    // "usuario123",
    // "user@correo.com",
    // "USER",
    // true
    // );
    // }

    // private void crearUsuarioSiNoExiste(String user, String pass, String correo,
    // String rol, boolean activo) {

    // usuarioRepository.findByUsuario(user).ifPresentOrElse(
    // u -> {
    // System.out.println("El usuario '" + user + "' ya existe.");
    // },
    // () -> {
    // Usuario nuevo = new Usuario();
    // nuevo.setUsuario(user);
    // nuevo.setContrasena(passwordEncoder.encode(pass));
    // nuevo.setCorreo(correo);
    // nuevo.setRol(rol);
    // nuevo.setActivo(activo);

    // usuarioRepository.save(nuevo);
    // System.out.println("Usuario creado: " + user);
    // }
    // );
    // }

}
