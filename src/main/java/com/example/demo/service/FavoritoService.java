package com.example.demo.service;

import com.example.demo.dto.FavoritoRequest;
import com.example.demo.dto.FavoritoResponse;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*El servicio va a conectar los DTO con el Repositorio
    Es en donde voy a tener mi lógica de negocio
*/
@Service
public class FavoritoService {
    private final FavoritoRepository repository;

    /*Spring inyecta automáticamente el repositorio aca.
    Esto es inyección de dependencias, y es una de las cosas que hace Spring para que no
    tengamos que crear instancias de las clases manualmente.
    Se lo exige a Spring a través de los parámetros del constructor.
    */

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    private Favorito buscar0Fallar(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe el favorito con id " + id));
    }

    //ahora los métodos CRUD

    /* Cuando el cliente pide crear un favorito, el Service desempaqueta el FavoritoRequestDTO, crea una 
    Entidad Favorito en blanco, le transfiere los datos permitidos (el producto y la nota) y le inyecta 
    la información del sistema (como el LocalDateTime.now()).
     */
private FavoritoResponse aResponse(Favorito f) {
        return new FavoritoResponse(f.id(), f.productoId(), f.nota(), f.fechaAgregado());
    }

    public FavoritoResponse crear(FavoritoRequest request) {
        Favorito nuevo = new Favorito(null, request.productoId(), request.nota(), LocalDateTime.now());
        return aResponse(repository.save(nuevo));
    }

    public List<FavoritoResponse> obtenerTodos() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    public FavoritoResponse obtenerPorId(Long id) {
        return aResponse(buscar0Fallar(id));
    }

    public FavoritoResponse actualizar(Long id, FavoritoRequest request) {
        Favorito existente = buscar0Fallar(id);
        Favorito actualizado = new Favorito(
            existente.id(),
            request.productoId(),
            request.nota(),
            existente.fechaAgregado() // no se pisa
        );
        return aResponse(repository.save(actualizado));
    }

    public void eliminar(Long id) {
        buscar0Fallar(id); // Evalúa si existe antes de borrar
        repository.deleteById(id);
    }
}


/*
Traducimos a Entidad porque el cliente manda información incompleta y necesitamos agregarle datos (como el ID y la fecha) antes de guardarlo.
Traducmos a DTO a la vuelta para no exponer el modelo de base de datos crudo a internet; 
entregamos un formato limpio, controlado y diseñado específicamente para la pantalla del usuario. */