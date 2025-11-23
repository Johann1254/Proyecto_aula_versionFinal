package com.example.demo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.demo.model.mysql.Usuario;
import com.example.demo.repos.mysql.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Asegurar que el rol tenga el prefijo "ROLE_"
        String rol = usuario.getRol().startsWith("ROLE_") ? usuario.getRol() : "ROLE_" + usuario.getRol();

         if (!usuario.isActivo()) {
            throw new DisabledException("El usuario está inactivo. Contacta al administrador.");
        }

        // Devuelve el usuario sin validación de activo/inactivo
        return new User(
                usuario.getUsuario(),
                usuario.getContrasena(),
                Collections.singleton(new SimpleGrantedAuthority(rol))
        );
    }

    
}
