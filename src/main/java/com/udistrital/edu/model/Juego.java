/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.udistrital.edu.model;

import com.udistrital.edu.model.Constantes.EstadoPolitico;
import static com.udistrital.edu.model.Constantes.EstadoPolitico.ACTIVO;
import static com.udistrital.edu.model.Constantes.EstadoPolitico.BAJO_SOSPECHA;
import static com.udistrital.edu.model.Constantes.EstadoPolitico.INVESTIGADO;
import com.udistrital.edu.model.Constantes.NivelJerarquico;
import com.udistrital.edu.model.Constantes.TipoEvento;

import java.util.*;

/**
 *
 * @author USUARIO
 */
public class Juego {

    private final ArbolCorrupcion arbolCorrupcion;
    private final GrafoConexiones grafoConexiones;
    private final ColaEventos colaEventos;
    private final RegistroEspecializado registro;
    private final PilaDeshacer pilaDeshacer;
    private final SistemaJusticia sistemaJusticia;
    private final EventoDinamico eventoDinamico;
    private Jugador jugador;

    // Recursos del jugador
    /*private int dineroSucio;
    private int dineroLimpio;
    private int influencia;
    private int capitalPolitico;
    private int nivelSospecha;
    private int reputacionPublica;*/
    private int turnoActual;

    // Constantes del juego
    private static final int SOSPECHA_MAXIMA = 100;
    private static final int REPUTACION_MAXIMA = 100;
    private static final int COSTO_BASE_SOBORNO = 1000;
    private static final int INFLUENCIA_BASE_SOBORNO = 50;

    public Juego() {
        this.arbolCorrupcion = ArbolCorrupcion.getInstance();
        this.grafoConexiones = new GrafoConexiones();
        this.colaEventos = new ColaEventos();
        this.registro = new RegistroEspecializado();
        this.pilaDeshacer = new PilaDeshacer(20);
        this.sistemaJusticia = new SistemaJusticia(grafoConexiones, arbolCorrupcion, registro);
        this.eventoDinamico = new EventoDinamico(this);

        // Inicializar recursos del jugador
        jugador = new Jugador.Builder().dineroSucio(50000)
                .dineroLimpio(10000)
                .influencia(100)
                .capitalPolitico(0)
                .nivelSospecha(10)
                .reputacionPublica(70).build();

        this.turnoActual = 1;

        /*this.dineroSucio = 50000;
        this.dineroLimpio = 10000;
        this.influencia = 100;
        this.capitalPolitico = 0;
        this.nivelSospecha = 10;
        this.reputacionPublica = 70;
        */
        
        // Crear político raíz si el árbol está vacío
        if (arbolCorrupcion.getRaiz() == null) {
            crearPoliticoInicial();
        }
    }

    // =========================================================================
    // MÉTODOS PRINCIPALES DEL JUEGO
    // =========================================================================
    /**
     * Avanza el juego un turno completo
     */
    public void avanzarTurno() {
        System.out.println("\n=== INICIANDO TURNO " + turnoActual + " ===");

        // 1. Generar eventos dinámicos
        eventoDinamico.generarEventosDinamicos();

        // 2. Procesar eventos pendientes
        procesarEventos();

        // 3. Ejecutar investigaciones de la IA
        sistemaJusticia.ejecutarInvestigacionesTurno();

        // 4. Actualizar recursos del jugador
        actualizarRecursos();

        // 5. Actualizar estados de los políticos
        actualizarEstadosPoliticos();

        // 6. Actualizar capital político
        actualizarCapitalPolitico();

        turnoActual++;
        System.out.println("=== FIN DEL TURNO " + (turnoActual - 1) + " ===");
    }

    private void crearPoliticoInicial() {
        Politico presidente = new Politico.Builder("pre-001", "El Presidente", NivelJerarquico.MINISTRO)
                .costoSoborno(50000)
                .aportePorInfluencia(30)
                .aportePorRiqueza(500)
                .nivelLealtad(80)
                .nivelAmbicion(60)
                .riesgoExposicion(30)
                .build();

        arbolCorrupcion.insertarPolitico(null, presidente);

        // Opcional: crear algunos subordinados iniciales
        Politico ministro = new Politico.Builder("min-001", "Ministro Hacienda", NivelJerarquico.MINISTRO)
                .costoSoborno(30000)
                .nivelLealtad(70)
                .build();

        arbolCorrupcion.insertarPolitico(presidente, ministro);
    }

