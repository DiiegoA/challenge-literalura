# Literature Application

## Description
The **Literature Application** is a Java-based project developed to manage and explore a repository of literary content, including books and their associated authors. It combines the power of Spring Boot with RESTful API integration, offering a comprehensive system for handling literary data. Users can perform dynamic searches, analyze statistics, and explore book details through a user-friendly menu interface.

The application includes robust features such as multilingual support, dynamic data conversion, and a flexible logging system for monitoring activities and debugging. By integrating external APIs and implementing advanced data management techniques, this application serves as an effective solution for organizing and exploring literary data.

## Key Features

### 1. Main Application
- **com.aluracursos.literatura.LiteraturaApplication**: Acts as the Spring Boot entry point, initializing the environment and running the main menu for accessing all application features.

### 2. Interactive Menu
- **com.aluracursos.literatura.menu.MenuPrincipal**: Provides an intuitive menu for user interaction, allowing book searches, author queries, language-based filtering, and statistical analysis.

### 3. Author Model
- **com.aluracursos.literatura.model.Autor**: Represents authors with details such as name, birth year, and death year, while maintaining a bidirectional relationship with associated books.

### 4. Author Data
- **com.aluracursos.literatura.model.DatosAutor**: Maps JSON data from external APIs into a structured format for representing authors' information.

### 5. Book Data
- **com.aluracursos.literatura.model.DatosLibro**: Maps JSON data from external APIs into a structured format for representing book details, including authors, languages, and download statistics.

### 6. Multilingual Support
- **com.aluracursos.literatura.model.Languages**: An enumeration supporting multiple representations of each language, facilitating filtering and searching based on language preferences.

### 7. Book Model
- **com.aluracursos.literatura.model.Libro**: Represents books, encapsulating attributes like title, download count, languages, and their associated authors.

### 8. Author Repository
- **com.aluracursos.literatura.repository.AutorRepository**: Enables CRUD operations and custom queries for authors, such as searching by name or filtering by birth and death years.

### 9. Book Repository
- **com.aluracursos.literatura.repository.LibroRepository**: Provides advanced book search capabilities, including top downloads, language-based filtering, and CRUD operations.

### 10. API Integration
- **com.aluracursos.literatura.service.ConsumoApi**: Handles RESTful interactions with external APIs to fetch data on books and authors dynamically.

### 11. Data Conversion
- **com.aluracursos.literatura.service.ConvierteDatos**: Converts JSON data into Java objects using an efficient implementation of the `IConvierteDatos` interface, ensuring seamless data processing.

### 12. Data Conversion Interface
- **com.aluracursos.literatura.service.IConvierteDatos**: A generic interface for converting JSON strings into specific Java objects, promoting reusability and consistency in data handling.

### 13. Logging Framework
- **com.aluracursos.logger.loggerbase.LoggerBase**: An interface defining the structure for logging operations, ensuring a standardized approach to monitoring and debugging.

### 14. Logger Implementation
- **com.aluracursos.logger.loggerbase.LoggerBaseImpl**: Provides a practical implementation of `LoggerBase`, offering formatted console logs to enhance application traceability and error detection.

## System Requirements
To run this application, ensure the following prerequisites are met:
- **Java SDK 8 or higher**: The application is developed using Java.
- **Spring Boot Framework**: Facilitates application setup and dependency management.
- **Internet Connection**: Required for fetching data from external APIs.

## How to Run
1. **Clone the Repository**: Clone the project repository using the following command:
   ```bash
   git clone https://github.com/DiiegoA/challenge-literalura.git
