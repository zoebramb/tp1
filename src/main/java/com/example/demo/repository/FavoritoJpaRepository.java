package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.FavoritoEntity;

//Spring data la implementa sola
@Repository 
public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
}