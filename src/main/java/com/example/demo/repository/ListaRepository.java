package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Lista;

public interface ListaRepository {
    Lista save(Lista lista);
    Optional<Lista> findById(Long id);
    List<Lista> findAll();
    void deleteById(Long id);
}
