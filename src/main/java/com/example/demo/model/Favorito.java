package com.example.demo.model;

import java.time.LocalDateTime;

public class Favorito {

    private Long id;
    private Long productoId;
    private String notaPersonal;
    private LocalDateTime fechaCreacion;

    //constructor sin parámetros, es obligatorio para que Spring pueda crear instancias de la clase Favorito
    public Favorito () {} 

    //constructor con parámetros para crear instancias de la clase Favorito con valores iniciales
    public Favorito(Long id, Long productoId, String notaPersonal, LocalDateTime fechaCreacion) {
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
