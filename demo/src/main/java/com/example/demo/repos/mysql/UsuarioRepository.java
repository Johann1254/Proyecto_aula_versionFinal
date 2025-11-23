package com.example.demo.repos.mysql;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.mysql.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String adminUsuario);

    boolean existsByCorreo(String correo);

    
}
