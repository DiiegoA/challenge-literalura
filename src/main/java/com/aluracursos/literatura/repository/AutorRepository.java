package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Autor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    // Consulta para buscar un autor por su nombre (sin importar mayúsculas o minúsculas)
    @Query("SELECT a FROM Autor a WHERE LOWER(a.nombre) = LOWER(:nombre)")
    Optional<Autor> findByNombre(@Param("nombre") String nombre);
    @Query("SELECT DISTINCT a FROM Libro l JOIN l.autores a")
    List<Autor> findAllAutores(Pageable pageable);
    @Query("SELECT DISTINCT a FROM Libro l JOIN l.autores a WHERE a.anioNacimiento <= :anio AND (a.anioFallecimiento IS NULL OR a.anioFallecimiento > :anio)")
    List<Autor> findAutoresVivosEnAnio(@Param("anio") int anio, Pageable pageable);
    @Query("SELECT a FROM Autor a WHERE LOWER(a.nombre) LIKE LOWER(:nombre)")
    List<Autor> findByNombreParcial(@Param("nombre") String nombre);
    @Query("SELECT a FROM Autor a WHERE a.anioFallecimiento = :anio")
    List<Autor> findAutoresPorAnioFallecimiento(@Param("anio") int anio, Pageable pageable);

}
