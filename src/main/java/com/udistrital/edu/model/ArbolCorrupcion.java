/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.EstadoPolitico;
import com.udistrital.edu.model.Constantes.NivelJerarquico;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
/**
 * Estructura de árbol n-ario para representar la jerarquía de corrupción
 * Implementa el patrón Singleton para asegurar una única instancia
 */
public class ArbolCorrupcion {
    private static ArbolCorrupcion instancia;
    private Politico raiz;
    private Map<String, Politico> registroPoliticos; // HashMap para acceso O(1)
    
    private ArbolCorrupcion() {
        this.registroPoliticos = new HashMap<>();
    }
    
    // Patrón Singleton
    public static synchronized ArbolCorrupcion getInstance() {
        if (instancia == null) {
            instancia = new ArbolCorrupcion();
        }
        return instancia;
    }
    
    // Operaciones del árbol
    public boolean insertarPolitico(Politico padre, Politico nuevoPolitico) {
        if (nuevoPolitico == null) return false;
        
        // Si es el primer político (raíz)
        if (raiz == null) {
            raiz = nuevoPolitico;
            registroPoliticos.put(nuevoPolitico.getId(), nuevoPolitico);
            return true;
        }
        
        // Si no hay padre especificado, no se puede insertar
        if (padre == null) return false;
        
        // Verificar que el padre existe en el árbol
        if (!registroPoliticos.containsKey(padre.getId())) return false;
        
        // Verificar que el nuevo político no existe ya
        if (registroPoliticos.containsKey(nuevoPolitico.getId())) return false;
        
        // Agregar al árbol
        padre.agregarSubordinado(nuevoPolitico);
        registroPoliticos.put(nuevoPolitico.getId(), nuevoPolitico);
        return true;
    }
    
    public boolean eliminarPolitico(String idPolitico) {
        Politico politico = registroPoliticos.get(idPolitico);
        if (politico == null) return false;
        
        // Si es la raíz
        if (politico == raiz) {
            // Si tiene subordinados, promover al primero como nueva raíz
            if (!politico.getSubordinados().isEmpty()) {
                raiz = politico.getSubordinados().get(0);
                raiz.getPadre().removerSubordinado(raiz);
                
                // Reasignar subordinados restantes
                for (int i = 1; i < politico.getSubordinados().size(); i++) {
                    Politico subordinado = politico.getSubordinados().get(i);
                    raiz.agregarSubordinado(subordinado);
                }
            } else {
                raiz = null;
            }
        } else {
            // Remover del padre
            if (politico.getPadre() != null) {
                politico.getPadre().removerSubordinado(politico);
                
                // Reasignar subordinados al padre
                for (Politico subordinado : politico.getSubordinados()) {
                    politico.getPadre().agregarSubordinado(subordinado);
                }
            }
        }
        
        registroPoliticos.remove(idPolitico);
        return true;
    }
    
    public Politico buscarPolitico(String id) {
        return registroPoliticos.get(id);
    }
    
    // Recorridos del árbol
    public void recorridoPreorden(Politico nodo, List<Politico> resultado) {
        if (nodo == null) return;
        
        resultado.add(nodo);
        for (Politico subordinado : nodo.getSubordinados()) {
            recorridoPreorden(subordinado, resultado);
        }
    }
    
    public void recorridoPostorden(Politico nodo, List<Politico> resultado) {
        if (nodo == null) return;
        
        for (Politico subordinado : nodo.getSubordinados()) {
            recorridoPostorden(subordinado, resultado);
        }
        resultado.add(nodo);
    }
    
    public List<Politico> obtenerTodosLosPoliticos() {
        List<Politico> todos = new ArrayList<>();
        if (raiz != null) {
            recorridoPreorden(raiz, todos);
        }
        return todos;
    }
    
    public List<Politico> obtenerPoliticosPorNivel(NivelJerarquico nivel) {
        return obtenerTodosLosPoliticos().stream()
                .filter(p -> p.getNivelJerarquico() == nivel)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Politico> obtenerPoliticosPorEstado(EstadoPolitico estado) {
        return obtenerTodosLosPoliticos().stream()
                .filter(p -> p.getEstado() == estado)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Cálculos del árbol completo
    public int calcularInfluenciaTotal() {
        return raiz != null ? raiz.calcularInfluenciaTotal() : 0;
    }
    
    public int calcularRiquezaTotal() {
        return raiz != null ? raiz.calcularRiquezaTotal() : 0;
    }
    
    public double calcularRiesgoPromedio() {
        return raiz != null ? raiz.calcularRiesgoPromedio() : 0.0;
    }
    
    public int obtenerTamanioRed() {
        return registroPoliticos.size();
    }
    
    // Getters
    public Politico getRaiz() { return raiz; }
    
    public Map<String, Politico> getRegistroPoliticos() {
        return new HashMap<>(registroPoliticos);
    }
    
    // Método para reiniciar (útil para testing)
    public void reiniciar() {
        raiz = null;
        registroPoliticos.clear();
    }
}
