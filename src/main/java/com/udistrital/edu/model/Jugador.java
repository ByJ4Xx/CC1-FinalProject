package com.udistrital.edu.model;

public class Jugador {
    private int dineroSucio;
    private int dineroLimpio;
    private int influencia;
    private int capitalPolitico;
    private int nivelSospecha;
    private int reputacionPublica;

    public Jugador (Builder builder){
        this.dineroSucio = builder.dineroSucio;
        this.dineroLimpio = builder.dineroLimpio;
        this.influencia = builder.influencia;
        this.capitalPolitico = builder.capitalPolitico;
        this.nivelSospecha = builder.nivelSospecha;
        this.reputacionPublica = builder.reputacionPublica;
    }

    public static class Builder {
        private int dineroSucio = 0;
        private int dineroLimpio = 0;
        private int influencia = 0;
        private int capitalPolitico = 0;
        private int nivelSospecha = 0;
        private int reputacionPublica = 0;


        public Builder dineroSucio(int dineroSucio){
            this.dineroSucio = Math.max(0,dineroSucio);
            return this;
        }

        public Builder dineroLimpio (int dineroLimpio){
            this.dineroLimpio = Math.max(0,dineroLimpio);
            return this;
        }

        public Builder influencia (int influencia){
            this.influencia = Math.max(0,influencia);
            return this;
        }

        public Builder capitalPolitico (int capitalPolitico){
            this.capitalPolitico = Math.max(0,capitalPolitico);
            return this;
        }

        public Builder nivelSospecha (int nivelSospecha){
            this.nivelSospecha = Math.max(0,nivelSospecha);
            return this;
        }

        public Builder reputacionPublica (int reputacionPublica){
            this.reputacionPublica = Math.max(0,reputacionPublica);
            return this;
        }

        public Jugador build (){return new Jugador(this);}
    }

    public int getDineroSucio() {
        return dineroSucio;
    }

    public void setDineroSucio(int dineroSucio) {
        this.dineroSucio = dineroSucio;
    }

    public int getDineroLimpio() {
        return dineroLimpio;
    }

    public void setDineroLimpio(int dineroLimpio) {
        this.dineroLimpio = dineroLimpio;
    }

    public int getInfluencia() {
        return influencia;
    }

    public void setInfluencia(int influencia) {
        this.influencia = influencia;
    }

    public int getCapitalPolitico() {
        return capitalPolitico;
    }

    public void setCapitalPolitico(int capitalPolitico) {
        this.capitalPolitico = capitalPolitico;
    }

    public int getNivelSospecha() {
        return nivelSospecha;
    }

    public void setNivelSospecha(int nivelSospecha) {
        this.nivelSospecha = nivelSospecha;
    }

    public int getReputacionPublica() {
        return reputacionPublica;
    }

    public void setReputacionPublica(int reputacionPublica) {
        this.reputacionPublica = reputacionPublica;
    }
}
