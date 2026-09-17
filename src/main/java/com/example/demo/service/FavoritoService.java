package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;

import com.example.demo.model.Favorito;

/*El servicio va a conectar los DTO con el Repositorio
    Es en donde voy a tener mi lógica de negocio
*/
@Service 
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final DummyJsonClient dummyJsonClient;

    /*Spring inyecta automáticamente el repositorio aca.
    Esto es inyección de dependencias, y es una de las cosas que hace Spring para que no
    tengamos que crear instancias de las clases manualmente.
    Se lo exige a Spring a través de los parámetros del constructor.
    */
    public FavoritoService(FavoritoRepository favoritoRepository, DummyJsonClient dummyJsonClient) 
    {
        this.favoritoRepository = favoritoRepository;
        this.dummyJsonClient = dummyJsonClient;
    }

    //ahora los métodos CRUD

    /* Cuando el cliente pide crear un favorito, el Service desempaqueta el FavoritoRequestDTO, crea una 
    Entidad Favorito en blanco, le transfiere los datos permitidos (el producto y la nota) y le inyecta 
    la información del sistema (como el LocalDateTime.now()).
     */
    public FavoritoResponseDTO crearFavorito(FavoritoRequestDTO requestDTO) 
    {
        boolean existe = dummyJsonClient.existeProducto(requestDTO.getProductoId());
        
        if (!existe) {
            throw new RecursoNoEncontradoException("El producto con ID " + requestDTO.getProductoId() + " no existe en DummyJSON");
        }

        Favorito nuevoFavorito = new Favorito();
        
        // Transferir datos del DTO a la entidad
        nuevoFavorito.setProductoId(requestDTO.getProductoId());
        nuevoFavorito.setNotaPersonal(requestDTO.getNotaPersonal());
        nuevoFavorito.setFechaCreacion(LocalDateTime.now());

        //guardar 
        Favorito favoritoGuardado = favoritoRepository.guardar(nuevoFavorito);

        //devolver la entidad traducida a DTO
        return mapearADto(favoritoGuardado);
    }

    public List<FavoritoResponseDTO> obtenerTodosLosFavoritos() 
    {
        List<Favorito> favoritos = favoritoRepository.buscarTodos();
        //usamos un Stream para traducir cada uno a DTO
        return favoritos.stream()
                        .map(this::mapearADto)
                        .toList();
    }

    public Optional<FavoritoResponseDTO> buscarPorID(Long id)
    {
        return favoritoRepository.buscarPorId(id).map(this::mapearADto);
    }

    public Optional<FavoritoResponseDTO> actualizar(Long id, FavoritoRequestDTO requestDTO)
    {

        boolean existe = dummyJsonClient.existeProducto(requestDTO.getProductoId());
        
        if (!existe) {
            throw new RecursoNoEncontradoException("El producto con ID " + requestDTO.getProductoId() + " no existe en DummyJSON");
        }

        //buscamos el favorito original en la base de datos
        Optional<Favorito> favoritoExistente = favoritoRepository.buscarPorId(id);

        if(favoritoExistente.isPresent())
        {
            Favorito favorito = favoritoExistente.get();
            //actualizamos los datos para guardarlo
            favorito.setProductoId(requestDTO.getProductoId());
            favorito.setNotaPersonal(requestDTO.getNotaPersonal());

            favoritoRepository.guardar(favorito);

            //lo tengo que devolver en DTO
            return Optional.of(mapearADto(favorito));
        }
        
        return Optional.empty();
    }

    public void eliminarPorId(Long id)
    {
        favoritoRepository.eliminarPorId(id);
    }


    // --- MÉTODO AYUDANTE (PRIVADO) ---
    // Lo usamos para no repetir código de mapeo en todos lados
    private FavoritoResponseDTO mapearADto(Favorito favorito) {
        FavoritoResponseDTO dto = new FavoritoResponseDTO();
        dto.setId(favorito.getId());
        dto.setProductoId(favorito.getProductoId());
        dto.setNotaPersonal(favorito.getNotaPersonal());
        dto.setFechaCreacion(favorito.getFechaCreacion());
        return dto;
    }
}

/*
Traducimos a Entidad porque el cliente manda información incompleta y necesitamos agregarle datos (como el ID y la fecha) antes de guardarlo.
Traducmos a DTO a la vuelta para no exponer el modelo de base de datos crudo a internet; 
entregamos un formato limpio, controlado y diseñado específicamente para la pantalla del usuario. */