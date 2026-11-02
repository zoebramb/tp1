package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.config.ContextoSeguridad;
import com.example.demo.dto.ListaRequest;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;

@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private ContextoSeguridad contexto;

    @Captor
    private ArgumentCaptor<Lista> listaCaptor;

    private ListaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ListaServiceImpl(listaRepository, favoritoRepository, contexto);
    }

    @Test
    void crearLista_quedaAsociadaAlUsuarioAutenticado() {
        // Arrange: simulamos que el usuario logueado tiene ID 42
        Long usuarioId = 42L;
        when(contexto.usuarioActualId()).thenReturn(usuarioId);
        when(listaRepository.save(any(Lista.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: se intenta crear una lista
        service.guardarLista(new ListaRequest("Mi lista"));

        // Assert: verificamos que se haya guardado con el usuarioId del contexto
        verify(listaRepository).save(listaCaptor.capture());
        Lista guardada = listaCaptor.getValue();

        assertThat(guardada.usuarioId()).isEqualTo(usuarioId);
        assertThat(guardada.nombre()).isEqualTo("Mi lista");
    }
}

