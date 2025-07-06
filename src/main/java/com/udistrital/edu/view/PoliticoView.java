/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.view;

/**
 *
 * @author USUARIO
 */
import com.udistrital.edu.controller.PoliticoController;
import com.udistrital.edu.model.ArbolCorrupcion;
import com.udistrital.edu.model.Constantes.NivelJerarquico;
import com.udistrital.edu.model.Politico;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Arrays;

public class PoliticoView extends JPanel {
    private PoliticoController controller;
    private JTree arbolPoliticos;
    private JPanel detallePanel;
    private JTextArea detalleTextArea;

    public PoliticoView(PoliticoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Árbol de políticos
        arbolPoliticos = new JTree();
        arbolPoliticos.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbolPoliticos.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof Politico) {
                mostrarDetalles((Politico) node.getUserObject());
            }
        });
        
        JScrollPane treeScroll = new JScrollPane(arbolPoliticos);
        
        // Panel de detalles
        detallePanel = new JPanel(new BorderLayout());
        detalleTextArea = new JTextArea();
        detalleTextArea.setEditable(false);
        detallePanel.add(new JScrollPane(detalleTextArea), BorderLayout.CENTER);
        detallePanel.setPreferredSize(new Dimension(300, 400));
        
        // Botón para sobornar
        JButton btnSobornar = new JButton("Sobornar Nuevo Político");
        btnSobornar.addActionListener(e -> mostrarDialogoSoborno());
        
        // Organizar layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detallePanel);
        splitPane.setDividerLocation(600);
        
        add(splitPane, BorderLayout.CENTER);
        add(btnSobornar, BorderLayout.SOUTH);
    }

    public void mostrarDetalles(Politico politico) {
        String detalles = String.format(
            "Nombre: %s\nNivel: %s\nEstado: %s\n\n" +
            "Lealtad: %d\nAmbición: %d\nRiesgo: %d\n\n" +
            "Costo Soborno: $%,d\nAporte Influencia: %d\nAporte Riqueza: $%,d",
            politico.getNombre(), politico.getNivelJerarquico().getTitulo(),
            politico.getEstado().getDescripcion(), politico.getNivelLealtad(),
            politico.getNivelAmbicion(), politico.getRiesgoExposicion(),
            politico.getCostoSoborno(), politico.getAportePorInfluencia(),
            politico.getAportePorRiqueza()
        );
        
        detalleTextArea.setText(detalles);
    }

    public void actualizarArbol(ArbolCorrupcion arbol) {
        System.out.println("DEBUG - Políticos en el árbol:");
    arbol.obtenerTodosLosPoliticos().forEach(p -> 
        System.out.println(p.getId() + " - " + p.getNombre()));
        Politico raiz = arbol.getRaiz();
        if (raiz != null) {
            DefaultMutableTreeNode rootNode = construirNodo(raiz);
            arbolPoliticos.setModel(new DefaultTreeModel(rootNode));
            expandirTodo();
        }
    }

    private DefaultMutableTreeNode construirNodo(Politico politico) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(politico);
        for (Politico subordinado : politico.getSubordinados()) {
            node.add(construirNodo(subordinado));
        }
        return node;
    }

    private void expandirTodo() {
        for (int i = 0; i < arbolPoliticos.getRowCount(); i++) {
            arbolPoliticos.expandRow(i);
        }
    }

    private void mostrarDialogoSoborno() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) arbolPoliticos.getLastSelectedPathComponent();
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof Politico)) {
            JOptionPane.showMessageDialog(this, "Seleccione un político para sobornar en su nombre", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Politico padre = (Politico) selectedNode.getUserObject();
        String[] opciones = Arrays.stream(NivelJerarquico.values())
                                .map(NivelJerarquico::getTitulo)
                                .toArray(String[]::new);
        
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el nivel del político a sobornar:",
            "Sobornar Nuevo Político",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        if (seleccion != null) {
            NivelJerarquico nivel = NivelJerarquico.valueOf(seleccion.toUpperCase());
            boolean exito = controller.intentarSoborno(nivel, padre.getId());
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Soborno exitoso!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "El soborno ha fallado", "Fallo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
