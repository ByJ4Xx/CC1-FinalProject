/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.EstadoPolitico;
import com.udistrital.edu.model.Constantes.HabilidadEspecial;
import com.udistrital.edu.model.Constantes.NivelJerarquico;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Representa un político o funcionario en la red de corrupción
 * Implementa el patrón Builder para construcción flexible
 */
public class Politico {
    // Atributos principales
    private final String id;
    private final String nombre;
    private final NivelJerarquico nivelJerarquico;
    
    // Atributos económicos
    private int costoSoborno;
    private int aportePorInfluencia;
    private int aportePorRiqueza;
    
    // Atributos de comportamiento (0-100)
    private int nivelLealtad;
    private int nivelAmbicion;
    private int riesgoExposicion;
    
    // Estado y capacidades
    private EstadoPolitico estado;
    private Set<HabilidadEspecial> habilidadesEspeciales;
    private boolean aceptaSobornos;
    
    // Estructura del árbol
    private Politico padre;
    private List<Politico> subordinados;
    
    // Constructor privado
    private Politico(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.nivelJerarquico = builder.nivelJerarquico;
        this.costoSoborno = builder.costoSoborno;
        this.aportePorInfluencia = builder.aportePorInfluencia;
        this.aportePorRiqueza = builder.aportePorRiqueza;
        this.nivelLealtad = builder.nivelLealtad;
        this.nivelAmbicion = builder.nivelAmbicion;
        this.riesgoExposicion = builder.riesgoExposicion;
        this.estado = builder.estado;
        this.habilidadesEspeciales = new HashSet<>(builder.habilidadesEspeciales);
        this.aceptaSobornos = builder.aceptaSobornos;
        this.subordinados = new ArrayList<>();
        this.padre = null;
    }
    
    // Patrón Builder
    public static class Builder {
        // Atributos obligatorios
        private final String id;
        private final String nombre;
        private final NivelJerarquico nivelJerarquico;
        
        // Atributos opcionales con valores por defecto
        private int costoSoborno = 1000;
        private int aportePorInfluencia = 10;
        private int aportePorRiqueza = 100;
        private int nivelLealtad = 50;
        private int nivelAmbicion = 50;
        private int riesgoExposicion = 30;
        private EstadoPolitico estado = EstadoPolitico.ACTIVO;
        private Set<HabilidadEspecial> habilidadesEspeciales = new HashSet<>();
        private boolean aceptaSobornos = true;
        
        public Builder(String id, String nombre, NivelJerarquico nivelJerarquico) {
            this.id = id;
            this.nombre = nombre;
            this.nivelJerarquico = nivelJerarquico;
        }
        
        public Builder costoSoborno(int costoSoborno) {
            this.costoSoborno = Math.max(0, costoSoborno);
            return this;
        }
        
        public Builder aportePorInfluencia(int aporte) {
            this.aportePorInfluencia = Math.max(0, aporte);
            return this;
        }
        
        public Builder aportePorRiqueza(int aporte) {
            this.aportePorRiqueza = Math.max(0, aporte);
            return this;
        }
        
        public Builder nivelLealtad(int nivel) {
            this.nivelLealtad = Math.max(0, Math.min(100, nivel));
            return this;
        }
        
        public Builder nivelAmbicion(int nivel) {
            this.nivelAmbicion = Math.max(0, Math.min(100, nivel));
            return this;
        }
        
        public Builder riesgoExposicion(int riesgo) {
            this.riesgoExposicion = Math.max(0, Math.min(100, riesgo));
            return this;
        }
        
        public Builder estado(EstadoPolitico estado) {
            this.estado = estado;
            return this;
        }
        
        public Builder agregarHabilidad(HabilidadEspecial habilidad) {
            this.habilidadesEspeciales.add(habilidad);
            return this;
        }

        public boolean tieneHabilidad(HabilidadEspecial habilidad) {
            return habilidadesEspeciales.contains(habilidad);
        }
        
        public Builder aceptaSobornos(boolean acepta) {
            this.aceptaSobornos = acepta;
            return this;
        }
        
        public Politico build() {
            return new Politico(this);
        }
    }
    
