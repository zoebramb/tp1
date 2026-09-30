package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository  //Esto le dice a spring que esta clase va a manejar datos

public class InMemoryFavoritoRepository implements FavoritoRepository {
    // Se usa ConcurrentHashMap y AtomicLong para que no se corrompan los datos cuando Tomcat atienda requests en paralelo.
    private final Map<Long, Favorito> datos = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Favorito> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Favorito save(Favorito favorito) {
        if (favorito.id() == null) {
            Long newId = nextId.getAndIncrement();
            Favorito nuevo = new Favorito(newId, favorito.productoId(), favorito.nota(), favorito.fechaAgregado());
            datos.put(newId, nuevo);
            return nuevo;
        }
        datos.put(favorito.id(), favorito);
        return favorito;
    }

    @Override
    public void deleteById(Long id) {
        datos.remove(id);
    }
}