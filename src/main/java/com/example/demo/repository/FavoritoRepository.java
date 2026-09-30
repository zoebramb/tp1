package com.example.demo.repository;

import com.example.demo.model.Favorito;
import java.util.List;
import java.util.Optional;

//en esta interfaz vamos a nombrar QUÉ puede hacer el repositorio pero no el CÓMO
//los métodos que va a poder hacer son el CRUD

public interface FavoritoRepository {
    List<Favorito> findAll();
    Optional<Favorito> findById(Long id);
    Favorito save(Favorito favorito);
    void deleteById(Long id);
}
