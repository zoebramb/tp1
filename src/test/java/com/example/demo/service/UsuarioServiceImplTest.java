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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.RegistroRequest;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    //captor atrapa el objeto exacto que se le envia a usuarioRepositorio.save
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;
    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UsuarioServiceImpl(repository, passwordEncoder, jwtService);
    }

    @Test
    void registrar_nuncaGuardaPasswordEnTextoPlano() {
        // Arrange: configuramos los mocks
        when(repository.existsByEmail("ana@test.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("$2a$10$hashedPasswordSeguro");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: llamamos al método registrar
        service.registrar(new RegistroRequest("ana@test.com", "12345678"));

        // Assert: capturamos el usuario enviado al repositorio
        verify(repository).save(usuarioCaptor.capture());
        Usuario guardado = usuarioCaptor.getValue();

        // Verificamos que la contraseña guardada NO sea la original en texto plano
        assertThat(guardado.passwordHash()).isNotEqualTo("12345678");
        assertThat(guardado.passwordHash()).isEqualTo("$2a$10$hashedPasswordSeguro");
        assertThat(guardado.email()).isEqualTo("ana@test.com");
        assertThat(guardado.rol()).isEqualTo(Rol.USER);
    }
}

