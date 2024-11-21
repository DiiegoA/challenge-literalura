package com.aluracursos.literatura.model;



import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private final String nombre;

    private final int anioNacimiento;
    private final int anioFallecimiento;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
    private final List<Libro> libros;

    // Constructor vacío con valores predeterminados
    public Autor() {
        this.nombre = "Información no disponible";
        this.anioNacimiento = 0;
        this.anioFallecimiento = 0;
        this.libros = new ArrayList<>();
    }

    // Constructor con DatosAutor
    public Autor(DatosAutor datosAutor) {
        this.nombre = verificarValor(datosAutor.nombre());
        this.anioNacimiento = verificarEntero(datosAutor.anioNacimiento());
        this.anioFallecimiento = verificarEntero(datosAutor.anioFallecimiento());
        this.libros = new ArrayList<>();
    }

    // Constructor con todos los campos
    public Autor(Long id, String nombre, int anioNacimiento, int anioFallecimiento, List<Libro> libros) {
        this.id = id;
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.anioFallecimiento = anioFallecimiento;
        this.libros = libros == null ? new ArrayList<>() : libros;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getAnioNacimiento() {
        return anioNacimiento;
    }

    public int getAnioFallecimiento() {
        return anioFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    // Método para agregar libros con sincronización bidireccional
    public void addLibro(Libro libro) {
        if (!libros.contains(libro)) {
            libros.add(libro);
            if (!libro.getAutores().contains(this)) {
                libro.getAutores().add(this);
            }
        }
    }

    // Métodos auxiliares para verificar valores
    private String verificarValor(String valor) {
        return (valor == null || valor.isEmpty()) ? "Información no disponible" : valor;
    }

    private int verificarEntero(Integer valor) {
        return valor != null ? valor : 0;
    }

    // Método personalizado para imprimir información del autor
    public String toCustomString() {
        return "Autor{id=" + id + ", nombre='" + nombre + "', nacimiento=" + anioNacimiento + ", fallecimiento=" + anioFallecimiento + "}";
    }
}