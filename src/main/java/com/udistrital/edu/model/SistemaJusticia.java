/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.EstadoPolitico;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author USUARIO
 */
public class SistemaJusticia {
    private final GrafoConexiones grafoConexiones;
    private final ArbolCorrupcion arbolCorrupcion;
    private final RegistroEspecializado registro;
    private final Map<String, Integer> evidenciaAcumulada; // ID político -> puntos de evidencia
    
    // Configuración de la IA
    private static final int UMBRAL_EVIDENCIA = 100;
    private static final int EVIDENCIA_BASE = 10;
    private static final int MULTIPLICADOR_RIESGO = 2;
    
    public SistemaJusticia(GrafoConexiones grafoConexiones, ArbolCorrupcion arbolCorrupcion, 
                         RegistroEspecializado registro) {
        this.grafoConexiones = grafoConexiones;
        this.arbolCorrupcion = arbolCorrupcion;
        this.registro = registro;
        this.evidenciaAcumulada = new HashMap<>();
    }
    
    // =========================================================================
    // MÉTODOS PRINCIPALES DE INVESTIGACIÓN
    // =========================================================================
    
    /**
     * Ejecuta todas las investigaciones activas en un turno
     */
    public void ejecutarInvestigacionesTurno() {
        // 1. Actualizar evidencia acumulada
        actualizarEvidencia();
        
        // 2. Procesar políticos con suficiente evidencia
        procesarEvidenciaSuficiente();
        
        // 3. Iniciar nuevas investigaciones basadas en riesgo
        iniciarNuevasInvestigaciones();
    }
    
    /**
     * Actualiza la evidencia acumulada para todos los políticos bajo investigación
     */
    private void actualizarEvidencia() {
        // Obtener todos los políticos bajo investigación o sospecha
        Set<String> investigados = registro.obtenerPoliticosPorEstado(EstadoPolitico.INVESTIGADO);
        Set<String> sospechosos = registro.obtenerPoliticosPorEstado(EstadoPolitico.BAJO_SOSPECHA);
        
        // Combinar y actualizar evidencia
        Set<String> todos = new HashSet<>();
        todos.addAll(investigados);
        todos.addAll(sospechosos);
        
        for (String idPolitico : todos) {
            Politico politico = arbolCorrupcion.buscarPolitico(idPolitico);
            if (politico != null) {
                int incremento = calcularIncrementoEvidencia(politico);
                evidenciaAcumulada.merge(idPolitico, incremento, Integer::sum);
                
                System.out.printf("Evidencia acumulada para %s: %d (+%d)%n",
                                politico.getNombre(), 
                                evidenciaAcumulada.getOrDefault(idPolitico, 0),
                                incremento);
            }
        }
    }
    
    /**
     * Calcula cuánta evidencia se acumula contra un político este turno
     */
    private int calcularIncrementoEvidencia(Politico politico) {
        int base = EVIDENCIA_BASE;
        
        // Modificadores
        double modRiesgo = politico.getRiesgoExposicion() / 100.0;
        double modEstado = politico.getEstado() == EstadoPolitico.INVESTIGADO ? 1.5 : 1.0;
        double modConexiones = calcularRiesgoConexiones(politico) / 50.0;
        
        return (int)(base * modRiesgo * modEstado * modConexiones);
    }
    
    /**
     * Calcula el riesgo por conexiones en el grafo
     */
    private double calcularRiesgoConexiones(Politico politico) {
        return grafoConexiones.obtenerRiesgos(politico.getId()).stream()
               .mapToDouble(Conexion::getPeso)
               .map(Math::abs)
               .sum();
    }
    
    /**
     * Procesa políticos con evidencia suficiente para tomar acciones
     */
    private void procesarEvidenciaSuficiente() {
        List<Map.Entry<String, Integer>> conEvidencia = evidenciaAcumulada.entrySet().stream()
            .filter(e -> e.getValue() >= UMBRAL_EVIDENCIA)
            .collect(Collectors.toList());
        
        for (Map.Entry<String, Integer> entry : conEvidencia) {
            Politico politico = arbolCorrupcion.buscarPolitico(entry.getKey());
            if (politico != null) {
                tomarAccionLegal(politico);
                evidenciaAcumulada.remove(entry.getKey()); // Resetear evidencia
            }
        }
    }
    
