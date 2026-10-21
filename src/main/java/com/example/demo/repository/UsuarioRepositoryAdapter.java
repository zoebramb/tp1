package com.example.demo.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioEntity;

// Adaptador: implementa el puerto UsuarioRepository y traduce entre Usuario (dominio) y UsuarioEntity (JPA)
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.id());
        entity.setEmail(usuario.email());
        entity.setPasswordHash(usuario.passwordHash());
        entity.setRol(usuario.rol());

        UsuarioEntity guardado = jpaRepository.save(entity);
        return mapearADominio(guardado);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(this::mapearADominio);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::mapearADominio);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private Usuario mapearADominio(UsuarioEntity entity) {
        return new Usuario(
            entity.getId(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getRol()
        );
    }
}
