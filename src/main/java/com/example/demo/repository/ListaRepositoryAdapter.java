package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo.model.Lista;
import com.example.demo.model.ListaEntity;
import com.example.demo.model.UsuarioEntity;

// Esta clase se encarga de implementar la interfaz ListaRepository y traduce los Lista (record) a ListaEntity (jpa)
@Component 
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = new ListaEntity();
        entity.setId(lista.id());
        entity.setNombre(lista.nombre());

        if (lista.usuarioId() != null) {
            UsuarioEntity usuarioEntity = new UsuarioEntity();
            usuarioEntity.setId(lista.usuarioId());
            entity.setUsuario(usuarioEntity);
        }

        ListaEntity guardada = jpaRepository.save(entity);
        return mapearADominio(guardada);
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return jpaRepository.findById(id).map(this::mapearADominio);
    }

    @Override
    public List<Lista> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .toList();
    }

    @Override
    public List<Lista> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearADominio)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Lista mapearADominio(ListaEntity entity) {
        Long usuarioId = entity.getUsuario() != null ? entity.getUsuario().getId() : null;
        return new Lista(entity.getId(), entity.getNombre(), usuarioId);
    }
}
