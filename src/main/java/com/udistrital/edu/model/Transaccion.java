package com.udistrital.edu.model;

    public class Transaccion {
        String id;
        double monto;
        private static final double RIESGO_BASE = 0.10; // 10% base
        private static final double ESCALA_MONTO = 0.0002; // 0.02% por unidad de dinero
        private static final double ESCALA_SOSPECHA = 0.005; // 0.5% por punto de sospecha

        public Transaccion(String id, int monto) {
            this.id = id;
            this.monto = monto;
        }

        public double calcularRiesgoTransaccion(double monto, int nivelSospecha) {
                // Riesgo base + riesgo por monto + riesgo por sospecha
                double riesgo = RIESGO_BASE
                        + (ESCALA_MONTO * monto)
                        + (ESCALA_SOSPECHA * nivelSospecha);

                // Limitar entre 10% y 90% para evitar extremos
                return Math.min(Math.max(riesgo, 0.10), 0.90);
            }
    }