    // Métodos de gestión de la estructura del árbol
    public void agregarSubordinado(Politico subordinado) {
        if (subordinado != null && !subordinados.contains(subordinado)) {
            subordinados.add(subordinado);
            subordinado.padre = this;
        }
    }
    
    public boolean removerSubordinado(Politico subordinado) {
        if (subordinados.remove(subordinado)) {
            subordinado.padre = null;
            return true;
        }
        return false;
    }
    
    // Métodos de cálculo
    public int calcularInfluenciaTotal() {
        int influenciaTotal = this.aportePorInfluencia;
        for (Politico subordinado : subordinados) {
            if (subordinado.isActivo()) {
                influenciaTotal += subordinado.calcularInfluenciaTotal();
            }
        }
        return influenciaTotal;
    }
    
    public int calcularRiquezaTotal() {
        int riquezaTotal = this.aportePorRiqueza;
        for (Politico subordinado : subordinados) {
            if (subordinado.isActivo()) {
                riquezaTotal += subordinado.calcularRiquezaTotal();
            }
        }
        return riquezaTotal;
    }
    
    public double calcularRiesgoPromedio() {
        double riesgoTotal = this.riesgoExposicion;
        int contador = 1;
        
        for (Politico subordinado : subordinados) {
            if (subordinado.isActivo()) {
                riesgoTotal += subordinado.calcularRiesgoPromedio();
                contador++;
            }
        }
        return riesgoTotal / contador;
    }
    
    // Métodos de estado
    public boolean isActivo() {
        return estado == EstadoPolitico.ACTIVO;
    }
    
    public boolean puedeSerSobornado() {
        return aceptaSobornos && (estado == EstadoPolitico.ACTIVO || 
                                 estado == EstadoPolitico.BAJO_SOSPECHA);
    }
    
    public boolean tieneHabilidad(HabilidadEspecial habilidad) {
        return habilidadesEspeciales.contains(habilidad);
    }
    
    // Métodos para modificar atributos (con validación)
    public void modificarLealtad(int cambio) {
        this.nivelLealtad = Math.max(0, Math.min(100, this.nivelLealtad + cambio));
    }
    
    public void modificarAmbicion(int cambio) {
        this.nivelAmbicion = Math.max(0, Math.min(100, this.nivelAmbicion + cambio));
    }
    
    public void modificarRiesgoExposicion(int cambio) {
        this.riesgoExposicion = Math.max(0, Math.min(100, this.riesgoExposicion + cambio));
    }

    public boolean usarHabilidad(HabilidadEspecial habilidad, String idObjetivo) {
        if (!tieneHabilidad(habilidad)) return false;

        switch (habilidad) {
            case CONTRATO_SICARIOS:

                break;
            // ...
        }

        habilidadesEspeciales.remove(habilidad);
        return true;
    }
    
    
    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public NivelJerarquico getNivelJerarquico() { return nivelJerarquico; }
    public int getCostoSoborno() { return costoSoborno; }
    public int getAportePorInfluencia() { return aportePorInfluencia; }
    public int getAportePorRiqueza() { return aportePorRiqueza; }
    public int getNivelLealtad() { return nivelLealtad; }
    public int getNivelAmbicion() { return nivelAmbicion; }
    public int getRiesgoExposicion() { return riesgoExposicion; }
    public EstadoPolitico getEstado() { return estado; }
    public Set<HabilidadEspecial> getHabilidadesEspeciales() { 
        return new HashSet<>(habilidadesEspeciales); 
    }
    public boolean isAceptaSobornos() { return aceptaSobornos; }
    public Politico getPadre() { return padre; }
    public List<Politico> getSubordinados() { 
        return new ArrayList<>(subordinados); 
    }
    
    // Setters con validación
    public void setEstado(EstadoPolitico estado) {
        this.estado = estado;
    }
    
    public void setAceptaSobornos(boolean aceptaSobornos) {
        this.aceptaSobornos = aceptaSobornos;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s [%s]", 
            nombre, nivelJerarquico.getTitulo(), estado.getDescripcion(), id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Politico politico = (Politico) obj;
        return Objects.equals(id, politico.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
