/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

/**
 *
 * @author USUARIO
 */
/**
 * Representa una acción que puede ser deshecha
 */
public abstract class AccionDeshacer {
    protected final String descripcion;
    protected final long timestamp;
    
    public AccionDeshacer(String descripcion) {
        this.descripcion = descripcion;
        this.timestamp = System.currentTimeMillis();
    }
    
    public abstract boolean deshacer();
    public abstract boolean rehacer();
    
    public String getDescripcion() { return descripcion; }
    public long getTimestamp() { return timestamp; }
}
