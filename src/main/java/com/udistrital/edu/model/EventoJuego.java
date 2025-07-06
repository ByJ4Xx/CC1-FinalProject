/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.TipoEvento;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author USUARIO
 */
/**
 * Representa un evento que puede ocurrir durante el juego
 */
public class EventoJuego {
    private final String id;
    private final TipoEvento tipo;
    private final String descripcion;
    private final int prioridad; // Mayor número = mayor prioridad
    private final Map<String, Object> parametros;
    private boolean procesado;
    private long tiempoCreacion;
    
    public EventoJuego(String id, TipoEvento tipo, String descripcion, int prioridad) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.parametros = new HashMap<>();
        this.procesado = false;
        this.tiempoCreacion = System.currentTimeMillis();
    }
    
    // Métodos para manejar parámetros
    public void agregarParametro(String clave, Object valor) {
        parametros.put(clave, valor);
    }
    
    public Object obtenerParametro(String clave) {
        return parametros.get(clave);
    }
    
    public <T> T obtenerParametro(String clave, Class<T> tipo) {
        Object valor = parametros.get(clave);
        if (valor != null && tipo.isInstance(valor)) {
            return tipo.cast(valor);
        }
        return null;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public TipoEvento getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public int getPrioridad() { return prioridad; }
    public boolean isProcesado() { return procesado; }
    public void setProcesado(boolean procesado) { this.procesado = procesado; }
    public long getTiempoCreacion() { return tiempoCreacion; }
    
    public Map<String, Object> getParametros() {
        return new HashMap<>(parametros);
    }
    
    @Override
    public String toString() {
        return String.format("Evento[%s]: %s - %s (Prioridad: %d)", 
            id, tipo.getDescripcion(), descripcion, prioridad);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventoJuego evento = (EventoJuego) obj;
        return Objects.equals(id, evento.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
