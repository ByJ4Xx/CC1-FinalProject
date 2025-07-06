/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.view;

/**
 *
 * @author USUARIO
 */

import com.udistrital.edu.model.Juego;
import com.udistrital.edu.controller.MainController;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private MainController controller;
    private JPanel mainPanel;
    private PoliticoView politicoView;
    private RecursosView recursosView;
    private JButton btnAvanzarTurno;

    public MainView(MainController controller) {
        this.controller = controller;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Corruptópolis - El Ascenso y Caída en el Laberinto del Poder");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Crear vistas secundarias
        politicoView = new PoliticoView(controller.getPoliticoController());
        recursosView = new RecursosView(controller.getJuego());
        
        // Panel de controles
        JPanel controlPanel = new JPanel();
        btnAvanzarTurno = new JButton("Avanzar Turno");
        btnAvanzarTurno.addActionListener(e -> controller.avanzarTurno());
        
        controlPanel.add(btnAvanzarTurno);
        
        // Organizar layout
        mainPanel.add(politicoView, BorderLayout.CENTER);
        mainPanel.add(recursosView, BorderLayout.EAST);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    public void mostrar() {
        setVisible(true);
    }

    public void actualizar(Juego juego) {
        politicoView.actualizarArbol(juego.getArbolCorrupcion());
        recursosView.actualizar(juego);
        repaint();
    }
}
