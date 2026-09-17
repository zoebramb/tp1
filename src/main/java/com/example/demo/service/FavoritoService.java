package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.model.Favorito;

/*El servicio va a conectar los DTO con el Repositorio
    Es en donde voy a tener mi lógica de negocio
*/
@Service 
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;

    /*Spring inyecta automáticamente el repositorio aca.
    Esto es inyección de dependencias, y es una de las cosas que hace Spring para que no
    tengamos que crear instancias de las clases manualmente.
    Se lo exige a Spring a través de los parámetros del constructor.
    */
    public FavoritoService(FavoritoRepository favoritoRepository) 
    {
        this.favoritoRepository = favoritoRepository;
    }

    //ahora los métodos CRUD

    /* Cuando el cliente pide crear un favorito, el Service desempaqueta el FavoritoRequestDTO, crea una 
    Entidad Favorito en blanco, le transfiere los datos permitidos (el producto y la nota) y le inyecta 
    la información del sistema (como el LocalDateTime.now()).
     */
    public FavoritoResposeDTO crearFavorito(FavoritoRequestDTO requestDTO) 
    {
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
