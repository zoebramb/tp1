package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo.model.Lista;
import com.example.demo.model.ListaEntity;

//esta clase se encarga de implementar la interfaz lisra repo y traduce los Lista (record) a lista entity(jpa)

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
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Lista mapearADominio(ListaEntity entity) {
        return new Lista(entity.getId(), entity.getNombre());
    }
}
