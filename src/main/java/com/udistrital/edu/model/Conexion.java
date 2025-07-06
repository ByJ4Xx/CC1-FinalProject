/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import java.util.Objects;

/**
 *
 * @author USUARIO
 */
/**
 * Representa una conexión entre dos entidades en el grafo de relaciones
 */
public class Conexion {
    private final String origen;
    private final String destino;
    private double peso; // Positivo = oportunidad, Negativo = riesgo
    private final String descripcion;
    private boolean activa;
    
    public Conexion(String origen, String destino, double peso, String descripcion) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.descripcion = descripcion;
        this.activa = true;
    }
    
    // Getters y Setters
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getDescripcion() { return descripcion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    
    public boolean esPositiva() { return peso > 0; }
    public boolean esNegativa() { return peso < 0; }
    
    @Override
    public String toString() {
        String tipo = peso > 0 ? "OPORTUNIDAD" : "RIESGO";
        return String.format("%s -> %s [%s: %.2f] %s", 
            origen, destino, tipo, Math.abs(peso), descripcion);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Conexion conexion = (Conexion) obj;
        return Objects.equals(origen, conexion.origen) && 
               Objects.equals(destino, conexion.destino);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(origen, destino);
    }
}
