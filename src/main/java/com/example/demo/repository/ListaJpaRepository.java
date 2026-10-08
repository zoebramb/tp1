package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ListaEntity;

//Hereda de spring data para darnos los metodos que hacen consultas sql sin escribir codigo
@Repository 
public interface ListaJpaRepository extends JpaRepository<ListaEntity, Long> {
}
