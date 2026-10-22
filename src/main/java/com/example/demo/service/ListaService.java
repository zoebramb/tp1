package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.ContextoSeguridad;
import com.example.demo.dto.FavoritoResponse;
import com.example.demo.dto.ListaRequest;
import com.example.demo.dto.MoverFavoritoRequest;
import com.example.demo.exception.AccesoDenegadoException;
import com.example.demo.exception.ListaNoVaciaException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Favorito;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;

// Conecta el controller con el repositorio de lista aplicando reglas de negocio y autorización
@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;
    private final ContextoSeguridad contexto;

    public ListaService(
        ListaRepository listaRepository,
        FavoritoRepository favoritoRepository,
        ContextoSeguridad contexto
    ) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
        this.contexto = contexto;
    }

    // Único punto de control: valida existencia (404) y propiedad/rol (403)
    private Lista buscarPropiaOFallar(Long id) {
        Lista lista = listaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe la lista con id " + id));

        boolean esDueno = lista.usuarioId() != null
            && lista.usuarioId().equals(contexto.usuarioActualId());

        if (!esDueno && !contexto.esAdmin()) {
            throw new AccesoDenegadoException("La lista " + id + " no te pertenece");
        }

        return lista;
    }

    public Lista guardarLista(ListaRequest request) {
        Long usuarioActualId = contexto.usuarioActualId();
        Lista nuevaLista = new Lista(null, request.nombre(), usuarioActualId);
        return listaRepository.save(nuevaLista);
    }

    public Lista guardarLista(Lista lista) {
        Long usuarioActualId = contexto.usuarioActualId();
        Lista nuevaLista = new Lista(lista.id(), lista.nombre(), usuarioActualId);
        return listaRepository.save(nuevaLista);
    }

    public List<Lista> buscarTodas() {
        if (contexto.esAdmin()) {
            return listaRepository.findAll();
        }
        Long usuarioId = contexto.usuarioActualId();
        return usuarioId != null ? listaRepository.findByUsuarioId(usuarioId) : List.of();
    }

    public Lista buscarPorId(Long id) {
        return buscarPropiaOFallar(id);
    }

    public List<FavoritoResponse> favoritosDe(Long id) {
        Lista lista = buscarPropiaOFallar(id);
        return favoritoRepository.findByListaId(lista.id()).stream()
            .map(f -> new FavoritoResponse(f.id(), f.productoId(), f.nota(), f.fechaAgregado()))
            .toList();
    }

    public void eliminar(Long id) {
        Lista lista = buscarPropiaOFallar(id);
        if (!favoritoRepository.findByListaId(lista.id()).isEmpty()) {
            throw new ListaNoVaciaException("La lista " + id + " todavía tiene favoritos, no se puede eliminar");
        }
        listaRepository.deleteById(lista.id());
    }

    @Transactional
    public void moverFavoritos(Long origenId, MoverFavoritoRequest request) {
        // Validamos que ambas listas existan y pertenezcan al usuario autenticado (o sea admin)
        Lista origen = buscarPropiaOFallar(origenId);
        Lista destino = buscarPropiaOFallar(request.listaDestinoId());

        // Buscamos todos los favoritos de la lista origen y los reasignamos al destino
        for (Favorito f : favoritoRepository.findByListaId(origen.id())) {
            favoritoRepository.save(new Favorito(
                f.id(), f.productoId(), f.nota(), f.fechaAgregado(), destino.id()
            ));
        }

        // Eliminamos la lista origen
        listaRepository.deleteById(origen.id());
    }
}
