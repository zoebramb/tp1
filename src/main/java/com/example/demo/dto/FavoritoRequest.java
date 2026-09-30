package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//El DTO de entrada, vamos a definir los tipos de datos que tiene que recibir las peticiones (id, alguna nota)

public record FavoritoRequest(
    @NotNull(message = "productoId es obligatorio")
    Long productoId,
    @NotBlank(message = "nota no puede estar vacía")
    String nota
) {}