/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.controller;

import com.udistrital.edu.model.*;
import com.udistrital.edu.model.Constantes.NivelJerarquico;
import com.udistrital.edu.view.PoliticoView;

/**
 *
 * @author USUARIO
 */
public class PoliticoController {
    private Juego juego;
    private PoliticoView politicoView;

    public PoliticoController(Juego juego) {
        this.juego = juego;
        this.politicoView = new PoliticoView(this);
    }

    public void seleccionarPolitico(String idPolitico) {
        Politico politico = juego.getArbolCorrupcion().buscarPolitico(idPolitico);
        if (politico != null) {
            politicoView.mostrarDetalles(politico);
        }
    }

    public void actualizarVista() {
        politicoView.actualizarArbol(juego.getArbolCorrupcion());
    }

    public boolean intentarSoborno(NivelJerarquico nivel, String idPadre) {
        Politico padre = juego.getArbolCorrupcion().buscarPolitico(idPadre);
        if (padre != null) {
            return juego.intentarSoborno(nivel, padre) != null;
        }
        return false;
    }
}
