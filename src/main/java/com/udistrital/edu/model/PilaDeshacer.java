/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import java.util.Stack;

/**
 * Pila para manejar acciones que pueden ser deshechas
 */
public class PilaDeshacer {
    private Stack<AccionDeshacer> pilaDeshacer;
    private Stack<AccionDeshacer> pilaRehacer;
    private final int capacidadMaxima;
    
    public PilaDeshacer(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.pilaDeshacer = new Stack<>();
        this.pilaRehacer = new Stack<>();
    }
    
    public void agregarAccion(AccionDeshacer accion) {
        // Limpiar pila de rehacer cuando se agrega nueva acción
        pilaRehacer.clear();
        
        // Agregar acción
        pilaDeshacer.push(accion);
        
        // Mantener capacidad máxima
        while (pilaDeshacer.size() > capacidadMaxima) {
            pilaDeshacer.remove(0);
        }
    }
    
    public boolean deshacer() {
        if (pilaDeshacer.isEmpty()) return false;
        
        AccionDeshacer accion = pilaDeshacer.pop();
        boolean exito = accion.deshacer();
        
        if (exito) {
            pilaRehacer.push(accion);
        } else {
            pilaDeshacer.push(accion); // Devolver a la pila si falló
        }
        
        return exito;
    }
    
    public boolean rehacer() {
        if (pilaRehacer.isEmpty()) return false;
        
        AccionDeshacer accion = pilaRehacer.pop();
        boolean exito = accion.rehacer();
        
        if (exito) {
            pilaDeshacer.push(accion);
        } else {
            pilaRehacer.push(accion); // Devolver a la pila si falló
        }
        
        return exito;
    }
    
    public boolean puedeDeshacer() {
        return !pilaDeshacer.isEmpty();
    }
    
    public boolean puedeRehacer() {
        return !pilaRehacer.isEmpty();
    }
    
    public String obtenerDescripcionUltimaAccion() {
        if (pilaDeshacer.isEmpty()) return null;
        return pilaDeshacer.peek().getDescripcion();
    }
    
    public String obtenerDescripcionSiguienteRehacer() {
        if (pilaRehacer.isEmpty()) return null;
        return pilaRehacer.peek().getDescripcion();
    }
    
    public int obtenerNumeroAcciones() {
        return pilaDeshacer.size();
    }
    
    public void limpiar() {
        pilaDeshacer.clear();
        pilaRehacer.clear();
    }
}
