package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository  //Esto le dice a spring que esta clase va a manejar datos
public class FavoritoRepositoryImpl implements FavoritoRepository 
{
    //usamos una lista para la persistencia
    private final List<Favorito> baseDeDatos = new ArrayList<>();

    //simulamos el id
    private Long contadorId = 1L;

    //ahora @Override los métodos de la interfaz
    @Override 
    public Favorito guardar(Favorito favorito)
    {
        if(favorito.getId() == null)
        {
            favorito.setId(contadorId++);
            baseDeDatos.add(favorito);
        }
        else 
        { 
            //si ya existe el id, buscar y reemplazar el favorito existente
            for (int i = 0; i < baseDeDatos.size(); i++) {
                if (baseDeDatos.get(i).getId().equals(favorito.getId())) {
                    baseDeDatos.set(i, favorito);
                    break;
                }
            }
        }
        return favorito;
    }

    @Override
    public List<Favorito> buscarTodos()
    {
        //devuelve todos los favoritos
        return baseDeDatos;
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) 
    {
        for (Favorito favorito : baseDeDatos) {
            if (favorito.getId().equals(id)) {
                // lo metemos en la caja (Optional)
                return Optional.of(favorito); 
            }
        }
        return Optional.empty(); 
    }

    @Override
    public void eliminarPorId(Long id) 
    {
        baseDeDatos.removeIf(favorito -> favorito.getId().equals(id));
    }
}
