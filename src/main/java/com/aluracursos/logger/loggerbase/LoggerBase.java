package com.aluracursos.logger.loggerbase;

// Esta es una interfaz que define los métodos que una clase de logger debe implementar.
public interface LoggerBase {

    // Método para registrar un mensaje de información (INFO).
    // Las clases que implementen esta interfaz deben proporcionar una implementación de este método.
    void logInfo(Object message);
}
