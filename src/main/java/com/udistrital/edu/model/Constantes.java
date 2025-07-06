/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

// =============================================================================
// ENUMS Y CONSTANTES
// =============================================================================

/**
 * Estados posibles de un político en el juego
 */

public class Constantes {
    // Enums como clases anidadas públicas
    public enum EstadoPolitico {
        ACTIVO("Activo"),
        BAJO_SOSPECHA("Bajo Sospecha"),
        INVESTIGADO("Investigado"),
        QUEMADO("Quemado"),
        ENCARCELADO("Encarcelado"),
        TESTIGO_PROTEGIDO("Testigo Protegido"),
        EN_UCI("En UCI");
        
        private final String descripcion;
        
        EstadoPolitico(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }

    public enum NivelJerarquico {
        ALCALDE(1, "Alcalde"),
        GOBERNADOR(2, "Gobernador"),
        CONGRESISTA(3, "Congresista"),
        MINISTRO(4, "Ministro"),
        JUEZ(3, "Juez"),
        EMPRESARIO(2, "Empresario"),
        CONTRATISTA(1, "Contratista");
        
        private final int poder;
        private final String titulo;
        
        NivelJerarquico(int poder, String titulo) {
            this.poder = poder;
            this.titulo = titulo;
        }
        
        public int getPoder() { return poder; }
        public String getTitulo() { return titulo; }
    }

    public enum HabilidadEspecial {
        BLOQUEAR_INVESTIGACION("Bloquear Investigación"),
        COMPRA_VOTOS("Compra de Votos"),
        CONTRATACION_AMANADA("Contratación Amañada"),
        MANIPULACION_MEDIATICA("Manipulación Mediática"),
        CONTRATO_SICARIOS("Contrato de Sicarios"),
        LAVADO_DINERO("Lavado de Dinero"),
        FALSIFICACION_PRUEBAS("Falsificación de Pruebas");
        
        private final String descripcion;
        
        HabilidadEspecial(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() { return descripcion; }
    }

    public enum TipoEvento {
        MEGA_OPERACION_ANTICORRUPCION("Mega-Operación AntiCorrupción"),
        FILTRACION_PRENSA("Filtración a la Prensa"),
        TRAICION_DOLOROSA("Traición Dolorosa"),
        ELECCIONES_REGIONALES("Elecciones Regionales"),
        CAMBIO_FISCAL_GENERAL("Cambio de Fiscal General"),
        PRESION_INTERNACIONAL("Presión Internacional");
        
        private final String descripcion;
        
        TipoEvento(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() { return descripcion; }
    }
}

