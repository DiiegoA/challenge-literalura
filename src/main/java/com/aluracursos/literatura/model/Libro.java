package com.aluracursos.literatura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private final String titulo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private final List<Autor> autores = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    @Enumerated(EnumType.STRING)
    private final List<Languages> idiomas = new ArrayList<>();

    private final int numeroDescargas;

    public Libro() {
        this.titulo = "Información no disponible";
        this.numeroDescargas = 0;
    }

    public Libro(DatosLibro datosLibro) {
        this.id = null;  // Se asignará automáticamente al persistir en la base de datos
        this.titulo = verificarValor(datosLibro.titulo());
        this.numeroDescargas = verificarEntero(datosLibro.numeroDescargas());
    }

    public Libro(Long id, String titulo, int numeroDescargas) {
        this.id = id;
        this.titulo = titulo;
        this.numeroDescargas = numeroDescargas;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Languages> getIdiomas() {
        return idiomas;
    }

    public int getNumeroDescargas() {
        return numeroDescargas;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void addAutor(final Autor autor) {
        if (!autores.contains(autor)) {
            autores.add(autor);
            if (!autor.getLibros().contains(this)) {
                autor.getLibros().add(this); // Sincronizar la relación bidireccional
            }
        }
    }

    private String verificarValor(final String valor) {
        return (valor == null || valor.isEmpty()) ? "Información no disponible" : valor;
    }

    private Integer verificarEntero(final Integer valor) {
        return valor != null ? valor : 0;
    }

    public String toCustomString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', descargas=" + numeroDescargas + "}";
    }
}