package com.example.demo.dto;

import com.example.demo.model.Rol;

//Devuelve los datos seguros del usuario registrado
public record UsuarioResponse(
    Long id,
    String email,
    Rol rol
) {}

