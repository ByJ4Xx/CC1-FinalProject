/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.controller;

import com.udistrital.edu.model.Juego;

/**
 *
 * @author USUARIO
 */
public class AccionController {
    private Juego juego;

    public AccionController(Juego juego) {
        this.juego = juego;
    }

    public boolean neutralizarAmenaza(String idAmenaza, int costoDinero, int costoInfluencia) {
        return juego.neutralizarAmenaza(idAmenaza, costoDinero, costoInfluencia);
    }

    public boolean operacionEncubrimiento(String idPolitico, int costoDinero) {
        return juego.operacionEncubrimiento(idPolitico, costoDinero);
    }

    public boolean lavarDinero(int cantidad) {
        // Implementación pendiente para Etapa 5
        return false;
    }
}
