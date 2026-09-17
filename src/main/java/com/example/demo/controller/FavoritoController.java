package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FavoritoRequest;
import com.example.demo.dto.FavoritoResponse;
import com.example.demo.service.FavoritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;  //Le tengo que decir al controlador cuando quiero que verifique segun las anotaciones que tengo en el request DTO

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



/* El trabajo del controlador es escuchar las peticiones de internet
    pasarle los datos al servicio y devolver las respuestas con el codigo http correcto
 */
@RestController 
@RequestMapping("/api/favoritos")
@Tag(name = "favoritos", description = "CRUD en memoria")
    
public class FavoritoController 
{
    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar favoritos")
    @GetMapping
    public ResponseEntity<List<FavoritoResponse>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Obtener un favorito por ID")
    @GetMapping("/{id}")
    public ResponseEntity<FavoritoResponse> obtenerUno(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un favorito")
    @PostMapping
    public ResponseEntity<FavoritoResponse> crear(@Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity
            .created(URI.create("/api/favoritos/" + creado.id()))
            .body(creado);
    }

    @Operation(summary = "Actualizar un favorito")
    @PutMapping("/{id}")
    public ResponseEntity<FavoritoResponse> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody FavoritoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un favorito")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}