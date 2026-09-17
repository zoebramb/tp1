package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//El DTO de entrada, vamos a definir los tipos de datos que tiene que recibir las peticiones (id, alguna nota)
public class FavoritoRequestDTO {

    @NotNull(message = "El Id del producto es obligatorio")
    private Long productoId;

    @NotBlank (message = "La nota personal no puede estar vacía")
    private String notaPersonal;

    public FavoritoRequestDTO() {}
    
    public FavoritoRequestDTO(Long productoId, String notaPersonal) 
    {
        this.productoId = productoId;
        this.notaPersonal = notaPersonal;
    }
    
    //getters y setters
    
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNotaPersonal() {
        return notaPersonal;
    }

    public void setNotaPersonal(String notaPersonal) {
        this.notaPersonal = notaPersonal;
    }

}
