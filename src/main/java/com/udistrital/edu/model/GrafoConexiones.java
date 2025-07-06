/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author USUARIO
 */
/**
 * Grafo dirigido ponderado para representar las relaciones externas
 * Usa lista de adyacencia para eficiencia en espacio
 */
public class GrafoConexiones {
    private Map<String, List<Conexion>> listaAdyacencia;
    private Set<String> entidades; // Todos los nodos del grafo
    
    public GrafoConexiones() {
        this.listaAdyacencia = new HashMap<>();
        this.entidades = new HashSet<>();
    }
    
    // Operaciones básicas del grafo
    public void agregarEntidad(String entidad) {
        if (!entidades.contains(entidad)) {
            entidades.add(entidad);
            listaAdyacencia.put(entidad, new ArrayList<>());
        }
    }
    
    public boolean agregarConexion(Conexion conexion) {
        if (conexion == null) return false;
        
        // Asegurar que ambas entidades existen
        agregarEntidad(conexion.getOrigen());
        agregarEntidad(conexion.getDestino());
        
        // Verificar si la conexión ya existe
        List<Conexion> conexionesOrigen = listaAdyacencia.get(conexion.getOrigen());
        for (Conexion c : conexionesOrigen) {
            if (c.getDestino().equals(conexion.getDestino())) {
                return false; // Conexión ya existe
            }
        }
        
        conexionesOrigen.add(conexion);
        return true;
    }
    
    public boolean removerConexion(String origen, String destino) {
        List<Conexion> conexiones = listaAdyacencia.get(origen);
        if (conexiones == null) return false;
        
        return conexiones.removeIf(c -> c.getDestino().equals(destino));
    }
    
    public Conexion obtenerConexion(String origen, String destino) {
        List<Conexion> conexiones = listaAdyacencia.get(origen);
        if (conexiones == null) return null;
        
        return conexiones.stream()
                .filter(c -> c.getDestino().equals(destino))
                .findFirst()
                .orElse(null);
    }
    
    public List<Conexion> obtenerConexiones(String entidad) {
        return new ArrayList<>(listaAdyacencia.getOrDefault(entidad, new ArrayList<>()));
    }
    
    public List<Conexion> obtenerConexionesActivas(String entidad) {
        return listaAdyacencia.getOrDefault(entidad, new ArrayList<>())
                .stream()
                .filter(Conexion::isActiva)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Métodos de análisis
    public List<Conexion> obtenerRiesgos(String entidad) {
        return obtenerConexionesActivas(entidad).stream()
                .filter(Conexion::esNegativa)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Conexion> obtenerOportunidades(String entidad) {
        return obtenerConexionesActivas(entidad).stream()
                .filter(Conexion::esPositiva)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public double calcularRiesgoTotal(String entidad) {
        return obtenerRiesgos(entidad).stream()
                .mapToDouble(c -> Math.abs(c.getPeso()))
                .sum();
    }
    
    public double calcularOportunidadTotal(String entidad) {
        return obtenerOportunidades(entidad).stream()
                .mapToDouble(Conexion::getPeso)
                .sum();
    }
    
    // Búsqueda en profundidad (DFS)
    public List<String> busquedaProfundidad(String inicio) {
        List<String> visitados = new ArrayList<>();
        Set<String> marcados = new HashSet<>();
        dfsRecursivo(inicio, visitados, marcados);
        return visitados;
    }
    
    private void dfsRecursivo(String nodo, List<String> visitados, Set<String> marcados) {
        marcados.add(nodo);
        visitados.add(nodo);
        
        for (Conexion conexion : obtenerConexionesActivas(nodo)) {
            if (!marcados.contains(conexion.getDestino())) {
                dfsRecursivo(conexion.getDestino(), visitados, marcados);
            }
        }
    }
    
    // Búsqueda en amplitud (BFS)
    public List<String> busquedaAmplitud(String inicio) {
        List<String> visitados = new ArrayList<>();
        Set<String> marcados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        
        cola.offer(inicio);
        marcados.add(inicio);
        
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            visitados.add(actual);
            
            for (Conexion conexion : obtenerConexionesActivas(actual)) {
                if (!marcados.contains(conexion.getDestino())) {
                    marcados.add(conexion.getDestino());
                    cola.offer(conexion.getDestino());
                }
            }
        }
        
        return visitados;
    }
    
    // Obtener entidades conectadas
    public Set<String> obtenerEntidadesConectadas(String entidad) {
        Set<String> conectadas = new HashSet<>();
        for (Conexion conexion : obtenerConexionesActivas(entidad)) {
            conectadas.add(conexion.getDestino());
        }
        return conectadas;
    }
    
    // Getters
    public Set<String> getEntidades() {
        return new HashSet<>(entidades);
    }
    
    public int obtenerNumeroEntidades() {
        return entidades.size();
    }
    
    public int obtenerNumeroConexiones() {
        return listaAdyacencia.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    // Método para obtener estadísticas
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("numeroEntidades", obtenerNumeroEntidades());
        stats.put("numeroConexiones", obtenerNumeroConexiones());
        
        long conexionesPositivas = listaAdyacencia.values().stream()
                .flatMap(List::stream)
                .filter(Conexion::esPositiva)
                .count();
        
        long conexionesNegativas = listaAdyacencia.values().stream()
                .flatMap(List::stream)
                .filter(Conexion::esNegativa)
                .count();
        
        stats.put("oportunidades", conexionesPositivas);
        stats.put("riesgos", conexionesNegativas);
        
        return stats;
    }
}
