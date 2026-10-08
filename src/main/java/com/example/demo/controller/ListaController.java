package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity; // Importamos para manejar los códigos de estado HTTP
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ListaRequest;
import com.example.demo.dto.MoverFavoritoRequest;
import com.example.demo.model.Lista;
import com.example.demo.service.ListaService;

@RestController
@RequestMapping("/listas")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @PostMapping
    public Lista crearLista(@RequestBody ListaRequest request) {
        // Armamos la Lista con ID null para que la BD lo genere, y le pasamos el nombre del request
        Lista nuevaLista = new Lista(null, request.nombre());
        return listaService.guardarLista(nuevaLista);
    }

    @GetMapping
    public List<Lista> obtenerTodas() {
        return listaService.buscarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lista> obtenerPorId(@PathVariable Long id) {
        return listaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Devuelve 404 si no existe
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLista(@PathVariable Long id) {
        listaService.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content al borrar con éxito
    }

    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @RequestBody MoverFavoritoRequest request) {
        
        listaService.moverFavoritos(origenId, request);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}