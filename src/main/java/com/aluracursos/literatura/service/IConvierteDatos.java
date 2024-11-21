package com.aluracursos.literatura.service;

/**
 * La interfaz IConvierteDatos define un contrato para convertir datos desde un formato JSON
 * a una instancia de una clase específica.
 *
 * @param <T> El tipo genérico que se utilizará para convertir los datos.
 */

public interface IConvierteDatos {


    /**
     * Método genérico para convertir un String JSON en un objeto del tipo especificado.
     *
     * @param <T>    El tipo de objeto al que se va a convertir el JSON.
     * @param json   La cadena de texto en formato JSON que contiene los datos a convertir.
     * @param clase  La clase del tipo al que se quiere convertir el JSON.
     */
    <T> T obtenerDatos(String json, Class<T> clase);

}
