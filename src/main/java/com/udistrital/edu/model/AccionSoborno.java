/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;


/**
 * Acción específica para deshacer sobornos
 */
public class AccionSoborno extends AccionDeshacer {
    private final String idPoliticoSobornado;
    private final String idPoliticoPadre;
    private final int costoSoborno;
    private final ArbolCorrupcion arbol;
    
    public AccionSoborno(String idPoliticoSobornado, String idPoliticoPadre, 
                        int costoSoborno, ArbolCorrupcion arbol) {
        super("Soborno a " + idPoliticoSobornado);
        this.idPoliticoSobornado = idPoliticoSobornado;
        this.idPoliticoPadre = idPoliticoPadre;
        this.costoSoborno = costoSoborno;
        this.arbol = arbol;
    }
    
    @Override
    public boolean deshacer() {
        // Remover al político sobornado del árbol
        return arbol.eliminarPolitico(idPoliticoSobornado);
    }
    
    @Override
    public boolean rehacer() {
        // Esto requeriría recrear el político, más complejo de implementar
        return false;
    }
    
    public int getCostoSoborno() { return costoSoborno; }
}