    // =========================================================================
    // SISTEMA DE RECURSOS
    // =========================================================================
    /**
     * Actualiza los recursos del jugador basado en su red de corrupción
     */
    private void actualizarRecursos() {
        // Calcular ganancias de la red
        int gananciaDinero = arbolCorrupcion.calcularRiquezaTotal();
        int gananciaInfluencia = arbolCorrupcion.calcularInfluenciaTotal();

        // Aplicar modificadores por sospecha
        double modificadorSospecha = 1.0 - (jugador.getNivelSospecha() / 200.0);
        gananciaDinero = (int) (gananciaDinero * modificadorSospecha);
        gananciaInfluencia = (int) (gananciaInfluencia * modificadorSospecha);

        // Actualizar recursos
        jugador.setDineroSucio(jugador.getDineroSucio()+gananciaDinero);
        jugador.setInfluencia(jugador.getInfluencia()+gananciaInfluencia);

        System.out.printf("Recursos actualizados: +$%,d (sucio), +%d influencia%n",
                gananciaDinero, gananciaInfluencia);
    }

    /**
     * Actualiza el capital político del jugador
     */
    private void actualizarCapitalPolitico() {
        int nuevoCapital = (jugador.getDineroSucio() / 1000) + (jugador.getInfluencia() * 10)
                + ((REPUTACION_MAXIMA - jugador.getNivelSospecha()) * 5);
        jugador.setCapitalPolitico(nuevoCapital);
    }

