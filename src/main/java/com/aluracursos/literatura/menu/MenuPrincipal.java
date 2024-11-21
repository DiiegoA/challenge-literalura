package com.aluracursos.literatura.menu;

import com.aluracursos.literatura.model.Autor;
import com.aluracursos.literatura.model.DatosLibro;
import com.aluracursos.literatura.model.Languages;
import com.aluracursos.literatura.model.Libro;
import com.aluracursos.literatura.repository.AutorRepository;
import com.aluracursos.literatura.repository.LibroRepository;
import com.aluracursos.literatura.service.ConsumoApi;
import com.aluracursos.literatura.service.ConvierteDatos;
import com.aluracursos.logger.loggerbase.LoggerBase;
import com.aluracursos.logger.loggerbase.LoggerBaseImpl;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

// Clase principal que gestiona el menú de la aplicación
public class MenuPrincipal {

    // URL base para la búsqueda de libros en la API
    private static final String URL_BASE_1 = "https://gutendex.com/books?search=";
    // Tamaño de la página para la paginación
    private static final int PAGE_SIZE = 5;

    // Scanner para la entrada del usuario
    private final Scanner scanner = new Scanner(System.in);
    // Servicio de consumo de API
    private final ConsumoApi consumoApi;
    // Servicio de conversión de datos
    private final ConvierteDatos conversor;
    // Repositorio de libros
    private final LibroRepository libroRepository;
    // Repositorio de autores
    private final AutorRepository autorRepository;
    // Logger para el registro de información
    private final LoggerBase logger;

    // Constructor que inicializa las dependencias
    public MenuPrincipal(LibroRepository libroRepository, AutorRepository autorRepository, ConsumoApi consumoApi, ConvierteDatos conversor) {
        this.libroRepository = libroRepository; // Inicializa el repositorio de libros
        this.autorRepository = autorRepository; // Inicializa el repositorio de autores
        this.consumoApi = consumoApi; // Inicializa el servicio de consumo de API
        this.conversor = conversor; // Inicializa el servicio de conversión de datos
        this.logger = new LoggerBaseImpl(MenuPrincipal.class.getName()); // Inicializa el logger
    }

