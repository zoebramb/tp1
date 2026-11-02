package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo.model.Favorito;
import com.example.demo.model.FavoritoEntity;
import com.example.demo.model.ListaEntity;

@Component
public class FavoritoRepositoryAdapter implements FavoritoRepository{

    //Inyectamos la interfaz de spring data  (favorito jpa repo)
    private final FavoritoJpaRepository jpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository)
    {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Favorito save(Favorito favorito)
    {
        //convertimos el record (dominio) a la entidad jpa
        FavoritoEntity entity = new FavoritoEntity();
        entity.setId(favorito.id()); //si es nuevo va a ser null, postgress le va a asignar uno
        entity.setProductoId(favorito.productoId());
        entity.setNota(favorito.nota());
        entity.setFechaAgregado(favorito.fechaAgregado());

        if(favorito.listaId() != null)
        {
            ListaEntity listaEntity = new ListaEntity();
            listaEntity.setId(favorito.listaId());
            entity.setLista(listaEntity);
        }

        //guardamos en la base de datos, hace el insert o update automatico
        FavoritoEntity guardado = jpaRepository.save(entity);

        //convertimos la entidad guardada de vuelta al record del dominio
        return mapearADominio(guardado);
    }

    @Override
    public Optional<Favorito> findById(Long id)
    {
        //findById devuelve un optional<FavoritoEntity> usamos map para transformarlo al record
        return jpaRepository.findById(id).map(this::mapearADominio);
    }

    @Override
    public List<Favorito> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Favorito> findByListaId(Long listaId) {
        return jpaRepository.findByListaId(listaId).stream()
                .map(this::mapearADominio)
                .toList();
    }

    // Método auxiliar privado para no repetir la conversión de Entidad a Record
    private Favorito mapearADominio(FavoritoEntity entity) {
        return new Favorito(
                entity.getId(),
                entity.getProductoId(),
                entity.getNota(),
                entity.getFechaAgregado(),
                entity.getLista() != null ? entity.getLista().getId() : null
        );
    }
}
