package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.service.FavoritoService;

import jakarta.validation.Valid;  //Le tengo que decir al controlador cuando quiero que verifique segun las anotaciones que tengo en el request DTO

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController 
@RequestMapping("/api/favoritos")
/* El trabajo del controlador es escuchar las peticiones de internet
    pasarle los datos al servicio y devolver las respuestas con el codigo http correcto
 */
public class FavoritoController 
{
    private final FavoritoService favoritoService;

    //declaro en el contructor el servicio que voy a utilizar
    public FavoritoController(FavoritoService favoritoService)
    {
        this.favoritoService = favoritoService;
    }

    //ahora los metodos http
    @PostMapping    //si no le pongo un path hago que cuando escuche la ruta gral y si es un petodo post viene x acá
    //ResponseEntity representa la respuesta HTTP completa y permite tener control total sobre lo que sale del servidor. Está compuesta por tres partes: body, status code, headers
    public ResponseEntity<FavoritoResponseDTO> crear(@Valid @RequestBody FavoritoRequestDTO requestDTO)
    {
        FavoritoResponseDTO creado = favoritoService.crearFavorito(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<FavoritoResponseDTO>> listarTodos()
    {
        List<FavoritoResponseDTO> lista = favoritoService.obtenerTodosLosFavoritos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")                             // uso path variable ya que el id viene en la url
    public ResponseEntity<FavoritoResponseDTO> obtener(@PathVariable Long id) 
    {       
        Optional<FavoritoResponseDTO> favorito = favoritoService.buscarPorID(id);

        if (favorito.isPresent()) {
            return ResponseEntity.ok(favorito.get()); // 200 OK con el DTO adentro
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<FavoritoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequestDTO request)
    {
        Optional<FavoritoResponseDTO> actualizado = favoritoService.actualizar(id, request);

        if(actualizado.isPresent())
        {
            return ResponseEntity.ok(actualizado.get());
        } else
            {
                return ResponseEntity.notFound().build();
            }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id)
    {
        favoritoService.eliminarPorId(id);
        return ResponseEntity.noContent().build(); //no content genera el codigo 204
    }
}
