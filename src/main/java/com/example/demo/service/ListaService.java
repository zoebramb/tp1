package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.FavoritoResponse;
import com.example.demo.dto.ListaRequest;
import com.example.demo.dto.MoverFavoritoRequest;
import com.example.demo.model.Lista;

public interface ListaService {
    Lista guardarLista(ListaRequest request);
    Lista guardarLista(Lista lista);
    List<Lista> buscarTodas();
    Lista buscarPorId(Long id);
    List<FavoritoResponse> favoritosDe(Long id);
    void eliminar(Long id);
    void moverFavoritos(Long origenId, MoverFavoritoRequest request);
}
