package com.example.demo.model;

import jakarta.persistence.*;

@Entity 
@Table(name = "listas")
public class ListaEntity 
{
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    /*fetch lazy "cuando cargues una lista, no vayas a buscar a la base
     todos los datos del usuario a menos que te lo pida con getUsuario()".
      Es una buena práctica de rendimiento.
    @JoinColumn(name = "usuario_id"): Le indica a JPA el nombre exacto de la columna 
    en la tabla listas de Postgres que almacena el id del usuario (la clave foránea / Foreign Key). */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "usuario_id")
    private UsuarioEntity usuario;

    public ListaEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }    

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
}