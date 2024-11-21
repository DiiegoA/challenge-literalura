package com.aluracursos.literatura.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ConsumoApi {

    public String obtenerDatos(String url) {
        // Configurar el cliente HTTP para seguir redirecciones
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL) // Habilitar redirecciones normales
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar si la respuesta es válida
            if (response.statusCode() != 200) {
                System.out.println("Error en la respuesta de la API. Código de estado: " + response.statusCode());
                return null;
            }

            return response.body();
        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.out.println("La solicitud fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restablecer el estado de interrupción
            return null;
        }
    }
}