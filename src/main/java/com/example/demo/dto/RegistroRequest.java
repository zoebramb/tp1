package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato de email es inválido")
    String email,

    @NotBlank(message = "La contraseña no puede estar vacía")
    String password
) {}

