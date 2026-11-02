package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FavoritoResponse;
import com.example.demo.dto.ListaRequest;
import com.example.demo.dto.MoverFavoritoRequest;
import com.example.demo.model.Lista;
import com.example.demo.service.ListaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping({"/api/listas", "/listas"})
@Tag(name = "Listas", description = "Endpoints para la gestión de listas de favoritos")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @Operation(summary = "Crear una nueva lista (asociada al usuario autenticado)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lista crearLista(@RequestBody ListaRequest request) {
        return listaService.guardarLista(request);
    }

    @Operation(summary = "Obtener listas del usuario autenticado (o todas si es admin)")
    @GetMapping
    public List<Lista> obtenerTodas() {
        return listaService.buscarTodas();
    }

    @Operation(summary = "Obtener una lista por id")
    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.buscarPorId(id));
    }

    @Operation(summary = "Obtener favoritos de una lista puntual")
    @GetMapping("/{id}/favoritos")
    public List<FavoritoResponse> obtenerFavoritosDeLista(@PathVariable Long id) {
        return listaService.favoritosDe(id);
    }

    @Operation(summary = "Eliminar una lista por id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLista(@PathVariable Long id) {
        listaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mover favoritos de una lista a otra")
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @RequestBody MoverFavoritoRequest request) {
        listaService.moverFavoritos(origenId, request);
        return ResponseEntity.noContent().build();
    }
}
