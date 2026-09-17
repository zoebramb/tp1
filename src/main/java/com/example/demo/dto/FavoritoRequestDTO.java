package com.example.demo.dto;

//El DTO de entrada, vamos a definir los tipos de datos que tiene que recibir las peticiones (id, alguna nota)
public class FavoritoRequestDTO {

    private Long productoId;
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