    // Método que muestra el menú y maneja la interacción del usuario
    public void muestraElMenu() {
        int opcion = -1; // Variable para almacenar la opción del usuario
        do {
            mostrarOpciones(); // Mostrar las opciones del menú
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim()); // Leer y convertir la entrada del usuario a un número entero
                procesarOpcion(opcion); // Procesar la opción ingresada
            } catch (NumberFormatException e) { // Capturar excepción si el usuario ingresa algo que no es un número
                logger.logInfo("Opción inválida. Por favor, ingresa un número válido."); // Mostrar mensaje de error
            }
        } while (opcion != 0); // Repetir hasta que el usuario decida salir (opción 0)
    }

    // Método que muestra las opciones del menú
    private void mostrarOpciones() {
        logger.logInfo(String.format("""
                %nEscribe una opción:
                1 - Buscar Libro
                2 - Listar Libros Registrados
                3 - Listar Autores Registrados
                4 - Listar Autores Vivos en un Determinado Año
                5 - Listar Libros Por Idioma
                6 - Imprimir Estadísticas de los Libros
                7 - Buscar Top 10 de los Libros más Descargados
                8 - Buscar Autor en la Base de Datos
                9 - Listar Autores Muertos de un Año Específico
                0 - Salir
            """)); // Mostrar las opciones disponibles al usuario
    }

    // Método que procesa la opción seleccionada por el usuario
    private void procesarOpcion(int opcion) {
        switch (opcion) { // Evaluar la opción ingresada
            case 1 -> buscarLibro(); // Opción 1: Buscar libro
            case 2 -> listarLibrosRegistrados(); // Opción 2: Listar libros registrados
            case 3 -> listarAutoresRegistrados(); // Opción 3: Listar autores registrados
            case 4 -> listarAutoresVivosAnio(); // Opción 4: Listar autores vivos en un año específico
            case 5 -> listarLibrosPorIdioma(); // Opción 5: Listar libros por idioma
            case 6 -> mostrarEstadisticas(); // Opción 6: Mostrar estadísticas de libros
            case 7 -> buscarTop10Libros(); // Opción 7: Buscar los 10 libros más descargados
            case 8 -> buscarAutor(); // Opción 8: Buscar autor en la base de datos
            case 9 -> listarAutoresMuertosAnio(); // Opción 9: Listar autores fallecidos en un año específico
            case 0 -> logger.logInfo("Cerrando la aplicación..."); // Opción 0: Salir
            default -> logger.logInfo("Opción inválida. Por favor, ingresa un número válido."); // Cualquier otra opción es inválida
        }
    }

    // Método para obtener los datos de un libro a partir del título ingresado
    private DatosLibro getDatosLibro(final String tituloLibro) {
        final String nombreLibro = tituloLibro.replace(" ", "+"); // Reemplazar espacios con '+' para formar la URL
        final String url = URL_BASE_1 + nombreLibro; // Construir la URL completa para la búsqueda

        final String json = consumoApi.obtenerDatos(url); // Consumir la API para obtener los datos en formato JSON
        if (json == null || json.isEmpty()) { // Verificar si la respuesta es nula o vacía
            logger.logInfo("No se recibió respuesta desde la API."); // Registrar que no se recibió respuesta
            return null; // Retornar null si no hay datos
        }

        try {
            final JsonNode rootNode = conversor.getObjectMapper().readTree(json); // Parsear el JSON a un nodo de árbol
            final JsonNode resultsArray = rootNode.path("results"); // Obtener el nodo 'results'

            if (resultsArray.isArray() && resultsArray.size() > 0) { // Verificar si el nodo 'results' es un array y tiene elementos
                return conversor.getObjectMapper().treeToValue(resultsArray.get(0), DatosLibro.class); // Convertir el primer elemento a un objeto DatosLibro
            }
        } catch (Exception e) { // Capturar cualquier excepción durante el proceso
            logger.logInfo("Error al procesar los datos de la API: " + e.getMessage()); // Registrar el error
        }
        return null; // Retornar null si no se pudo procesar la información
    }

    // Método para buscar un libro e intentar registrarlo si no está presente
    @Transactional
    private void buscarLibro() {
        logger.logInfo("Por favor escribe el nombre del libro que deseas buscar:"); // Solicitar al usuario el nombre del libro
        final String tituloLibro = scanner.nextLine().trim(); // Leer y limpiar la entrada del usuario

        if (tituloLibro.isEmpty()) { // Verificar si no se ingresó un título
            logger.logInfo("No ingresaste ningún título. Por favor intenta de nuevo."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        libroRepository.findByTituloLibro(tituloLibro).ifPresentOrElse( // Buscar si el libro ya está registrado
                libro -> logger.logInfo("El libro ya está registrado: " + libro.toCustomString()), // Si el libro existe, mostrar un mensaje
                () -> registrarNuevoLibro(tituloLibro) // Si el libro no existe, registrar uno nuevo
        );
    }

    // Método para registrar un nuevo libro en el repositorio
    private void registrarNuevoLibro(String tituloLibro) {
        final DatosLibro datosLibro = getDatosLibro(tituloLibro); // Obtener los datos del libro desde la API

        if (datosLibro == null) { // Verificar si no se encontraron datos
            logger.logInfo("No se encontraron resultados para el libro."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        if (datosLibro.autoresList() == null || datosLibro.autoresList().isEmpty()) { // Verificar si no hay información de autores
            logger.logInfo("El libro no contiene información de autores en la API."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        Libro libro = new Libro(datosLibro); // Crear un nuevo objeto Libro con los datos obtenidos
        asignarIdiomas(datosLibro, libro); // Asignar los idiomas al libro
        procesarAutores(datosLibro, libro); // Procesar y asignar los autores al libro
        libroRepository.save(libro); // Guardar el libro en el repositorio
        logger.logInfo("Libro guardado con éxito: " + libro.toCustomString()); // Mostrar mensaje de éxito
    }

    // Método para asignar los idiomas a un libro
    private void asignarIdiomas(DatosLibro datosLibro, Libro libro) {
        datosLibro.idiomas().stream() // Obtener la lista de idiomas y crear un stream
                .map(idioma -> { // Mapear cada idioma a un objeto del enum Languages
                    try {
                        return Languages.fromInput(idioma); // Intentar convertir el idioma a un enum Languages
                    } catch (IllegalArgumentException e) { // Capturar excepción si el idioma no es válido
                        logger.logInfo("Idioma no reconocido: " + idioma); // Registrar el idioma no reconocido
                        return null; // Retornar null si no se reconoce el idioma
                    }
                })
                .filter(Objects::nonNull) // Filtrar los valores nulos
                .forEach(libro.getIdiomas()::add); // Añadir los idiomas válidos a la lista de idiomas del libro
    }

    // Método para procesar y asignar los autores a un libro
    private void procesarAutores(DatosLibro datosLibro, Libro libro) {
        datosLibro.autoresList().forEach(datosAutor -> { // Iterar sobre la lista de autores
            Autor autor = autorRepository.findByNombre(datosAutor.nombre()) // Buscar si el autor ya existe en el repositorio
                    .orElseGet(() -> autorRepository.saveAndFlush(new Autor(datosAutor))); // Si no existe, crear y guardar un nuevo autor
            libro.addAutor(autor); // Añadir el autor al libro
        });
    }

    // Método para listar los libros registrados
    private void listarLibrosRegistrados() {
        logger.logInfo("Listando los primeros 5 libros registrados:"); // Mostrar mensaje de inicio
        Pageable pageable = PageRequest.of(0, PAGE_SIZE); // Configurar la paginación para obtener los primeros 5 libros
        List<Libro> libros = libroRepository.findAllLibrosConAutores(pageable); // Obtener la lista de libros con sus autores

        if (libros.isEmpty()) { // Verificar si no hay libros registrados
            logger.logInfo("No hay libros registrados."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        libros.forEach(this::imprimirLibro); // Imprimir la información de cada libro
    }

    // Método para imprimir los detalles de un libro
    private void imprimirLibro(Libro libro) {
        String autoresNombres = libro.getAutores().stream() // Crear un stream de autores
                .map(Autor::getNombre) // Obtener el nombre de cada autor
                .reduce((autor1, autor2) -> autor1 + ", " + autor2) // Concatenar los nombres de los autores con comas
                .orElse("Sin autores registrados"); // Si no hay autores, mostrar un mensaje por defecto

        String idiomasFormateados = libro.getIdiomas().stream() // Crear un stream de idiomas
                .map(Languages::name) // Obtener el nombre de cada idioma
                .reduce((idioma1, idioma2) -> idioma1 + ", " + idioma2) // Concatenar los nombres de los idiomas con comas
                .orElse("Sin idiomas registrados"); // Si no hay idiomas, mostrar un mensaje por defecto

        logger.logInfo("""
            ------Libro------
            Título: %s
            Autores: %s
            Idiomas: %s
            Descargas: %d
            -----------------
            """.formatted(
                libro.getTitulo(), // Título del libro
                autoresNombres, // Nombres de los autores
                idiomasFormateados, // Idiomas del libro
                libro.getNumeroDescargas() // Número de descargas del libro
        ));
    }

    // Método para listar los autores registrados
    private void listarAutoresRegistrados() {
        logger.logInfo("Listando todos los autores registrados:"); // Mostrar mensaje de inicio
        Pageable pageable = PageRequest.of(0, PAGE_SIZE); // Configurar la paginación para obtener los primeros 5 autores
        List<Autor> autores = autorRepository.findAllAutores(pageable); // Obtener la lista de autores

        if (autores.isEmpty()) { // Verificar si no hay autores registrados
            logger.logInfo("No hay autores registrados."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        autores.forEach(this::imprimirAutor); // Imprimir la información de cada autor
    }

    // Método para imprimir los detalles de un autor
    private void imprimirAutor(Autor autor) {
        logger.logInfo("""
            ------Autor------
            Nombre: %s
            Año de Nacimiento: %d
            Año de Fallecimiento: %s
            -----------------
            """.formatted(
                autor.getNombre(), // Nombre del autor
                autor.getAnioNacimiento(), // Año de nacimiento del autor
                autor.getAnioFallecimiento() > 0 ? autor.getAnioFallecimiento() : "Vivo" // Año de fallecimiento o estado de "Vivo"
        ));
    }

    // Método para listar los autores vivos en un año específico
    private void listarAutoresVivosAnio() {
        logger.logInfo("Por favor, ingresa el año para listar autores vivos:"); // Solicitar al usuario el año
        try {
            final int anio = Integer.parseInt(scanner.nextLine().trim()); // Leer y convertir la entrada del usuario a un número entero
            logger.logInfo("Buscando autores vivos en el año " + anio + "..."); // Mostrar mensaje de búsqueda

            Pageable limit = PageRequest.of(0, PAGE_SIZE); // Configurar la paginación
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAnio(anio, limit); // Obtener la lista de autores vivos en el año indicado

            if (autoresVivos.isEmpty()) { // Verificar si no hay autores vivos en el año
                logger.logInfo("No se encontraron autores vivos en el año " + anio + "."); // Mostrar mensaje de error
                return; // Terminar la ejecución del método
            }

            autoresVivos.forEach(this::imprimirAutor); // Imprimir la información de cada autor vivo
        } catch (NumberFormatException e) { // Capturar excepción si el usuario ingresa un valor no numérico
            logger.logInfo("Entrada inválida. Por favor ingresa un año válido."); // Mostrar mensaje de error
        }
    }

    // Método para listar libros según el idioma
    private void listarLibrosPorIdioma() {
        logger.logInfo("Por favor, ingresa el idioma para filtrar los libros (código ISO o nombre en inglés/español):"); // Solicitar al usuario el idioma
        String inputIdioma = scanner.nextLine().trim(); // Leer y limpiar la entrada del usuario

        if (inputIdioma.isEmpty()) { // Verificar si no se ingresó un idioma
            logger.logInfo("No ingresaste ningún idioma. Por favor intenta de nuevo."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        String idiomaFormat = inputIdioma.substring(0, 1).toUpperCase() + inputIdioma.substring(1).toLowerCase(); // Formatear el idioma ingresado

        try {
            final Languages idioma = Languages.fromInput(idiomaFormat); // Intentar convertir el idioma ingresado al enum Languages
            logger.logInfo("Buscando libros en el idioma: " + idioma.name()); // Mostrar mensaje de búsqueda

            Pageable limit = PageRequest.of(0, PAGE_SIZE); // Configurar la paginación
            List<Libro> librosPorIdioma = libroRepository.findLibrosPorIdioma(idioma, limit); // Obtener la lista de libros en el idioma indicado

            if (librosPorIdioma.isEmpty()) { // Verificar si no hay libros en el idioma
                logger.logInfo("No se encontraron libros en el idioma " + idioma.getLanguagesOmdb() + "."); // Mostrar mensaje de error
                return; // Terminar la ejecución del método
            }

            librosPorIdioma.forEach(this::imprimirLibro); // Imprimir la información de cada libro
        } catch (IllegalArgumentException e) { // Capturar excepción si el idioma ingresado no es válido
            logger.logInfo("Idioma no reconocido. Por favor, ingresa un idioma válido."); // Mostrar mensaje de error
        }
    }

    // Método para mostrar las estadísticas de los libros registrados
    private void mostrarEstadisticas() {
        logger.logInfo("Generando estadísticas de los libros registrados..."); // Mostrar mensaje de inicio

        List<Libro> libros = libroRepository.findAll(); // Obtener todos los libros registrados
        if (libros.isEmpty()) { // Verificar si no hay libros registrados
            logger.logInfo("No hay libros registrados para generar estadísticas."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        IntSummaryStatistics estadisticasDescargas = libros.stream() // Crear un stream de libros
                .mapToInt(Libro::getNumeroDescargas) // Obtener el número de descargas de cada libro
                .summaryStatistics(); // Generar estadísticas de las descargas

        IntSummaryStatistics estadisticasAutoresPorLibro = libros.stream() // Crear un stream de libros
                .mapToInt(libro -> libro.getAutores().size()) // Obtener el número de autores de cada libro
                .summaryStatistics(); // Generar estadísticas del número de autores por libro

        logger.logInfo("""
            ------ Estadísticas ------
            Total de libros: %d
            Descargas totales: %d
            Promedio de descargas por libro: %.2f
            Máximo de descargas: %d
            Mínimo de descargas: %d
            
            Promedio de autores por libro: %.2f
            Máximo de autores en un libro: %d
            Mínimo de autores en un libro: %d
            --------------------------
            """.formatted(
                estadisticasDescargas.getCount(), // Total de libros registrados
                estadisticasDescargas.getSum(), // Total de descargas
                estadisticasDescargas.getAverage(), // Promedio de descargas por libro
                estadisticasDescargas.getMax(), // Máximo de descargas
                estadisticasDescargas.getMin(), // Mínimo de descargas
                estadisticasAutoresPorLibro.getAverage(), // Promedio de autores por libro
                estadisticasAutoresPorLibro.getMax(), // Máximo de autores en un libro
                estadisticasAutoresPorLibro.getMin() // Mínimo de autores en un libro
        ));
    }

    // Método para buscar el Top 10 de libros más descargados
    private void buscarTop10Libros() {
        logger.logInfo("Buscando el Top 10 de libros más descargados..."); // Mostrar mensaje de inicio

        Pageable top10 = PageRequest.of(0, 10); // Configurar la paginación para obtener los primeros 10 libros
        List<Libro> topLibros = libroRepository.findTop10Libros(top10); // Obtener la lista de los 10 libros más descargados

        if (topLibros.isEmpty()) { // Verificar si no hay libros registrados
            logger.logInfo("No hay libros registrados para generar el Top 10."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        logger.logInfo("------ Top 10 Libros Más Descargados ------"); // Mostrar encabezado del Top 10
        topLibros.forEach(this::imprimirLibro); // Imprimir la información de cada libro
    }

    // Método para buscar un autor en la base de datos
    private void buscarAutor() {
        logger.logInfo("Por favor, escribe el nombre del autor que deseas buscar (formato: Nombre Apellido):"); // Solicitar al usuario el nombre del autor
        String nombreUsuario = scanner.nextLine().trim(); // Leer y limpiar la entrada del usuario

        if (nombreUsuario.isEmpty()) { // Verificar si no se ingresó un nombre
            logger.logInfo("No ingresaste ningún nombre. Por favor intenta de nuevo."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        String[] partesNombre = nombreUsuario.split(" "); // Dividir el nombre ingresado en partes
        if (partesNombre.length < 2) { // Verificar si el nombre no tiene al menos dos partes
            logger.logInfo("Formato inválido. Por favor ingresa el nombre completo (Nombre Apellido)."); // Mostrar mensaje de error
            return; // Terminar la ejecución del método
        }

        String formatoBD = partesNombre[1] + ", " + partesNombre[0]; // Formatear el nombre para buscar en la base de datos

        List<Autor> autores = autorRepository.findByNombreParcial("%" + formatoBD + "%"); // Buscar autores que coincidan parcialmente con el nombre

        if (autores.isEmpty()) { // Verificar si no se encontraron autores
            logger.logInfo("No se encontró ningún autor con el nombre: " + nombreUsuario); // Mostrar mensaje de error
        } else { // Si se encontraron autores
            logger.logInfo("Autores encontrados:"); // Mostrar encabezado
            autores.forEach(this::imprimirAutor); // Imprimir la información de cada autor encontrado
        }
    }

    // Método para listar los autores fallecidos en un año específico
    private void listarAutoresMuertosAnio() {
        logger.logInfo("Por favor, ingresa el año para listar los autores fallecidos:"); // Solicitar al usuario el año
        try {
            int anio = Integer.parseInt(scanner.nextLine().trim()); // Leer y convertir la entrada del usuario a un número entero

            Pageable limit = PageRequest.of(0, PAGE_SIZE); // Configurar la paginación para obtener los primeros 5 autores fallecidos
            List<Autor> autores = autorRepository.findAutoresPorAnioFallecimiento(anio, limit); // Obtener la lista de autores fallecidos en el año indicado

            if (autores.isEmpty()) { // Verificar si no hay autores fallecidos en el año
                logger.logInfo("No se encontraron autores fallecidos en el año " + anio + "."); // Mostrar mensaje de error
                return; // Terminar la ejecución del método
            }

            logger.logInfo("Autores fallecidos en el año " + anio + ":"); // Mostrar encabezado
            autores.forEach(this::imprimirAutor); // Imprimir la información de cada autor fallecido
        } catch (NumberFormatException e) { // Capturar excepción si el usuario ingresa un valor no numérico
            logger.logInfo("El año ingresado no es válido. Por favor, intenta de nuevo."); // Mostrar mensaje de error
        }
    }
}
