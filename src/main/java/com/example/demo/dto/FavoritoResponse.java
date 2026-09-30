package com.example.demo.dto;

import java.time.LocalDateTime;

//En el DTO de respuesta, va a definir que datos se van a devolver a la pantalla
//tener un dto de respuesta me va a servir si tengo algunos datos en mi base de datos que no quiero que se filtren en la respuesta, no los pongo acá y no se rompe nada.

public record FavoritoResponse(
    Long id, 
    Long productoId,
    String nota, 
    LocalDateTime fechaAgregado
) {}