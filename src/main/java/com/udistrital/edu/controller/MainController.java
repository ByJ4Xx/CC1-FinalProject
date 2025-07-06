/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.controller;

import com.udistrital.edu.model.*;
import com.udistrital.edu.view.MainView;

/**
 *
 * @author USUARIO
 */
public class MainController {
    private Juego juego;
    private MainView mainView;
    private PoliticoController politicoController;
    private AccionController accionController;

    public MainController(Juego juego) {
        this.juego = juego;
        this.politicoController = new PoliticoController(juego);
        this.accionController = new AccionController(juego);
        this.mainView = new MainView(this);
        
        iniciar();
    }

    private void iniciar() {
        mainView.mostrar();
        actualizarVistas();
    }

    public void avanzarTurno() {
        juego.avanzarTurno();
        actualizarVistas();
    }

    public void actualizarVistas() {
        mainView.actualizar(juego);
        politicoController.actualizarVista();
    }

    // Getters para sub-controladores
    public PoliticoController getPoliticoController() {
        return politicoController;
    }

    public AccionController getAccionController() {
        return accionController;
    }

    public Juego getJuego() {
        return juego;
    }
}
