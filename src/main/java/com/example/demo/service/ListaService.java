package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.MoverFavoritoRequest;
import com.example.demo.exception.ListaNoVaciaException;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;

//Conecta el controller con el repositorio de lista
@Service
public class ListaService {
private final ListaRepository listaRepository;
private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    public Lista guardarLista(Lista lista) {
        return listaRepository.save(lista);
    }

    public List<Lista> buscarTodas() {
        return listaRepository.findAll();
    }

    public Optional<Lista> buscarPorId(Long id) {
        return listaRepository.findById(id);
    }

    public void eliminar(Long id) {
        if (!favoritoRepository.findByListaId(id).isEmpty()) {
            throw new ListaNoVaciaException("La lista " + id + " todavía tiene favoritos, no se puede eliminar");
        }
        listaRepository.deleteById(id);
    }

    @Transactional
    public void moverFavoritos(Long origenId, MoverFavoritoRequest request) {
        // Validamos que la lista origen exista (404 si no)
        Lista origen = listaRepository.findById(origenId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Lista origen no encontrada"));

        // Validamos que la lista destino exista (404 si no)
        Lista destino = listaRepository.findById(request.listaDestinoId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Lista destino no encontrada"));

        // Buscamos todos los favoritos de la lista origen y los reasignamos al destino
        for (com.example.demo.model.Favorito f : favoritoRepository.findByListaId(origen.id())) {
            favoritoRepository.save(new com.example.demo.model.Favorito(
                    f.id(), f.productoId(), f.nota(), f.fechaAgregado(), destino.id()
            ));
        }

        // Eliminamos la lista origen
        listaRepository.deleteById(origen.id());
    }
}