    /**
     * Toma acción legal contra un político con evidencia suficiente
     */
    private void tomarAccionLegal(Politico politico) {
        System.out.printf("¡ACCIÓN LEGAL! %s tiene evidencia suficiente (%d puntos)%n",
                        politico.getNombre(), evidenciaAcumulada.get(politico.getId()));
        
        // 70% de probabilidad de encarcelamiento, 30% de quedar quemado
        if (Math.random() < 0.7) {
            politico.setEstado(EstadoPolitico.ENCARCELADO);
            registro.registrarCambioEstado(politico.getId(), politico.getEstado(), EstadoPolitico.ENCARCELADO);
            arbolCorrupcion.eliminarPolitico(politico.getId());
            System.out.println("Resultado: Encarcelado");
        } else {
            politico.setEstado(EstadoPolitico.QUEMADO);
            registro.registrarCambioEstado(politico.getId(), politico.getEstado(), EstadoPolitico.QUEMADO);
            System.out.println("Resultado: Quemado (inútil para la red)");
        }
    }
    
    /**
     * Inicia nuevas investigaciones basadas en riesgo y conexiones
     */
    private void iniciarNuevasInvestigaciones() {
        // Usar Dijkstra para encontrar el camino más probable de investigación
        List<String> fiscalesActivos = grafoConexiones.getEntidades().stream()
            .filter(id -> id.startsWith("fis-"))
            .collect(Collectors.toList());
            
        for (String fiscalId : fiscalesActivos) {
            Optional<Politico> objetivo = encontrarObjetivoInvestigacion(fiscalId);
            objetivo.ifPresent(p -> {
                if (p.getEstado() == EstadoPolitico.ACTIVO) {
                    p.setEstado(EstadoPolitico.BAJO_SOSPECHA);
                    registro.registrarCambioEstado(p.getId(), EstadoPolitico.ACTIVO, EstadoPolitico.BAJO_SOSPECHA);
                    System.out.printf("%s inicia investigación sobre %s%n", fiscalId, p.getNombre());
                }
            });
        }
    }
    
    /**
     * Encuentra el objetivo más probable para un fiscal usando Dijkstra
     */
    private Optional<Politico> encontrarObjetivoInvestigacion(String fiscalId) {
        Map<String, Double> distancias = new HashMap<>();
        PriorityQueue<NodoDistancia> colaPrioridad = new PriorityQueue<>();
        
        // Inicialización
        grafoConexiones.getEntidades().forEach(e -> distancias.put(e, Double.POSITIVE_INFINITY));
        distancias.put(fiscalId, 0.0);
        colaPrioridad.add(new NodoDistancia(fiscalId, 0.0));
        
        // Algoritmo de Dijkstra
        while (!colaPrioridad.isEmpty()) {
            NodoDistancia actual = colaPrioridad.poll();
            
            for (Conexion conexion : grafoConexiones.obtenerConexiones(actual.id)) {
                if (conexion.esPositiva()) continue; // Solo conexiones negativas (riesgos)
                
                double nuevaDistancia = actual.distancia + Math.abs(conexion.getPeso());
                if (nuevaDistancia < distancias.get(conexion.getDestino())) {
                    distancias.put(conexion.getDestino(), nuevaDistancia);
                    colaPrioridad.add(new NodoDistancia(conexion.getDestino(), nuevaDistancia));
                }
            }
        }
        
        // Encontrar el político activo con menor distancia (mayor riesgo)
        return distancias.entrySet().stream()
            .filter(e -> e.getKey().startsWith("alc-") || e.getKey().startsWith("gob-") || e.getKey().startsWith("min-"))
            .filter(e -> {
                Politico p = arbolCorrupcion.buscarPolitico(e.getKey());
                return p != null && p.isActivo();
            })
            .min(Comparator.comparingDouble(Map.Entry::getValue))
            .map(e -> arbolCorrupcion.buscarPolitico(e.getKey()));
    }
    
    // Clase auxiliar para Dijkstra
    private static class NodoDistancia implements Comparable<NodoDistancia> {
        String id;
        double distancia;
        
        NodoDistancia(String id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
        
        @Override
        public int compareTo(NodoDistancia otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }
    
    // =========================================================================
    // MÉTODOS DE ACCESO
    // =========================================================================
    
    public Map<String, Integer> getEvidenciaAcumulada() {
        return new HashMap<>(evidenciaAcumulada);
    }
    
    public void limpiarEvidencia() {
        evidenciaAcumulada.clear();
    }
}
