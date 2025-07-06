/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.TipoEvento;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author USUARIO
 */
public class EventoDinamico {
    private final Juego juego;
    private final Random random;
    
    // Probabilidades base de eventos
    private static final double PROB_BASE_MEGA_OPERACION = 0.05;
    private static final double PROB_BASE_FILTRACION = 0.08;
    private static final double PROB_BASE_TRAICION = 0.06;
    
    public EventoDinamico(Juego juego) {
        this.juego = juego;
        this.random = new Random();
    }
    
    /**
     * Genera eventos dinámicos basados en el estado del juego
     */
    public void generarEventosDinamicos() {
        // Calcular probabilidades ajustadas
        double probMegaOperacion = calcularProbabilidadMegaOperacion();
        double probFiltracion = calcularProbabilidadFiltracion();
        double probTraicion = calcularProbabilidadTraicion();
        
        // Generar eventos según probabilidades
        if (random.nextDouble() < probMegaOperacion) {
            generarMegaOperacion();
        }
        
        if (random.nextDouble() < probFiltracion) {
            generarFiltracion();
        }
        
        if (random.nextDouble() < probTraicion) {
            generarTraicionDolorosa();
        }
        
        // Eventos periódicos fijos
        if (juego.getTurnoActual() % 5 == 0) {
            generarEventoPeriodico();
        }
    }
    
    private double calcularProbabilidadMegaOperacion() {
        double modSospecha = juego.getNivelSospecha() / 100.0;
        double modRed = juego.getArbolCorrupcion().calcularRiesgoPromedio() / 100.0;
        return PROB_BASE_MEGA_OPERACION * (1.0 + modSospecha + modRed);
    }
    
    private double calcularProbabilidadFiltracion() {
        double modRiesgo = juego.getArbolCorrupcion().calcularRiesgoPromedio() / 50.0;
        return PROB_BASE_FILTRACION * (1.0 + modRiesgo);
    }
    
    private double calcularProbabilidadTraicion() {
        long traidoresPotenciales = juego.getArbolCorrupcion().obtenerTodosLosPoliticos().stream()
            .filter(p -> p.getNivelLealtad() < 40 && p.getNivelAmbicion() > 70)
            .count();
            
        return PROB_BASE_TRAICION * (1.0 + (traidoresPotenciales * 0.1));
    }
    
    private void generarMegaOperacion() {
        EventoJuego evento = new EventoJuego(
            "mega-op-" + juego.getTurnoActual(),
            TipoEvento.MEGA_OPERACION_ANTICORRUPCION,
            "¡MEGA OPERACIÓN ANTICORRUPCIÓN EN CURSO! Todos los políticos con alto riesgo están siendo investigados",
            15 // Alta prioridad
        );
        
        // Añadir parámetros adicionales
        evento.agregarParametro("aumento_sospecha", 20);
        evento.agregarParametro("duracion", 3); // turnos
        
        juego.getColaEventos().agregarEvento(evento);
    }
    
    private void generarFiltracion() {
        Optional<Politico> filtrador = juego.getArbolCorrupcion().obtenerTodosLosPoliticos().stream()
            .filter(p -> p.isActivo() && p.getRiesgoExposicion() > 60)
            .max(Comparator.comparingInt(Politico::getRiesgoExposicion));
            
        if (filtrador.isPresent()) {
            Politico p = filtrador.get();
            EventoJuego evento = new EventoJuego(
                "filt-" + p.getId() + "-" + juego.getTurnoActual(),
                TipoEvento.FILTRACION_PRENSA,
                String.format("%s ha filtrado información comprometedora a la prensa", p.getNombre()),
                12
            );
            
            evento.agregarParametro("politico", p.getId());
            evento.agregarParametro("perdida_reputacion", 15);
            
            juego.getColaEventos().agregarEvento(evento);
        }
    }
    
    private void generarTraicionDolorosa() {
        Optional<Politico> traidor = juego.getArbolCorrupcion().obtenerTodosLosPoliticos().stream()
            .filter(p -> p.isActivo())
            .max(Comparator.comparingInt(p -> p.getNivelAmbicion() - p.getNivelLealtad()));
            
        if (traidor.isPresent()) {
            Politico p = traidor.get();
            EventoJuego evento = new EventoJuego(
                "traicion-" + p.getId() + "-" + juego.getTurnoActual(),
                TipoEvento.TRAICION_DOLOROSA,
                String.format("%s está considerando traicionarte (Ambición: %d, Lealtad: %d)", 
                             p.getNombre(), p.getNivelAmbicion(), p.getNivelLealtad()),
                14
            );
            
            evento.agregarParametro("politico", p.getId());
            evento.agregarParametro("riesgo", p.getRiesgoExposicion());
            
            juego.getColaEventos().agregarEvento(evento);
        }
    }
    
    private void generarEventoPeriodico() {
        TipoEvento[] eventosPeriodicos = {
            TipoEvento.ELECCIONES_REGIONALES,
            TipoEvento.CAMBIO_FISCAL_GENERAL,
            TipoEvento.PRESION_INTERNACIONAL
        };
        
        TipoEvento tipo = eventosPeriodicos[random.nextInt(eventosPeriodicos.length)];
        String id = "periodico-" + tipo.name() + "-" + juego.getTurnoActual();
        
        EventoJuego evento = new EventoJuego(
            id,
            tipo,
            "Evento periódico: " + tipo.getDescripcion(),
            8
        );
        
        juego.getColaEventos().agregarEvento(evento);
    }
}