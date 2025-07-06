/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.TipoEvento;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author USUARIO
 */
/**
 * Cola de prioridad para manejar eventos del juego
 * Los eventos con mayor prioridad se procesan primero
 */
public class ColaEventos {
    private PriorityQueue<EventoJuego> colaEventos;
    private Map<String, EventoJuego> registroEventos;
    
    public ColaEventos() {
        // Comparador: mayor prioridad primero, luego por tiempo de creación
        this.colaEventos = new PriorityQueue<>((e1, e2) -> {
            int comparacionPrioridad = Integer.compare(e2.getPrioridad(), e1.getPrioridad());
            if (comparacionPrioridad != 0) {
                return comparacionPrioridad;
            }
            return Long.compare(e1.getTiempoCreacion(), e2.getTiempoCreacion());
        });
        this.registroEventos = new HashMap<>();
    }
    
    public boolean agregarEvento(EventoJuego evento) {
        if (evento == null || registroEventos.containsKey(evento.getId())) {
            return false;
        }
        
        colaEventos.offer(evento);
        registroEventos.put(evento.getId(), evento);
        return true;
    }
    
    public EventoJuego procesarSiguienteEvento() {
        EventoJuego evento = colaEventos.poll();
        if (evento != null) {
            evento.setProcesado(true);
        }
        return evento;
    }
    
    public EventoJuego verSiguienteEvento() {
        return colaEventos.peek();
    }
    
    public boolean removerEvento(String idEvento) {
        EventoJuego evento = registroEventos.get(idEvento);
        if (evento != null && !evento.isProcesado()) {
            colaEventos.remove(evento);
            registroEventos.remove(idEvento);
            return true;
        }
        return false;
    }
    
    public List<EventoJuego> obtenerEventosPendientes() {
        return colaEventos.stream()
                .filter(e -> !e.isProcesado())
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<EventoJuego> obtenerEventosPorTipo(TipoEvento tipo) {
        return registroEventos.values().stream()
                .filter(e -> e.getTipo() == tipo)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public int obtenerTamanoCola() {
        return colaEventos.size();
    }
    
    public boolean estaVacia() {
        return colaEventos.isEmpty();
    }
    
    public void limpiarEventosProcesados() {
        registroEventos.entrySet().removeIf(entry -> entry.getValue().isProcesado());
    }
}
