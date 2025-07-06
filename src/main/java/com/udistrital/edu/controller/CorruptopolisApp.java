/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.controller;

import com.udistrital.edu.model.Juego;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author USUARIO
 */
public class CorruptopolisApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Crear modelo y controlador principal
            Juego juego = new Juego();
            new MainController(juego);
        });
    }
}
