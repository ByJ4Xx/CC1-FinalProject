/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.view;

/**
 *
 * @author USUARIO
 */


import com.udistrital.edu.model.Conexion;
import com.udistrital.edu.model.GrafoConexiones;

import javax.swing.*;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.BorderLayout;
import java.awt.Color;

public class GrafoView extends JPanel {
    private GrafoConexiones grafoConexiones;
    private mxGraphComponent graphComponent;

    public GrafoView(GrafoConexiones grafoConexiones) {
        this.grafoConexiones = grafoConexiones;
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Convertir nuestro grafo a un formato que JGraphX pueda manejar
        Graph<String, Conexion> graph = new DefaultDirectedWeightedGraph<>(Conexion.class);
        
        // Agregar nodos
        grafoConexiones.getEntidades().forEach(graph::addVertex);
        
        // Agregar aristas
        grafoConexiones.getEntidades().forEach(entidad -> {
            grafoConexiones.obtenerConexiones(entidad).forEach(conexion -> {
                graph.addEdge(conexion.getOrigen(), conexion.getDestino(), conexion);
                graph.setEdgeWeight(conexion, Math.abs(conexion.getPeso()));
            });
        });
        
        // Crear adaptador para JGraphX
        JGraphXAdapter<String, Conexion> graphAdapter = new JGraphXAdapter<>(graph);
        
        // Configurar visualización
        graphComponent = new mxGraphComponent(graphAdapter);
        graphComponent.setConnectable(false);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        
        // Aplicar layout circular
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        
        add(graphComponent, BorderLayout.CENTER);
        
    }

    public void actualizar() {
        // Implementar actualización del grafo si es necesario
    }
}