    // =========================================================================
    // SISTEMA DE SOBORNOS
    // =========================================================================
    /**
     * Intenta sobornar a un nuevo político para unirse a la red
     *
     * @param nivel Nivel jerárquico del político a reclutar
     * @return Politico reclutado o null si falló
     */
    public Politico intentarSoborno(NivelJerarquico nivel, Politico padre) {
        if (padre == null || !arbolCorrupcion.getRegistroPoliticos().containsKey(padre.getId())) {
            System.out.println("Error: Padre no válido o no en la red");
            return null;
        }

        // Calcular costo base del soborno
        int costo = calcularCostoSoborno(nivel);
        int costoInfluencia = INFLUENCIA_BASE_SOBORNO * nivel.getPoder();

        // Verificar recursos
        if (jugador.getDineroSucio() < costo || jugador.getInfluencia() < costoInfluencia) {
            System.out.println("Recursos insuficientes para el soborno");
            return null;
        }

        // Crear político potencial
        Politico nuevo = generarPoliticoAleatorio(nivel);

        // Calcular probabilidad de éxito
        double probabilidad = calcularProbabilidadExito(padre, nuevo);
        boolean exito = Math.random() < probabilidad;

        // Registrar acción en pila de deshacer
        pilaDeshacer.agregarAccion(new AccionSoborno(nuevo.getId(), padre.getId(), costo, arbolCorrupcion));

        if (exito) {
            // Éxito: agregar al árbol
            jugador.setDineroSucio(jugador.getDineroSucio()-costo);
            jugador.setInfluencia(jugador.getInfluencia()-costoInfluencia);
            arbolCorrupcion.insertarPolitico(padre, nuevo);

            // Registrar conexión en el grafo
            grafoConexiones.agregarEntidad(nuevo.getId());
            grafoConexiones.agregarConexion(
                    new Conexion(padre.getId(), nuevo.getId(), 0.8, "Subordinado directo"));

            System.out.printf("¡Soborno exitoso! %s se unió a la red por $%,d y %d influencia%n",
                    nuevo.getNombre(), costo, costoInfluencia);
            return nuevo;
        } else {
            // Fracaso: aumentar sospecha
            int aumentoSospecha = (int) (nivel.getPoder() * 3 * (1 - probabilidad));
            jugador.setNivelSospecha(Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + aumentoSospecha));

            System.out.printf("¡Soborno fallido! %s rechazó la oferta. +%d sospecha%n",
                    nuevo.getNombre(), aumentoSospecha);
            return null;
        }
    }

    /**
     * Calcula el costo de sobornar un político de cierto nivel
     */
    private int calcularCostoSoborno(NivelJerarquico nivel) {
        // Costo base modificado por nivel, reputación y sospecha
        double modificador = nivel.getPoder() * (1.0 + (jugador.getNivelSospecha() / 100.0) - (jugador.getReputacionPublica() / 200.0));
        return (int) (COSTO_BASE_SOBORNO * modificador);
    }

    /**
     * Calcula la probabilidad de éxito de un soborno
     */
    private double calcularProbabilidadExito(Politico padre, Politico objetivo) {
        double probabilidadBase = 0.7 - (objetivo.getNivelJerarquico().getPoder() * 0.1);

        // Modificadores
        double modLealtad = padre.getNivelLealtad() / 100.0;
        double modInfluencia = jugador.getInfluencia() / 200.0;
        double modSospecha = 1.0 - (jugador.getNivelSospecha() / 150.0);

        // Aplicar modificadores
        probabilidadBase *= modLealtad * modInfluencia * modSospecha;

        return Math.max(0.1, Math.min(0.9, probabilidadBase));
    }

    /**
     * Genera un político aleatorio con atributos basados en su nivel
     */
    private Politico generarPoliticoAleatorio(NivelJerarquico nivel) {
        Random rand = new Random();
        String id = String.format("%s-%03d", nivel.name().substring(0, 3).toLowerCase(), rand.nextInt(1000));
        String nombre = generarNombreAleatorio(nivel);

        return new Politico.Builder(id, nombre, nivel)
                .costoSoborno(calcularCostoSoborno(nivel))
                .aportePorInfluencia(5 + rand.nextInt(nivel.getPoder() * 5))
                .aportePorRiqueza(50 + rand.nextInt(nivel.getPoder() * 50))
                .nivelLealtad(30 + rand.nextInt(50))
                .nivelAmbicion(40 + rand.nextInt(50))
                .riesgoExposicion(20 + rand.nextInt(50))
                .estado(EstadoPolitico.ACTIVO)
                .aceptaSobornos(true)
                .build();
    }

    private String generarNombreAleatorio(NivelJerarquico nivel) {
        String[] nombres = {"Juan", "Pedro", "Luis", "Carlos", "Andrés", "Jorge", "Fernando"};
        String[] apellidos = {"Pérez", "Gómez", "Rodríguez", "López", "Martínez", "García"};

        String titulo = nivel.getTitulo();
        String nombre = nombres[new Random().nextInt(nombres.length)];
        String apellido = apellidos[new Random().nextInt(apellidos.length)];

        return String.format("%s %s %s", titulo, nombre, apellido);
    }

    // =========================================================================
    // SISTEMA DE LEALTAD Y AMBICIÓN
    // =========================================================================
    /**
     * Actualiza los estados de los políticos basado en su lealtad/ambición
     */
    private void actualizarEstadosPoliticos() {
        List<Politico> politicos = arbolCorrupcion.obtenerTodosLosPoliticos();

        for (Politico politico : politicos) {
            if (!politico.isActivo()) {
                continue;
            }

            // Posible aumento de ambición
            if (Math.random() < 0.2) {
                int aumentoAmbicion = 1 + (int) (politico.getNivelAmbicion() * 0.05);
                politico.modificarAmbicion(aumentoAmbicion);
            }

            // Posible disminución de lealtad
            if (Math.random() < 0.3) {
                int disminucionLealtad = 1 + (int) ((100 - politico.getNivelLealtad()) * 0.03);
                politico.modificarLealtad(-disminucionLealtad);
            }

            // Verificar traición
            verificarTraicion(politico);

            // Verificar aumento de riesgo por ambición
            if (politico.getNivelAmbicion() > 70 && Math.random() < 0.4) {
                int aumentoRiesgo = (int) ((politico.getNivelAmbicion() - 70) * 0.2);
                politico.modificarRiesgoExposicion(aumentoRiesgo);
            }
        }
    }

    /**
     * Verifica si un político traiciona al jugador
     */
    private void verificarTraicion(Politico politico) {
        if (politico.getNivelLealtad() > 30 || politico.getNivelAmbicion() < 80) {
            return; // No hay riesgo de traición
        }

        double probabilidadTraicion = (politico.getNivelAmbicion() - politico.getNivelLealtad()) / 200.0;
        if (Math.random() < probabilidadTraicion) {
            ejecutarTraicion(politico);
        }
    }

    /**
     * Ejecuta las consecuencias de una traición
     */
    private void ejecutarTraicion(Politico traidor) {
        // 50% de probabilidad de convertirse en testigo
        if (Math.random() < 0.5) {
            traidor.setEstado(EstadoPolitico.TESTIGO_PROTEGIDO);
            registro.registrarCambioEstado(traidor.getId(), EstadoPolitico.ACTIVO, EstadoPolitico.TESTIGO_PROTEGIDO);

            // Aumentar sospecha significativamente
            jugador.setNivelSospecha(Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + 20));

            System.out.printf("¡TRAICIÓN! %s se ha convertido en testigo protegido. +20 sospechan", traidor.getNombre());
        } else {
            // Robar parte de la red
            List<Politico> subordinados = traidor.getSubordinados();
            int cantidadRobada = 0;
            if (!subordinados.isEmpty()) {
                cantidadRobada = subordinados.size() / 2 + 1;
                for (int i = 0; i < cantidadRobada; i++) {
                    arbolCorrupcion.eliminarPolitico(subordinados.get(i).getId());
                }
            }

            arbolCorrupcion.eliminarPolitico(traidor.getId());
            System.out.printf("¡TRAICIÓN! %s ha robado %d subordinados y abandonado la red%n",
                    traidor.getNombre(), Math.min(subordinados.size(), cantidadRobada));
        }
    }

    // =========================================================================
    // SISTEMA DE EVENTOS E INVESTIGACIONES
    // =========================================================================
    /**
     * Procesa todos los eventos pendientes en la cola
     */
    private void procesarEventos() {
        List<Politico> politicos = arbolCorrupcion.obtenerTodosLosPoliticos();
        while (!colaEventos.estaVacia()) {
            EventoJuego evento = colaEventos.procesarSiguienteEvento();
            if (evento == null) {
                continue;
            }

            System.out.println("Procesando evento: " + evento.getDescripcion());

            switch (evento.getTipo()) {
                case MEGA_OPERACION_ANTICORRUPCION:
                    // Aumentar actividad de fiscales
                    for (Politico p : politicos) {
                        if (p.getRiesgoExposicion() > 50) {
                            p.modificarRiesgoExposicion(10);
                        }
                    }
                    jugador.setNivelSospecha(Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + 15));
                    break;

                case FILTRACION_PRENSA:
                    // Pérdida de reputación
                    jugador.setReputacionPublica(Math.max(0, jugador.getReputacionPublica() - 10));
                    jugador.setCapitalPolitico(Math.max(0, jugador.getCapitalPolitico() - 100));
                    break;

                case TRAICION_DOLOROSA:
                    // Ejecutar traición de un político aleatorio
                    Politico candidato = obtenerPoliticoConMayorRiesgoTraicion();
                    if (candidato != null) {
                        ejecutarTraicion(candidato);
                    }
                    break;

                case ELECCIONES_REGIONALES:
                    // Oportunidad de ganar influencia
                    jugador.setInfluencia(jugador.getInfluencia()+50);
                    break;

                case CAMBIO_FISCAL_GENERAL:
                    // Cambio aleatorio en sospecha
                    int cambio = new Random().nextBoolean() ? 10 : -10;
                    jugador.setNivelSospecha(Math.max(0, Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + cambio)));
                    break;

                case PRESION_INTERNACIONAL:
                    // Aumentar costos de soborno
                    for (Politico p : politicos) {
                        p.modificarRiesgoExposicion(5);
                    }
                    break;
            }
        }
    }

    /**
     * Genera eventos aleatorios basados en el estado del juego
     */
    private void generarEventosAleatorios() {
        Random rand = new Random();

        // Evento de mega operación (probabilidad aumenta con sospecha)
        if (rand.nextDouble() < jugador.getNivelSospecha() / 200.0) {
            EventoJuego evento = new EventoJuego(
                    "evt-mega-" + turnoActual,
                    TipoEvento.MEGA_OPERACION_ANTICORRUPCION,
                    "Mega operación anticorrupción en curso",
                    10
            );
            colaEventos.agregarEvento(evento);
        }

        // Evento de filtración (probabilidad aumenta con riesgo promedio)
        double riesgoPromedio = arbolCorrupcion.calcularRiesgoPromedio();
        if (rand.nextDouble() < riesgoPromedio / 150.0) {
            EventoJuego evento = new EventoJuego(
                    "evt-filt-" + turnoActual,
                    TipoEvento.FILTRACION_PRENSA,
                    "Información sensible filtrada a la prensa",
                    8
            );
            colaEventos.agregarEvento(evento);
        }
    }

    /**
     * Procesa las investigaciones de la IA de justicia
     */
    private void procesarInvestigaciones() {
        // Simular investigaciones basadas en riesgo de exposición
        List<Politico> politicos = arbolCorrupcion.obtenerTodosLosPoliticos();

        for (Politico politico : politicos) {
            if (!politico.isActivo()) {
                continue;
            }

            double probabilidadInvestigacion = politico.getRiesgoExposicion() / 100.0;
            if (Math.random() < probabilidadInvestigacion) {
                iniciarInvestigacion(politico);
            }
        }
    }

    /**
     * Inicia una investigación contra un político
     */
    private void iniciarInvestigacion(Politico politico) {
        // 30% de probabilidad de que la investigación progrese
        if (Math.random() < 0.3) {
            switch (politico.getEstado()) {
                case ACTIVO:
                    politico.setEstado(EstadoPolitico.BAJO_SOSPECHA);
                    registro.registrarCambioEstado(politico.getId(), EstadoPolitico.ACTIVO, EstadoPolitico.BAJO_SOSPECHA);
                    System.out.printf("%s está ahora BAJO SOSPECHA%n", politico.getNombre());
                    break;

                case BAJO_SOSPECHA:
                    politico.setEstado(EstadoPolitico.INVESTIGADO);
                    registro.registrarCambioEstado(politico.getId(), EstadoPolitico.BAJO_SOSPECHA, EstadoPolitico.INVESTIGADO);
                    System.out.printf("%s está ahora siendo INVESTIGADO%n", politico.getNombre());
                    break;

                case INVESTIGADO:
                    // 50% de probabilidad de ser encarcelado
                    if (Math.random() < 0.5) {
                        politico.setEstado(EstadoPolitico.ENCARCELADO);
                        registro.registrarCambioEstado(politico.getId(), EstadoPolitico.INVESTIGADO, EstadoPolitico.ENCARCELADO);
                        System.out.printf("%s ha sido ENCARCELADO%n", politico.getNombre());

                        // Eliminar del árbol pero mantener en registros
                        arbolCorrupcion.eliminarPolitico(politico.getId());
                    }
                    break;
            }

            // Aumentar sospecha general
            jugador.setNivelSospecha(Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + 2));
        }
    }

    /**
     * Neutraliza una amenaza (fiscal, periodista, etc.)
     */
    public boolean neutralizarAmenaza(String idAmenaza, int costoDinero, int costoInfluencia) {
        if (jugador.getDineroSucio() < costoDinero || jugador.getInfluencia() < costoInfluencia) {
            return false;
        }

        // Buscar en el grafo de conexiones
        Conexion amenaza = grafoConexiones.obtenerConexiones(idAmenaza).stream()
                .filter(c -> c.esNegativa())
                .findFirst()
                .orElse(null);

        if (amenaza != null) {
            // Intentar neutralizar
            double probabilidadExito = 0.7 - (jugador.getNivelSospecha()/ 200.0);

            if (Math.random() < probabilidadExito) {
                // Éxito: eliminar conexión negativa
                grafoConexiones.removerConexion(amenaza.getOrigen(), amenaza.getDestino());
                jugador.setDineroSucio(jugador.getDineroSucio()-costoDinero);
                jugador.setInfluencia(jugador.getInfluencia()-costoInfluencia);

                System.out.printf("¡Amenaza neutralizada! %s ya no es un riesgo%n", idAmenaza);
                return true;
            } else {
                // Fracaso: aumentar sospecha
                jugador.setNivelSospecha(Math.min(SOSPECHA_MAXIMA, jugador.getNivelSospecha() + 10));
                System.out.printf("Fallo al neutralizar %s. +10 sospecha%n", idAmenaza);
                return false;
            }
        }

        return false;
    }

    /**
     * Realiza una operación de encubrimiento para reducir riesgo/sospecha
     */
    public boolean operacionEncubrimiento(String idPolitico, int costoDinero) {
        Politico politico = arbolCorrupcion.buscarPolitico(idPolitico);
        if (politico == null || jugador.getDineroSucio() < costoDinero) {
            return false;
        }

        // Reducir riesgo de exposición
        int reduccionRiesgo = 15 + (int) (politico.getRiesgoExposicion() * 0.2);
        politico.modificarRiesgoExposicion(-reduccionRiesgo);

        // Reducir sospecha global
        int reduccionSospecha = 5 + (int) (jugador.getNivelSospecha()* 0.1);
        jugador.setNivelSospecha(Math.max(0, jugador.getNivelSospecha() - reduccionSospecha));

        jugador.setDineroSucio(jugador.getDineroSucio()-costoDinero);

        System.out.printf("Encubrimiento exitoso en %s. -%d riesgo, -%d sospecha%n",
                politico.getNombre(), reduccionRiesgo, reduccionSospecha);
        return true;
    }

    public boolean lavarDinero(int monto){
        if (monto>jugador.getDineroSucio()){
            System.out.println("No cuenta con suficientes fondos para lavar esa cantidad");
            return false;
        }
        Random rand = new Random();
        String id = String.format("%s-%03d","tra-", rand.nextInt(1000));
        Transaccion transaccion = new Transaccion(id,monto);

        jugador.setDineroSucio(jugador.getDineroSucio()-monto);

        if (Math.random() > transaccion.calcularRiesgoTransaccion(transaccion.monto,jugador.getNivelSospecha())) {
                jugador.setDineroLimpio((int) (jugador.getDineroLimpio() + (transaccion.monto*0.85))); // 15% "comisión"
                System.out.printf("La transacción %s fue realizada con éxito. +%f Fondos limpios disponibles",id,transaccion.monto*0.85);
            return true;
            } else {
                jugador.setNivelSospecha(jugador.getNivelSospecha()+10); // Falla en lavado
                System.out.println("La transacción ha fracasado, aumentando riesgos. +10 en nivel de sospecha");
            }
            return false;
    }

    public void habilidadContratarSicarios(String idAmenaza) {
        // Buscar en el grafo de conexiones
        Conexion amenaza = grafoConexiones.obtenerConexiones(idAmenaza).stream()
                .filter(c -> c.esNegativa())
                .findFirst()
                .orElse(null);

        if (amenaza != null) {
            // Intentar neutralizar
            grafoConexiones.removerConexion(amenaza.getOrigen(), amenaza.getDestino());
            System.out.printf("¡Amenaza neutralizada! %s ya no es un riesgo%n", idAmenaza);
        }
    }

    public boolean usarHabilidad(Politico politico, Constantes.HabilidadEspecial habilidad, String idObjetivo) {
        if (politico.getHabilidadesEspeciales().contains(habilidad)) {
            // Ejecutar efecto de la habilidad
            ejecutarHabilidad(habilidad, idObjetivo);

            // Eliminar del arsenal (solo un uso)
            politico.getHabilidadesEspeciales().remove(habilidad);
            return true;
        }
        return false;
    }

    public void ejecutarHabilidad (Constantes.HabilidadEspecial habilidad, String idObjetivo){
        switch (habilidad) {
            case BLOQUEAR_INVESTIGACION:
                // Reduce significativamente el nivel de sospecha
                jugador.setNivelSospecha(jugador.getNivelSospecha()-40);
                System.out.println("¡Investigación bloqueada! -40 puntos de sospecha");
                break;

            case COMPRA_VOTOS:
                // Aumenta la influencia inmediatamente
                jugador.setInfluencia(jugador.getInfluencia()+150);
                // Reduce ligeramente la reputación por fraude electoral
                jugador.setReputacionPublica(jugador.getReputacionPublica()-10);
                System.out.println("¡Votos comprados! +150 influencia, -10 reputación");
                break;

            case CONTRATACION_AMANADA:
                // Aumenta el dinero sucio a través de contratos fraudulentos
                double contratoFraudulento = 1000 + (Math.random() * 5000);
                jugador.setDineroSucio(jugador.getDineroSucio()+(int)contratoFraudulento);
                System.out.printf("¡Contratación amañada! +$%.2f dinero sucio%n", contratoFraudulento);
                break;

            case MANIPULACION_MEDIATICA:
                // Mejora la reputación pública
                jugador.setReputacionPublica(jugador.getReputacionPublica()+25);
                // Reduce temporalmente el riesgo de exposición
                jugador.setNivelSospecha(jugador.getNivelSospecha()-15);
                System.out.println("¡Medios manipulados! +25 reputación, -15 nivel de sospecha");
                break;

            case CONTRATO_SICARIOS:
                // Elimina un nodo enemigo aleatorio (ej: fiscal o periodista)
                habilidadContratarSicarios(idObjetivo);

            case LAVADO_DINERO:
                // Lavado instantáneo de todo el dinero sucio sin comisión
                double monto = jugador.getDineroSucio();
                jugador.setDineroSucio(0);
                jugador.setDineroLimpio(jugador.getDineroLimpio()+(int)monto);
                System.out.printf("¡Lavado instantáneo! Convertidos $%.2f a dinero limpio%n", monto);
                break;

            case FALSIFICACION_PRUEBAS:
                // Reduce la evidencia acumulada contra el jugador
                sistemaJusticia.limpiarEvidencia();
                jugador.setNivelSospecha(jugador.getNivelSospecha()-15);
                System.out.println("¡Pruebas falsificadas! -15 sospecha");
                break;

            default:
                System.out.println("Habilidad desconocida: " + habilidad.getDescripcion());
                break;
        }
    }


    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
    private Politico obtenerPoliticoConMayorRiesgoTraicion() {
        return arbolCorrupcion.obtenerTodosLosPoliticos().stream()
                .filter(Politico::isActivo)
                .max(Comparator.comparingInt(p -> p.getNivelAmbicion() - p.getNivelLealtad()))
                .orElse(null);
    }

    // =========================================================================
    // GETTERS PARA EL ESTADO DEL JUEGO
    // =========================================================================
    public int getDineroSucio() {
        return jugador.getDineroSucio();
    }

    public int getDineroLimpio()
    {
        return jugador.getDineroLimpio();
    }

    public int getInfluencia() {
        return jugador.getInfluencia();
    }

    public int getCapitalPolitico() {
        return jugador.getCapitalPolitico();
    }

    public int getNivelSospecha() {
        return jugador.getNivelSospecha();
    }

    public int getReputacionPublica() {
        return jugador.getInfluencia();
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public ArbolCorrupcion getArbolCorrupcion() {
        return arbolCorrupcion;
    }

    public GrafoConexiones getGrafoConexiones() {
        return grafoConexiones;
    }

    public ColaEventos getColaEventos() {
        return colaEventos;
    }

    public RegistroEspecializado getRegistro() {
        return registro;
    }

    public PilaDeshacer getPilaDeshacer() {
        return pilaDeshacer;
    }

    public SistemaJusticia getSistemaJusticia() {
        return sistemaJusticia;
    }

    public EventoDinamico getEventoDinamico() {
        return eventoDinamico;
    }
}
