package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.Lista;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ListaControllerIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long listaDeAnaId;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByEmail("ana@test.com")) {
            usuarioRepository.save(new Usuario(
                null, "ana@test.com", passwordEncoder.encode("password123"), Rol.USER
            ));
        }

        if (!usuarioRepository.existsByEmail("bob@test.com")) {
            usuarioRepository.save(new Usuario(
                null, "bob@test.com", passwordEncoder.encode("password123"), Rol.USER
            ));
        }

        if (!usuarioRepository.existsByEmail("admin@test.com")) {
            usuarioRepository.save(new Usuario(
                null, "admin@test.com", passwordEncoder.encode("password123"), Rol.ADMIN
            ));
        }

        Usuario ana = usuarioRepository.findByEmail("ana@test.com").orElseThrow();
        Lista listaAna = listaRepository.save(new Lista(null, "Lista de Ana", ana.id()));
        this.listaDeAnaId = listaAna.id();
    }

    private String loguear(String email, String password) throws Exception {
        LoginRequest req = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse resp = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            LoginResponse.class
        );
        return resp.token();
    }

    @Test
    void loginConCredencialesValidas_devuelve200ConToken() throws Exception {
        LoginRequest req = new LoginRequest("ana@test.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginConCredencialesInvalidas_devuelve401() throws Exception {
        LoginRequest req = new LoginRequest("ana@test.com", "claveIncorrecta");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accederAListaSinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accederAListaPropia_devuelve200() throws Exception {
        String tokenAna = loguear("ana@test.com", "password123");
        mockMvc.perform(get("/api/listas/{id}", listaDeAnaId)
                .header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listaDeAnaId))
                .andExpect(jsonPath("$.nombre").value("Lista de Ana"));
    }

    @Test
    void accederAListaAjena_devuelve403() throws Exception {
        String tokenBob = loguear("bob@test.com", "password123");
        mockMvc.perform(get("/api/listas/{id}", listaDeAnaId)
                .header("Authorization", "Bearer " + tokenBob))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccediendoAListaAjena_devuelve200() throws Exception {
        String tokenAdmin = loguear("admin@test.com", "password123");
        mockMvc.perform(get("/api/listas/{id}", listaDeAnaId)
                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listaDeAnaId));
    }
}

