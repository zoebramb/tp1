package com.example.demo.repository;

import com.example.demo.model.Favorito;
import java.util.List;
import java.util.Optional;

//en esta interfaz vamos a nombrar QUÉ puede hacer el repositorio pero no el CÓMO
//los métodos que va a poder hacer son el CRUD
public interface FavoritoRepository 
{
    //create lo vamos a usar tambien para actualizar
    Favorito guardar(Favorito favorito);
    //read (todos y por id)
    List<Favorito> buscarTodos();
    Optional<Favorito> buscarPorId(Long id);
    //delete
    void eliminarPorId(Long id);
}
