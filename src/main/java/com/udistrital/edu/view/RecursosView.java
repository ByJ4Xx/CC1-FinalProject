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
import javax.swing.*;
import java.awt.*;

public class RecursosView extends JPanel {
    private Juego juego;
    private JLabel lblDineroSucio;
    private JLabel lblDineroLimpio;
    private JLabel lblInfluencia;
    private JLabel lblCapitalPolitico;
    private JLabel lblSospecha;
    private JLabel lblReputacion;
    private JLabel lblTurno;

    public RecursosView(Juego juego) {
        this.juego = juego;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Recursos y Estado"));
        setPreferredSize(new Dimension(300, 400));
        
        inicializarComponentes();
        actualizar(juego);
    }

    private void inicializarComponentes() {
        lblDineroSucio = crearEtiquetaRecurso();
        lblDineroLimpio = crearEtiquetaRecurso();
        lblInfluencia = crearEtiquetaRecurso();
        lblCapitalPolitico = crearEtiquetaRecurso();
        lblSospecha = crearEtiquetaRecurso();
        lblReputacion = crearEtiquetaRecurso();
        lblTurno = crearEtiquetaRecurso();
        
        add(new JLabel("Estado Actual:"));
        add(lblTurno);
        add(Box.createVerticalStrut(10));
        add(new JLabel("Recursos:"));
        add(lblDineroSucio);
        add(lblDineroLimpio);
        add(lblInfluencia);
        add(Box.createVerticalStrut(10));
        add(new JLabel("Estadísticas:"));
        add(lblCapitalPolitico);
        add(lblSospecha);
        add(lblReputacion);
    }

    private JLabel crearEtiquetaRecurso() {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public void actualizar(Juego juego) {
        lblDineroSucio.setText(String.format("Dinero Sucio: $%,d", juego.getDineroSucio()));
        lblDineroLimpio.setText(String.format("Dinero Limpio: $%,d", juego.getDineroLimpio()));
        lblInfluencia.setText(String.format("Influencia: %,d", juego.getInfluencia()));
        lblCapitalPolitico.setText(String.format("Capital Político: %,d", juego.getCapitalPolitico()));
        lblSospecha.setText(String.format("Nivel Sospecha: %d%%", juego.getNivelSospecha()));
        lblReputacion.setText(String.format("Reputación Pública: %d%%", juego.getReputacionPublica()));
        lblTurno.setText(String.format("Turno %d", juego.getTurnoActual()));
        
        actualizarColores();
    }

    private void actualizarColores() {
        lblSospecha.setForeground(juego.getNivelSospecha() > 70 ? Color.RED : 
                                 juego.getNivelSospecha() > 40 ? Color.ORANGE : Color.GREEN);
        
        lblReputacion.setForeground(juego.getReputacionPublica() < 30 ? Color.RED : 
                                   juego.getReputacionPublica() < 60 ? Color.ORANGE : Color.GREEN);
    }
}
