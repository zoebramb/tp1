package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.model.Usuario;

// Puerto del dominio: define QUÉ operaciones de persistencia necesitamos para Usuario
public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
