package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegistroRequest;
import com.example.demo.dto.UsuarioResponse;
import com.example.demo.exception.CredencialesInvalidasException;
import com.example.demo.exception.EmailDuplicadoException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailDuplicadoException("Ya existe una cuenta con el email " + request.email());
        }

        Usuario nuevo = new Usuario(
            null,
            request.email(),
            passwordEncoder.encode(request.password()), // hash, nunca texto plano
            Rol.USER
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        return aResponse(guardado);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.passwordHash())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        String token = jwtService.generarToken(usuario.id(), usuario.rol());
        return new LoginResponse(token);
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.id(), usuario.email(), usuario.rol());
    }
}

