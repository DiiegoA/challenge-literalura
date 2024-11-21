package com.aluracursos.logger.loggerbase;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


// Clase que implementa la interfaz LoggerBase, proporcionando una implementación básica del sistema de logging.
public class LoggerBaseImpl implements LoggerBase{

    // Instancia del objeto Logger, que será utilizado para registrar mensajes.
    private final Logger logger;

    // Constructor de la clase LoggerBaseImpl.
    // Recibe como parámetro el nombre de la clase que será usada para identificar el logger.
    public LoggerBaseImpl(String className) {
        // Inicializa el logger con el nombre de la clase proporcionada.
        this.logger = Logger.getLogger(className);
        // Llama al método configureLogger para configurar el logger con los ajustes adecuados.
        configureLogger();
    }

    // Método privado para configurar el logger.
    // Verifica si ya existen manejadores (handlers) asociados al logger y, si no es así, añade uno.
    private void configureLogger() {
        // Si el logger no tiene manejadores (handlers), añade uno nuevo de consola (ConsoleHandler).
        if (logger.getHandlers().length == 0) {
            // Crea un nuevo ConsoleHandler, que se encargará de enviar los mensajes del logger a la consola.
            ConsoleHandler handler = new ConsoleHandler();
            // Configura el formato del ConsoleHandler utilizando una clase personalizada SimpleFormatter.
            handler.setFormatter(new SimpleFormatter());
            // Añade el ConsoleHandler al logger.
            logger.addHandler(handler);
        }
        // Indica que el logger no debe utilizar los manejadores predeterminados (por ejemplo, los padres).
        logger.setUseParentHandlers(false);
    }

    // Método implementado de la interfaz LoggerBase.
    // Este método registra un mensaje como información (INFO).
    @Override
    public void logInfo(Object message) {
        // Convierte el mensaje recibido en String y lo registra como un mensaje de nivel INFO.
        logger.info(String.valueOf(message));
    }

    // Clase interna estática que extiende la clase Formatter.
    // Proporciona un formato simple para los mensajes de log.
    private static class SimpleFormatter extends Formatter {
        // Método que da formato a un objeto LogRecord (el registro del log).
        @Override
        public String format(LogRecord record) {
            // Formatea el mensaje del registro y añade una nueva línea al final.
            return formatMessage(record) + System.lineSeparator();
        }
    }
}
