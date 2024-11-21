package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Languages;
import com.aluracursos.literatura.model.Libro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) = LOWER(:tituloLibro)")
    Optional<Libro> findByTituloLibro(@Param("tituloLibro") String tituloLibro);
    @Query("SELECT DISTINCT l FROM Libro l JOIN FETCH l.autores")
    List<Libro> findAllLibrosConAutores(Pageable pageable);
    @Query("SELECT DISTINCT l FROM Libro l JOIN FETCH l.autores a WHERE :idioma MEMBER OF l.idiomas")
    List<Libro> findLibrosPorIdioma(@Param("idioma") Languages idioma, Pageable pageable);
    @Query("SELECT l FROM Libro l ORDER BY l.numeroDescargas DESC")
    List<Libro> findTop10Libros(Pageable pageable);
}
