package com.example.demo.dto;

import java.time.LocalDateTime;

//En el DTO de respuesta, va a definir que datos se van a devolver a la pantalla
//tener un dto de respuesta me va a servir si tengo algunos datos en mi base de datos que no quiero que se filtren en la respuesta, no los pongo acá y no se rompe nada.

public class FavoritoResponseDTO {

    private Long id;
    private Long productoId;
    private String notaPersonal;
    private LocalDateTime fechaCreacion;

    public FavoritoResponseDTO () {} 

    public FavoritoResponseDTO(Long id, Long productoId, String notaPersonal, LocalDateTime fechaCreacion) {
        this.id = id;
        this.productoId = productoId;
        this.notaPersonal = notaPersonal;
        this.fechaCreacion = fechaCreacion;
    }


    //getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
