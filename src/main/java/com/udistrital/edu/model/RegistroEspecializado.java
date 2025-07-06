/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.EstadoPolitico;
import com.udistrital.edu.model.Constantes.HabilidadEspecial;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registro especializado para políticos en estados especiales
 */
public class RegistroEspecializado {
    private Map<EstadoPolitico, Set<String>> politicosPorEstado;
    private Map<HabilidadEspecial, Set<String>> politicosPorHabilidad;
    private Map<String, Set<String>> politicosInvestigados; // fiscal -> Set<politicos>
    
    public RegistroEspecializado() {
        this.politicosPorEstado = new HashMap<>();
        this.politicosPorHabilidad = new HashMap<>();
        this.politicosInvestigados = new HashMap<>();
        
        // Inicializar mapas para cada estado
        for (EstadoPolitico estado : EstadoPolitico.values()) {
            politicosPorEstado.put(estado, new HashSet<>());
        }
        
        // Inicializar mapas para cada habilidad
        for (HabilidadEspecial habilidad : HabilidadEspecial.values()) {
            politicosPorHabilidad.put(habilidad, new HashSet<>());
        }
    }
    
    // Métodos para manejo de estados
    public void registrarCambioEstado(String idPolitico, EstadoPolitico estadoAnterior, 
                                     EstadoPolitico estadoNuevo) {
        if (estadoAnterior != null) {
            politicosPorEstado.get(estadoAnterior).remove(idPolitico);
        }
        politicosPorEstado.get(estadoNuevo).add(idPolitico);
    }
    
    public Set<String> obtenerPoliticosPorEstado(EstadoPolitico estado) {
        return new HashSet<>(politicosPorEstado.get(estado));
    }
    
    public boolean estaEnEstado(String idPolitico, EstadoPolitico estado) {
        return politicosPorEstado.get(estado).contains(idPolitico);
    }
    
    // Métodos para manejo de habilidades
    public void registrarHabilidad(String idPolitico, HabilidadEspecial habilidad) {
        politicosPorHabilidad.get(habilidad).add(idPolitico);
    }
    
    public void removerHabilidad(String idPolitico, HabilidadEspecial habilidad) {
        politicosPorHabilidad.get(habilidad).remove(idPolitico);
    }
    
    public Set<String> obtenerPoliticosConHabilidad(HabilidadEspecial habilidad) {
        return new HashSet<>(politicosPorHabilidad.get(habilidad));
    }
    
    public boolean tieneHabilidad(String idPolitico, HabilidadEspecial habilidad) {
        return politicosPorHabilidad.get(habilidad).contains(idPolitico);
    }
    
    // Métodos para investigaciones
    public void registrarInvestigacion(String idFiscal, String idPolitico) {
        politicosInvestigados.computeIfAbsent(idFiscal, k -> new HashSet<>()).add(idPolitico);
    }
    
    public void removerInvestigacion(String idFiscal, String idPolitico) {
        Set<String> investigados = politicosInvestigados.get(idFiscal);
        if (investigados != null) {
            investigados.remove(idPolitico);
            if (investigados.isEmpty()) {
                politicosInvestigados.remove(idFiscal);
            }
        }
    }
    
    public Set<String> obtenerPoliticosInvestigadosPor(String idFiscal) {
        return new HashSet<>(politicosInvestigados.getOrDefault(idFiscal, new HashSet<>()));
    }
    
    public Set<String> obtenerFiscalesInvestigando(String idPolitico) {
        return politicosInvestigados.entrySet().stream()
                .filter(entry -> entry.getValue().contains(idPolitico))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }
    
    // Métodos de consulta rápida
    public int contarPoliticosPorEstado(EstadoPolitico estado) {
        return politicosPorEstado.get(estado).size();
    }
    
    public int contarPoliticosConHabilidad(HabilidadEspecial habilidad) {
        return politicosPorHabilidad.get(habilidad).size();
    }
    
    public int contarInvestigacionesActivas() {
        return politicosInvestigados.values().stream()
                .mapToInt(Set::size)
                .sum();
    }
    
    // Obtener estadísticas generales
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        // Estadísticas por estado
        Map<String, Integer> estadoStats = new HashMap<>();
        for (EstadoPolitico estado : EstadoPolitico.values()) {
            estadoStats.put(estado.name(), contarPoliticosPorEstado(estado));
        }
        stats.put("politicosPorEstado", estadoStats);
        
        // Estadísticas por habilidad
        Map<String, Integer> habilidadStats = new HashMap<>();
        for (HabilidadEspecial habilidad : HabilidadEspecial.values()) {
            habilidadStats.put(habilidad.name(), contarPoliticosConHabilidad(habilidad));
        }
        stats.put("politicosPorHabilidad", habilidadStats);
        
        stats.put("investigacionesActivas", contarInvestigacionesActivas());
        stats.put("fiscalesActivos", politicosInvestigados.size());
        
        return stats;
    }
    
    // Limpieza
    public void limpiarRegistros() {
        politicosPorEstado.values().forEach(Set::clear);
        politicosPorHabilidad.values().forEach(Set::clear);
        politicosInvestigados.clear();
    }
}
