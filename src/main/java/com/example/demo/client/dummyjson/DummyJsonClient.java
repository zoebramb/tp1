package com.example.demo.client.dummyjson;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

//Su trabajo es hacer la peticion GET y lanzar la excepcion si el producto no existe

@Component //para que spring lo guarde en su memoria y nos deje inyectarlo 
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient restclient)
    {
        //Spring inyecta el RestClient que tenemos configurado en RestClientConfig
        this.restClient = restclient;
    }

    public boolean existeProducto(Long productoId) {
        try {
            restClient.get()
                    .uri("https://dummyjson.com/products/{id}", productoId)
                    .retrieve()
                    .toBodilessEntity();
            return true; // Si devolvió 200 OK, existe
        } catch (HttpClientErrorException.NotFound e) {
            return false; // Si devolvió 404, no existe
        }
    }
}
