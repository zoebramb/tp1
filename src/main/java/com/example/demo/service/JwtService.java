package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.Rol;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracionMs;

    public JwtService(
        @Value("${app.jwt.secreto}") String secreto,
        @Value("${app.jwt.expiracion-ms}") long expiracionMs
    ) {
        this.key = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(Long usuarioId, Rol rol) {
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
            .subject(String.valueOf(usuarioId))
            .claim("rol", rol.name())
            .issuedAt(ahora)
            .expiration(fechaExpiracion)
            .signWith(key, Jwts.SIG.HS384)
            .compact();
    }

    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}

